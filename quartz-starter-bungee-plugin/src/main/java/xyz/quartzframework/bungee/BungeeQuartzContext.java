package xyz.quartzframework.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.context.AbstractQuartzContext;

class BungeeQuartzContext extends AbstractQuartzContext<Plugin> {

    public BungeeQuartzContext(Class<? extends Quartz<Plugin>> pluginClass) {
        super(pluginClass, null, null, null, null, null);
    }
}