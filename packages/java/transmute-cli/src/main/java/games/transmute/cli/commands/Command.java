package games.transmute.cli.commands;

/**
 * Interface for CLI commands.
 */
public interface Command {
    
    /**
     * Execute the command.
     * 
     * @param args Command arguments (excluding the command name itself)
     */
    void execute(String[] args);
    
    /**
     * Get the command name.
     * 
     * @return The command name
     */
    String getName();
    
    /**
     * Get command aliases.
     * 
     * @return Array of command aliases
     */
    default String[] getAliases() {
        return new String[0];
    }
    
    /**
     * Get a short description of the command.
     * 
     * @return Command description
     */
    String getDescription();
}
