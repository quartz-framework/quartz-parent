package xyz.quartzframework.bungee.session;

import net.md_5.bungee.api.ProxyServer;
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
    BungeeSession session(ProxyServer server) {
        return new BungeeSession(server);
    }
}
