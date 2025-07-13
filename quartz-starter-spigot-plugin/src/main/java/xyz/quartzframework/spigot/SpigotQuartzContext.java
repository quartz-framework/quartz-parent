package xyz.quartzframework.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.context.AbstractQuartzContext;

class SpigotQuartzContext extends AbstractQuartzContext<JavaPlugin> {

    public SpigotQuartzContext(Class<? extends Quartz<JavaPlugin>> pluginClass) {
        super(pluginClass, null, null, null, null,null);
    }
}