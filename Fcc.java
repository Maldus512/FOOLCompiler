import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import parser.ExecuteVM;
import parser.FOOLLexer;
import parser.FOOLParser;
import parser.SVMLexer;
import parser.SVMParser;
import util.Environment;
import util.SemanticError;
import util.SyntaxErrorListener;
import ast.FoolVisitorImpl;
import ast.Node;
import ast.types.ClassTypeNode;
import ast.types.BottomTypeNode;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Fcc {

	public static void main(String[] args) {

		Cli commandArgs = new Cli();

		commandArgs.parse(args);

		String fileName = commandArgs.inputFile;

		if (fileName == null) {
			System.exit(0);
		}

		FileInputStream is = null;

		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("\nERROR. No file found with the given name.\n");
			System.exit(2);
		}

		CharStream input = null;
		try {
			input = CharStreams.fromStream(is);
		} catch (IOException e) {
			System.out.println(e.toString());
			System.exit(2);
		}
		FOOLLexer lexer = new FOOLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		SyntaxErrorListener errorListener = new SyntaxErrorListener();
		FOOLParser parser = new FOOLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		FoolVisitorImpl visitor = new FoolVisitorImpl();

		Node ast = visitor.visit(parser.prog()); //generazione AST 
		if (errorListener.errors > 0) {
			System.out.println("The program was not in the right format. Exiting the compilation process now");
			System.exit(1);
		}

		Environment env = new Environment();
		ArrayList<SemanticError> err = ast.checkSemantics(env);

		if (err.size() > 0) {
			System.out.println("You had: " + err.size() + " error(s):");
			for (SemanticError e : err)
				System.out.println("\t" + e);

			System.exit(1);
		}

		if (commandArgs.verbose) {
			System.out.println("Visualizing AST...");
			System.out.println(ast.toPrint(""));
		}

		Node type = ast.typeCheck(env); //type-checking bottom-up 

		if (commandArgs.verbose) {
			if (type instanceof ClassTypeNode) {
				System.out.println("Type of the program is: " + ((ClassTypeNode) type).getId());
			} else {
				System.out.println(type.toPrint("Type of the program is: "));
			}
		}

		if (type instanceof BottomTypeNode) {
			System.out.println("Type checking of the program not successful.");
			System.exit(1);
		}

		if (commandArgs.codeGen) {

			try {
				// CODE GENERATION  prova.fool.asm
				String code = ast.codeGeneration();
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName + ".asm"));
				out.write(code);
				out.close();
			} catch (IOException e) {
				System.out.println(e.toString());
				System.exit(2);
			}
			System.out.println("Code generated!");
		}
		System.exit(0);
	}

	public static class Cli {
		public boolean codeGen = true;
		public String inputFile = null;
		public boolean verbose = false;
		private Options options;

		public void parse(String[] args) {
			options = new Options();
			options.addOption("h", "help", false, "show this help menu.");
			options.addOption("c", "check", false, "only perform semantic and type check");
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

				if (cmd.hasOption("c")) {
					codeGen = false;
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
