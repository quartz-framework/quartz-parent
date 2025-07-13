package xyz.quartzframework.bungee;

import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionBuilder;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionRegistry;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.strategy.BeanNameStrategy;
import xyz.quartzframework.context.QuartzContextBuilder;

import java.net.URLClassLoader;

public final class BungeeQuartzBuilder extends QuartzContextBuilder<Plugin, BungeeQuartzContext> {

    BungeeQuartzBuilder(Quartz<Plugin> plugin, Class<? extends Quartz<Plugin>> pluginClass) {
        super(pluginClass, plugin);
    }

    public void build() {
        val context = new BungeeQuartzContext(getPluginClass());
        run(context);
    }

    @Override
    public BungeeQuartzBuilder beanFactory(QuartzBeanFactory factory) {
        super.beanFactory(factory);
        return this;
    }

    @Override
    public BungeeQuartzBuilder beanDefinitionRegistry(QuartzBeanDefinitionRegistry registry) {
        super.beanDefinitionRegistry(registry);
        return this;
    }

    @Override
    public BungeeQuartzBuilder beanDefinitionBuilder(QuartzBeanDefinitionBuilder builder) {
        super.beanDefinitionBuilder(builder);
        return this;
    }

    @Override
    public BungeeQuartzBuilder classLoader(URLClassLoader classLoader) {
        super.classLoader(classLoader);
        return this;
    }

    @Override
    public BungeeQuartzBuilder beanNameStrategy(BeanNameStrategy strategy) {
        super.beanNameStrategy(strategy);
        return this;
    }
}