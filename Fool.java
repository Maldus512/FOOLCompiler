import java.io.FileInputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import parser.ExecuteVM;
import parser.SVMLexer;
import parser.SVMParser;
import util.SyntaxErrorListener;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Fool {

	public static void main(String[] args) throws Exception {

		Cli commandArgs = new Cli();

		commandArgs.parse(args);

		String fileName = commandArgs.inputFile;

		if (fileName == null) {
			System.exit(0);
		}

		FileInputStream isASM = new FileInputStream(fileName);
		CharStream inputASM = CharStreams.fromStream(isASM);
		SVMLexer lexerASM = new SVMLexer(inputASM);
		CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
		SyntaxErrorListener errorListener = new SyntaxErrorListener();
		SVMParser parserASM = new SVMParser(tokensASM);
		parserASM.removeErrorListeners();
		parserASM.addErrorListener(errorListener);

		parserASM.assembly();


		if (errorListener.errors > 0) {
			System.out.println("The program was not in the right format. Exiting the compilation process now");
			System.exit(1);
		}

		int flags = 0;
		if (commandArgs.verbose) {
			flags = 1;
			System.out.println("Starting Virtual Machine...");
		}
		ExecuteVM vm = new ExecuteVM(parserASM.code, flags);
		vm.cpu();
		System.exit(0);
	}

	public static class Cli {
		public String inputFile = null;
		public boolean verbose = false;
		private Options options;

		public void parse(String[] args) {
			options = new Options();
			options.addOption("h", "help", false, "show this help menu.");
			options.addOption("d", "debug", false, "verbose output (parse tree)");
			options.addOption("v", "version", false, "compiler version");
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

				if (cmd.hasOption("d")) {
					verbose = true;
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

}
