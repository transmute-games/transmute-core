package games.transmute.cli.commands;

import static games.transmute.cli.CliConstants.*;

/**
 * Command to display version information.
 */
public class VersionCommand implements Command {

    @Override
    public void execute(String[] args) {
        System.out.println("Transmute CLI v" + CLI_VERSION);
        System.out.println("Transmute Core Engine v" + ENGINE_VERSION);
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"--version", "-v"};
    }

    @Override
    public String getDescription() {
        return "Show version information";
    }
}
