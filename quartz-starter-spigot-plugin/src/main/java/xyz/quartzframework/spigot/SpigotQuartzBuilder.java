package xyz.quartzframework.spigot;

import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionBuilder;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionRegistry;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.strategy.BeanNameStrategy;
import xyz.quartzframework.context.QuartzContextBuilder;

import java.net.URLClassLoader;

public final class SpigotQuartzBuilder extends QuartzContextBuilder<JavaPlugin, SpigotQuartzContext> {

    SpigotQuartzBuilder(Quartz<JavaPlugin> plugin, Class<? extends Quartz<JavaPlugin>> pluginClass) {
        super(pluginClass, plugin);
    }

    public void build() {
        val context = new SpigotQuartzContext(getPluginClass());
        run(context);
    }

    @Override
    public SpigotQuartzBuilder beanFactory(QuartzBeanFactory factory) {
        super.beanFactory(factory);
        return this;
    }

    @Override
    public SpigotQuartzBuilder beanDefinitionRegistry(QuartzBeanDefinitionRegistry registry) {
        super.beanDefinitionRegistry(registry);
        return this;
    }

    @Override
    public SpigotQuartzBuilder beanDefinitionBuilder(QuartzBeanDefinitionBuilder builder) {
        super.beanDefinitionBuilder(builder);
        return this;
    }

    @Override
    public SpigotQuartzBuilder classLoader(URLClassLoader classLoader) {
        super.classLoader(classLoader);
        return this;
    }

    @Override
    public SpigotQuartzBuilder beanNameStrategy(BeanNameStrategy strategy) {
        super.beanNameStrategy(strategy);
        return this;
    }
}