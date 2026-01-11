package games.transmute.cli;

import games.transmute.cli.commands.Command;
import games.transmute.cli.commands.CommandRegistry;
import games.transmute.cli.commands.HelpCommand;

import java.util.Arrays;

/**
 * Main CLI entry point for the Transmute project generator.
 * Delegates to command implementations for clean separation of concerns.
 */
public class TransmuteCLI {

    private static final CommandRegistry commandRegistry = new CommandRegistry();

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }

        String commandName = args[0];
        Command command = commandRegistry.getCommand(commandName);

        if (command != null) {
            // Extract command arguments (everything after the command name)
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
            command.execute(commandArgs);
        } else {
            System.err.println("Unknown command: " + commandName);
            System.err.println("Run 'transmute --help' for usage information.");
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Transmute CLI - Project Generator for Transmute Core Engine");
        System.out.println("\nUsage:");
        System.out.println("  transmute new <project-name> [options]");
        System.out.println("  transmute templates");
        System.out.println("  transmute --help");
        System.out.println("  transmute --version");
        System.out.println("\nRun 'transmute --help' for more information.");
    }
}
