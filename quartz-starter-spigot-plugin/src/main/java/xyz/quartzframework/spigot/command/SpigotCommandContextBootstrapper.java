package xyz.quartzframework.spigot.command;

import lombok.RequiredArgsConstructor;
import xyz.quartzframework.Inject;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.cli.CommandExecutor;
import xyz.quartzframework.cli.picocli.CommandLineDefinition;
import xyz.quartzframework.ordered.Priority;
import xyz.quartzframework.spigot.session.SpigotSession;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SpigotCommandContextBootstrapper {

    private final QuartzBeanFactory quartzBeanFactory;

    @Provide
    @Priority(0)
    @ActivateWhenBeanMissing(CommandExecutor.class)
    CommandExecutor commandExecutor(CommandLineDefinition commandLineDefinition) {
        return new SpigotCommandExecutor(quartzBeanFactory, commandLineDefinition);
    }

    @Provide
    @Priority(1)
    @Preferred
    CommandInterceptor commandInterceptor(SpigotSession session, CommandExecutor executor, SpigotCommandService service) {
        return new CommandInterceptor(session, executor, service);
    }
}
