package xyz.quartzframework.support.beans;

import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.strategy.BeanNameStrategy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.context.QuartzContext;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
public class BeanNameStrategyContextBootstrapper {

    @Provide
    @Preferred
    BeanNameStrategy beanDefinitionRegistry(QuartzContext<?> context) {
        return context.getBeanNameStrategy();
    }
}