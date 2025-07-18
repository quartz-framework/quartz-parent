package xyz.quartzframework.bungee.command;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import xyz.quartzframework.Injectable;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.bungee.session.BungeeSession;
import xyz.quartzframework.cli.CommandExecutor;
import xyz.quartzframework.cli.CommandService;
import xyz.quartzframework.cli.picocli.CommandLineDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@NoProxy
@Injectable
@RequiredArgsConstructor
public class BungeeCommandService implements CommandService {

    private static final String DEFAULT_COMMAND_NAME = "<main class>";

    private final CommandLineDefinition commandLineDefinition;

    private final BungeeSession session;

    private final Plugin plugin;

    private final PluginManager pluginManager;

    private final CommandExecutor commandExecutor;

    private final QuartzBeanFactory quartzBeanFactory;

    @Getter
    private boolean registered;

    @PostConstruct
    public void init() {
        try {
            List<CommandSpec> commandSpecs = getCommands();
            commandSpecs.forEach(this::registerCommand);
            log.info("Successfully registered {} commands", commandSpecs.size());
            registered = true;
        } catch (Throwable t) {
            log.warn("Failed to register commands natively, falling back to event listeners", t);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            List<CommandSpec> commandSpecs = getCommands();
            commandSpecs.forEach(this::unregisterCommand);
        } catch (Throwable t) {
            log.warn("Failed to unregister commands natively", t);
        }
    }

    @Override
    @SneakyThrows
    public void registerCommand(CommandSpec commandSpec) {
        val command = new WrappedBungeeCommand(commandSpec, session, commandExecutor);
        pluginManager.registerCommand(plugin, command);
    }

    @Override
    @SneakyThrows
    public void unregisterCommand(CommandSpec commandSpec) {
        val commandName = commandSpec.name();
        pluginManager
                .getCommands()
                .stream()
                .filter(c -> c.getKey().equals(commandName))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(pluginManager::unregisterCommand);
    }

    @Override
    public List<CommandSpec> getCommands() {
        val commandLine = commandLineDefinition.build(quartzBeanFactory);
        val commandSpec = commandLine.getCommandSpec();
        if (DEFAULT_COMMAND_NAME.equals(commandSpec.name())) {
            return commandSpec.subcommands().values().stream()
                    .map(CommandLine::getCommandSpec)
                    .filter(distinctByKey(CommandSpec::name))
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(commandSpec);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}