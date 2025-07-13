package xyz.quartzframework.data.storage;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import xyz.quartzframework.Injectable;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.data.query.JPAQueryExecutor;
import xyz.quartzframework.data.query.QueryExecutor;

@Injectable
@RequiredArgsConstructor
@ActivateWhenBeanMissing(JPAStorageProvider.class)
public class JPAStorageProvider implements StorageProvider {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public <E, ID> HibernateJPAStorage<E, ID> create(Class<E> entity, Class<ID> id) {
        return new HibernateJPAStorage<>(entityManagerFactory, entity, id);
    }

    @Override
    public <E, ID> QueryExecutor<E> getQueryExecutor(SimpleStorage<E, ID> storage) {
        return new JPAQueryExecutor<>(entityManagerFactory, storage.getEntityClass());
    }
}