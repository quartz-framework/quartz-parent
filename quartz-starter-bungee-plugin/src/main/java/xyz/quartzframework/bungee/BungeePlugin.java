package xyz.quartzframework.bungee;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.context.QuartzContext;

import java.util.Objects;

@NoProxy
public abstract class BungeePlugin extends Plugin implements Quartz<Plugin> {

    @Getter
    @Setter
    private QuartzContext<Plugin> context;

    public static BungeeQuartzBuilder builder(BungeePlugin plugin) {
        return new BungeeQuartzBuilder(plugin, plugin.getClass());
    }

    @Override
    public final void onLoad() {

    }

    @Override
    public final void onEnable() {
        main();
    }

    @Override
    public final void onDisable() {
        close();
    }

    @Override
    public void close() {
        val context = getContext();
        if (Objects.isNull(context)) return;
        context.close();
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public String getName() {
        return getDescription().getName();
    }
}