package xyz.quartzframework.data.properties;

import lombok.Getter;
import xyz.quartzframework.Injectable;
import xyz.quartzframework.config.Property;

@Getter
@Injectable
public class HibernateProperties {

    @Property("${quartz.data.hibernate.dialect:}")
    private String dialect;

    @Property("${quartz.data.hibernate.ddl-auto:none}")
    private String ddlAuto;

    @Property("${quartz.data.hibernate.show-sql:false}")
    private boolean showSql;

    @Property("${quartz.data.hibernate.format-sql:false}")
    private boolean formatSql;

    @Property("${quartz.data.hibernate.highlight-sql:false}")
    private boolean highlightSql;

    @Property("${quartz.data.hibernate.log-slow-query:0}")
    private Long logSlowQuery;

    @Property("${quartz.data.hibernate.use-sql-comments:false}")
    private boolean useSqlComments;

    @Property("${quartz.data.hibernate.fetch-size:0}")
    private int fetchSize;

    @Property("${quartz.data.hibernate.use-scrollable-resultset:true}")
    private boolean useScrollableResultSet;

    @Property("${quartz.data.hibernate.lob.non-contextual-creation:true}")
    private boolean nonContextualLobCreation;

    @Property("${quartz.data.hibernate.log-jdbc-warnings:true}")
    private boolean logJdbcWarnings;

    @Property("${quartz.data.hibernate.jdbc.time-zone:}")
    private String jdbcTimeZone;

    @Property("${quartz.data.hibernate.use-get-generated-keys:true}")
    private boolean useGetGeneratedKeys;

    @Property("${quartz.data.hibernate.connection-handling:}")
    private String connectionHandling;

    @Property("${quartz.data.hibernate.statement-inspector:}")
    private String statementInspector;

    @Property("${quartz.data.hibernate.validation-mode:AUTO}")
    private String validationMode;

    @Property("${quartz.data.hibernate.shared-cache-mode:NONE}")
    private String sharedCacheMode;

    @Property("${quartz.data.hibernate.exclude-unlisted-classes:false}")
    private boolean excludeUnlistedClasses;

    @Property("${quartz.data.hibernate.dialect.native-param-markers:true}")
    private boolean dialectNativeParamMarkers;
}