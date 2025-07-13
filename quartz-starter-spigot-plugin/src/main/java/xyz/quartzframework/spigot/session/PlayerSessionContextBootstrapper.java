package xyz.quartzframework.spigot.session;

import org.bukkit.Server;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.session.SenderSession;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
public class PlayerSessionContextBootstrapper {

    @Provide
    @Preferred
    @ActivateWhenBeanMissing(SenderSession.class)
    SpigotSession session(Server server) {
        return new SpigotSession(server);
    }
}
