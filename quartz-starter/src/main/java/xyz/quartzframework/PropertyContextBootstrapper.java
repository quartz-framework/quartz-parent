package xyz.quartzframework;

import org.springframework.core.convert.ConversionService;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.config.DefaultPropertyPostProcessor;
import xyz.quartzframework.config.DefaultPropertySourceFactory;
import xyz.quartzframework.config.PropertyPostProcessor;
import xyz.quartzframework.config.PropertySourceFactory;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
public class PropertyContextBootstrapper {

    @Provide
    @ActivateWhenBeanMissing(PropertySourceFactory.class)
    PropertySourceFactory propertySourceFactory(Quartz<?> plugin) {
        return new DefaultPropertySourceFactory(plugin);
    }

    @Provide
    @ActivateWhenBeanMissing(PropertyPostProcessor.class)
    PropertyPostProcessor propertyPostProcessor(PropertySourceFactory propertySourceFactory, ConversionService conversionService) {
        return new DefaultPropertyPostProcessor(propertySourceFactory, conversionService);
    }
}