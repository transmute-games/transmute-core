package games.transmute.cli.commands;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for CLI commands.
 */
public class CommandRegistry {
    
    private final Map<String, Command> commands = new HashMap<>();
    
    public CommandRegistry() {
        // Register all commands
        register(new NewCommand());
        register(new HelpCommand());
        register(new VersionCommand());
        register(new TemplatesCommand());
    }
    
    /**
     * Register a command and its aliases.
     */
    private void register(Command command) {
        commands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias, command);
        }
    }
    
    /**
     * Get a command by name or alias.
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }
    
    /**
     * Check if a command exists.
     */
    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }
}
