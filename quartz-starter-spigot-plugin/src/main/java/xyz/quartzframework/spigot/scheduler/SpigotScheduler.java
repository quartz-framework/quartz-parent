package xyz.quartzframework.spigot.scheduler;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.quartzframework.Inject;
import xyz.quartzframework.Injectable;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanPresent;
import xyz.quartzframework.scheduler.Scheduler;
import xyz.quartzframework.spigot.session.SpigotSession;

@Injectable
@ActivateWhenBeanPresent(BukkitScheduler.class)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SpigotScheduler implements Scheduler {

    private final SpigotSession session;

    private final Plugin plugin;

    private final BukkitScheduler scheduler;

    @Override
    public int scheduleSyncDelayedTask(Runnable task, long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, session.wrap(task), delay);
    }

    @Override
    public int scheduleSyncDelayedTask(Runnable task) {
        return scheduler.scheduleSyncDelayedTask(plugin, session.wrap(task));
    }

    @Override
    public int scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, session.wrap(task), delay, period);
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(int taskId) {
        return scheduler.isQueued(taskId);
    }
}