import util.Cli;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
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

		ANTLRInputStream input = null;
		try {
			input = new ANTLRInputStream(is);
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

		if (err.size() > 0 ){
			System.out.println("You had: " +err.size()+" error(s):");
			for(SemanticError e : err)
				System.out.println("\t" + e);

			System.exit(1);
		}

		// Node type = ast.typeCheck(env); //type-checking bottom-up 

		if (commandArgs.verbose) {
			System.out.println("Visualizing AST...");
			System.out.println(ast.toPrint(""));
			// System.out.println(type.toPrint("Type checking ok! Type of the program is: "));
		}

		if (commandArgs.codeGen) {

			try {
				// CODE GENERATION  prova.fool.asm
				String code=ast.codeGeneration(); 
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName+".asm")); 
				out.write(code);
				out.close(); 
			} catch (IOException e) {
				System.out.println(e.toString());
				System.exit(2);
			}
			System.out.println("Code generated! Assembling and running generated code.");
		}
		System.exit(0);
	}
}

