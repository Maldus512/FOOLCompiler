
package test;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
    public boolean codeGen = true;
    public String inputFile = null;
    private Options options;

    public void parse(String[] args) {
        options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addOption("c", "check", false, "only perform semantic and type check");
        options.addOption("f", "input-file", true, "input file to be compilated");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            boolean option = false;

            if (cmd.hasOption("h")) {
                help();
                option = true;
            }

            if (cmd.hasOption("c")) {
                codeGen = false;
                option = true;
            }

            if (cmd.hasOption("f")) {
                String input = cmd.getOptionValue("f");
                option = true;
                if (input != null) {
                    inputFile = input;
                } else {
                    System.out.println("Filename not specified");
                    System.exit(1);
                }
            }

            if (!option) {
                help();
            }

        } catch (ParseException e) {
            help();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
    }
}
