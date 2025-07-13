package xyz.quartzframework;

import lombok.val;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.stereotype.ContextBootstrapper;

import java.util.Map;

@NoProxy
@ContextBootstrapper
public class ConvertContextBootstrapper {

    @Provide
    @ActivateWhenBeanMissing(ConversionService.class)
    ConversionService conversionService() {
        val service = new DefaultConversionService();
        service.addConverter(ConfigurationSection.class, Map.class, (s) -> s.getValues(true));
        return service;
    }
}