package xyz.quartzframework.data.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import xyz.quartzframework.common.Pair;
import xyz.quartzframework.data.page.Page;
import xyz.quartzframework.data.page.Pagination;
import xyz.quartzframework.data.util.ParameterBindingUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class JPAQueryExecutor<E> implements QueryExecutor<E> {

    private final EntityManagerFactory entityManagerFactory;
    private final Class<E> entityClass;

    @Override
    public <R> List<R> find(DynamicQueryDefinition query, Object[] args) {
        try (val em = entityManagerFactory.createEntityManager()) {
            if (query.raw() != null) {
                return executeRawQuery(em, query, args);
            }
            val jpaQuery = buildJpaQuery(em, query, args);
            if (query.limit() != null) jpaQuery.setMaxResults(query.limit());
            return (List<R>) jpaQuery.getResultList();
        }
    }

    @Override
    public <R> Page<R> find(DynamicQueryDefinition query, Object[] args, Pagination pagination) {
        try (val em = entityManagerFactory.createEntityManager()) {
            if (query.raw() != null) {
                val typedQuery = buildRawQuery(em, query, args);
                typedQuery.setFirstResult(pagination.offset());
                typedQuery.setMaxResults(pagination.size());
                List<R> items = (List<R>) typedQuery.getResultList();
                long total = count(query, args);
                return Page.of(items, pagination, (int) total);
            }
            val jpaQuery = buildJpaQuery(em, query, args);
            jpaQuery.setFirstResult(pagination.offset());
            jpaQuery.setMaxResults(pagination.size());
            List<R> items = (List<R>) jpaQuery.getResultList();
            long total = count(query, args);
            return Page.of(items, pagination, (int) total);
        }
    }

    @Override
    public long count(DynamicQueryDefinition query, Object[] args) {
        try (val em = entityManagerFactory.createEntityManager()) {
            if (query.raw() != null) {
                val rawQuery = buildRawCountQuery(em, query, args);
                val result = rawQuery.getResultList();
                if (result.isEmpty()) return 0;
                Object first = result.get(0);
                if (first instanceof Object[] arr && arr.length == 1 && arr[0] instanceof Number n) {
                    return n.longValue();
                }
                if (first instanceof Number n) {
                    return n.longValue();
                }
                throw new IllegalStateException("Expected count query to return a number, got: " + first);
            }
            val cb = em.getCriteriaBuilder();
            val cq = cb.createQuery(Long.class);
            val root = cq.from(entityClass);
            cq.select(query.distinct() ? cb.countDistinct(root) : cb.count(root)).where(buildPredicates(query, cb, root, query.queryConditions(), args));
            return em.createQuery(cq).getSingleResult();
        }
    }

    @Override
    public boolean exists(DynamicQueryDefinition query, Object[] args) {
        try (val em = entityManagerFactory.createEntityManager()) {
            if (query.raw() != null) {
                val q = buildRawQuery(em, query, args);
                Object result = q.setMaxResults(1).getSingleResult();
                if (result instanceof Boolean b) return b;
                if (result instanceof Number n) return n.longValue() > 0;
                throw new IllegalStateException("Expected exists query to return boolean or number, got: " + result);
            }
            return count(query, args) > 0;
        }
    }

    private <R> List<R> executeRawQuery(EntityManager em, DynamicQueryDefinition query, Object[] args) {
        val typedQuery = buildRawQuery(em, query, args);
        if (query.limit() != null && query.raw() != null && !query.raw().toLowerCase().contains("limit")) {
            typedQuery.setMaxResults(query.limit());
        }
        return (List<R>) typedQuery.getResultList();
    }

    private <R> TypedQuery<R> buildRawQuery(EntityManager em, DynamicQueryDefinition query, Object[] args) {
        TypedQuery<?> typedQuery;

        if (query.nativeSQL()) {
            if (query.returnType() == Object[].class) {
                typedQuery = (TypedQuery<?>) em.createNativeQuery(query.raw());
            } else {
                typedQuery = (TypedQuery<?>) em.createNativeQuery(query.raw(), query.returnType());
            }
        } else {
            if (query.returnType() == Object[].class) {
                typedQuery = (TypedQuery<?>) em.createQuery(query.raw());
            } else {
                typedQuery = em.createQuery(query.raw(), query.returnType());
            }
        }
        bindParameters(typedQuery, query, args);
        return (TypedQuery<R>) typedQuery;
    }

    private jakarta.persistence.Query buildRawCountQuery(EntityManager em, DynamicQueryDefinition query, Object[] args) {
        val rawQuery = query.nativeSQL()
                ? em.createNativeQuery(query.raw(), Long.class)
                : em.createQuery(query.raw(), Long.class);
        bindParameters(rawQuery, query, args);
        return rawQuery;
    }

    private TypedQuery<E> buildJpaQuery(EntityManager em, DynamicQueryDefinition query, Object[] args) {
        val cb = em.getCriteriaBuilder();
        val cq = cb.createQuery(entityClass);
        val root = cq.from(entityClass);

        cq.where(buildPredicates(query, cb, root, query.queryConditions(), args));

        if (!query.orders().isEmpty()) {
            cq.orderBy(buildOrders(cb, root, query.orders()));
        }

        if (query.distinct()) {
            cq.distinct(true);
        }

        return em.createQuery(cq);
    }

    private Predicate[] buildPredicates(DynamicQueryDefinition queryDefinition, CriteriaBuilder cb, Root<E> root, List<QueryCondition> conditions, Object[] args) {
        return conditions.stream()
                .map(cond -> buildPredicate(queryDefinition, cb, root, cond, args))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
    }

    private List<jakarta.persistence.criteria.Order> buildOrders(CriteriaBuilder cb, Root<E> root, List<Order> orders) {
        return orders.stream()
                .map(order -> order.descending()
                        ? cb.desc(root.get(order.property()))
                        : cb.asc(root.get(order.property())))
                .toList();
    }

    private void bindParameters(jakarta.persistence.Query query, DynamicQueryDefinition def, Object[] args) {
        for (QuerySubstitution sub : def.querySubstitutions()) {
            Object value;

            if (sub.isLiteral()) {
                value = sub.getLiteralValue();
            } else if (sub.isNamed()) {
                String name = sub.getNameOrIndex();
                if (name == null || name.isBlank()) {
                    throw new IllegalArgumentException("Named parameter substitution is missing a name: " + sub);
                }
                value = ParameterBindingUtil.findNamedParameter(def.method(), name, args);
                query.setParameter(name, value);
            } else {
                String key = sub.getNameOrIndex();
                if (key == null || key.isBlank()) {
                    throw new IllegalArgumentException("Positional parameter substitution is missing an index: " + sub);
                }

                int index;
                try {
                    index = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid positional parameter index: ?" + key, e);
                }

                if (index < 0 || index >= args.length) {
                    throw new IllegalArgumentException("Missing argument for positional index: ?" + index);
                }

                value = args[index];
                query.setParameter(index + 1, value);
            }
        }
    }

    private Predicate buildPredicate(DynamicQueryDefinition queryDefinition, CriteriaBuilder cb, Root<E> root, QueryCondition cond, Object[] args) {
        Object value;

        int index = queryDefinition.queryConditions().indexOf(cond);
        if (index == -1 || index >= queryDefinition.querySubstitutions().size()) {
            throw new IllegalStateException("No substitution found for condition: " + cond.getRawCondition());
        }

        val substitution = queryDefinition.querySubstitutions().get(index);

        if (substitution.isLiteral()) {
            value = substitution.getLiteralValue();
        } else if (substitution.isNamed()) {
            String name = substitution.getNameOrIndex();
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Named parameter missing name: " + substitution);
            }
            value = ParameterBindingUtil.findNamedParameter(queryDefinition.method(), name, args);
        } else if (substitution.isPositional()) {
            String key = substitution.getNameOrIndex();
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("Positional parameter missing index: " + substitution);
            }
            int paramIndex;
            try {
                paramIndex = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid positional index: ?" + key, e);
            }
            if (paramIndex < 0 || paramIndex >= args.length) {
                throw new IllegalArgumentException("Missing argument at index: ?" + paramIndex);
            }
            value = args[paramIndex];
        } else {
            throw new IllegalStateException("Unrecognized substitution: " + substitution);
        }

        Path<?> path = resolvePath(root, cond.getAttribute().name(), queryDefinition.raw());

        return switch (cond.getOperation()) {
            case EQUAL -> cb.equal(path, value);
            case NOT_EQUAL -> cb.notEqual(path, value);
            case LIKE -> cb.like(path.as(String.class), String.valueOf(value));
            case NOT_LIKE -> cb.notLike(path.as(String.class), String.valueOf(value));
            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
            case IN -> {
                if (!(value instanceof Collection<?> col)) {
                    throw new IllegalArgumentException("IN expects Collection, got: " + value);
                }
                yield path.in(col);
            }
            case NOT_IN -> {
                if (!(value instanceof Collection<?> col)) {
                    throw new IllegalArgumentException("NOT IN expects Collection, got: " + value);
                }
                yield cb.not(path.in(col));
            }
            case GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL -> {
                if (!(value instanceof Comparable<?> cmp)) {
                    throw new IllegalArgumentException("Comparison expects Comparable, got: " + value);
                }
                Path<? extends Comparable<Object>> cmpPath = (Path<? extends Comparable<Object>>) path;
                Comparable<Object> cmpVal = (Comparable<Object>) cmp;
                yield switch (cond.getOperation()) {
                    case GREATER_THAN -> cb.greaterThan(cmpPath, cmpVal);
                    case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(cmpPath, cmpVal);
                    case LESS_THAN -> cb.lessThan(cmpPath, cmpVal);
                    case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(cmpPath, cmpVal);
                    default -> throw new IllegalArgumentException("Invalid op: " + cond.getOperation());
                };
            }
        };
    }

    private Path<Object> resolvePath(From<?, ?> root, String propertyPath, String rawQuery) {
        String[] parts = propertyPath.split("\\.");
        if (parts.length == 1) {
            return root.get(parts[0]);
        }
        if (rawQuery == null) {
            Path<?> path = root;
            for (String part : parts) {
                path = path.get(part);
            }
            return (Path<Object>) path;
        }
        Map<String, Pair<String, JoinType>> aliasMap = extractAliases(rawQuery);
        String alias = parts[0];
        String property = parts[1];
        if (!aliasMap.containsKey(alias)) {
            throw new IllegalArgumentException("Unknown alias in query: " + alias);
        }
        Pair<String, JoinType> joinPath = aliasMap.get(alias);
        if (joinPath.getFirst().isEmpty()) {
            return root.get(property);
        }
        String[] joinParts = joinPath.getFirst().split("\\.");
        From<?, ?> current = root;
        for (int i = 1; i < joinParts.length; i++) {
            current = current.join(joinParts[i], joinPath.getSecond());
        }
        return current.get(property);
    }

    private Map<String, Pair<String, JoinType>> extractAliases(String rawQuery) {
        Map<String, Pair<String, JoinType>> aliasMap = new HashMap<>();
        Matcher fromMatcher = Pattern.compile(
                "(?i)from\\s+(\\w+(?:\\.\\w+)?)(?:\\s+as)?\\s+(\\w+)"
        ).matcher(rawQuery);
        if (fromMatcher.find()) {
            String alias = fromMatcher.group(2);
            aliasMap.put(alias, Pair.of("", JoinType.INNER));
        }
        Matcher joinMatcher = Pattern.compile(
                "(?i)(left|inner|right|full)?\\s*join\\s+(\\w+(?:\\.\\w+)*)\\s+(?:as\\s+)?(\\w+)"
        ).matcher(rawQuery);
        while (joinMatcher.find()) {
            String joinTypeRaw = joinMatcher.group(1);
            String path = joinMatcher.group(2);
            String alias = joinMatcher.group(3);
            JoinType joinType = switch (joinTypeRaw == null ? "" : joinTypeRaw.toLowerCase()) {
                case "left" -> JoinType.LEFT;
                case "right" -> JoinType.RIGHT;
                case "inner" -> JoinType.INNER;
                case "full" -> JoinType.LEFT;
                default -> JoinType.INNER;
            };
            aliasMap.put(alias, Pair.of(path, joinType));
        }
        return aliasMap;
    }
}