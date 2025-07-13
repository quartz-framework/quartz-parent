package xyz.quartzframework.support.beans;

import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionBuilder;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionRegistry;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.context.QuartzContext;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
public class BeanDefinitionRegistryContextBootstrapper {

    @Provide
    @Preferred
    QuartzBeanDefinitionRegistry beanDefinitionRegistry(QuartzContext<?> context) {
        return context.getBeanDefinitionRegistry();
    }

    @Provide
    @Preferred
    QuartzBeanDefinitionBuilder beanDefinitionBuilder(QuartzContext<?> context) {
        return context.getBeanDefinitionBuilder();
    }
}