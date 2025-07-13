package xyz.quartzframework.support.flyway;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.core.io.ResourceLoader;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanPresent;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenClassPresent;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenPropertyEquals;
import xyz.quartzframework.config.Property;
import xyz.quartzframework.stereotype.Configurer;

import javax.sql.DataSource;

@Slf4j
@ActivateWhenClassPresent(classNames = "org.flywaydb.core.Flyway")
@Configurer(force = true)
public class FlywayConfigurer {

    @Provide
    @Preferred
    @ActivateWhenBeanPresent(DataSource.class)
    @ActivateWhenPropertyEquals(expression = "${quartz.flyway.enabled:false}", expected = "true")
    public Flyway flyway(ResourceLoader resourceLoader, DataSource dataSource, FlywayProperties properties) {
        log.info("Enabling Flyway support...");
        Flyway flyway = Flyway.configure(resourceLoader.getClassLoader())
                .dataSource(dataSource)
                .locations(properties.getLocations())
                .table(properties.getTable())
                .schemas(properties.getSchemas())
                .baselineDescription(properties.getBaselineDescription() == null ? "" : properties.getBaselineDescription())
                .baselineOnMigrate(properties.isBaselineOnMigrate())
                .baselineVersion(properties.getBaselineVersion())
                .cleanDisabled(properties.isCleanDisabled())
                .validateOnMigrate(properties.isValidateOnMigrate())
                .outOfOrder(properties.isOutOfOrder())
                .load();
        flyway.migrate();
        return flyway;
    }
}