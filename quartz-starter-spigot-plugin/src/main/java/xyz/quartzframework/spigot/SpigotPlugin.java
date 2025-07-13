package xyz.quartzframework.spigot;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.quartzframework.Quartz;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.context.AbstractQuartzContext;

import java.util.Objects;

@NoProxy
public abstract class SpigotPlugin extends JavaPlugin implements Quartz<JavaPlugin> {

    @Getter
    @Setter
    private AbstractQuartzContext<JavaPlugin> context;

    public static SpigotQuartzBuilder builder(SpigotPlugin plugin) {
        return new SpigotQuartzBuilder(plugin, plugin.getClass());
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
    public JavaPlugin getPlugin() {
        return this;
    }

}