package xyz.quartzframework;

import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.context.QuartzContext;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
public class BeanFactoryContextBootstrapper {

    @Provide
    @Preferred
    QuartzBeanFactory beanFactory(QuartzContext<?> context) {
        return context.getBeanFactory();
    }
}