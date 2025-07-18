package xyz.quartzframework.support.flyway;

import lombok.Getter;
import xyz.quartzframework.Injectable;
import xyz.quartzframework.config.Property;

@Getter
@Injectable
public class FlywayProperties {

    @Property("${quartz.flyway.locations:classpath:db/migrations}")
    private String[] locations;

    @Property("${quartz.flyway.schemas:}")
    private String[] schemas;

    @Property("${quartz.flyway.table:flyway_schema_history}")
    private String table;

    @Property("${quartz.flyway.enabled:false}")
    private boolean enabled;

    @Property("${quartz.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    @Property("${quartz.flyway.baseline-version:1}")
    private String baselineVersion;

    @Property("${quartz.flyway.baseline-description:}")
    private String baselineDescription;

    @Property("${quartz.flyway.clean-disabled:true}")
    private boolean cleanDisabled;

    @Property("${quartz.flyway.validate-on-migrate:true}")
    private boolean validateOnMigrate;

    @Property("${quartz.flyway.out-of-order:false}")
    private boolean outOfOrder;
}