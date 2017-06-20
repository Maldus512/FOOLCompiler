import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import parser.ExecuteVM;
import parser.FOOLLexer;
import parser.FOOLParser;
import parser.SVMLexer;
import parser.SVMParser;
import util.Environment;
import util.SemanticError;
import ast.FoolVisitorImpl;
import ast.Node;

public class Test {
	public static void main(String[] args) throws Exception {

		String fileName = "prova2.fool";
		FileInputStream is = null;

//		try {
//			fileName = args[0];
//		}
//		catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("\nERROR. You must provide a fool program.\nEither use:\n$ java Test prog.fool\n$ make run f=prog.fool\nwhere prog.fool is a fool program.\n");
//			System.exit(1);
//		}

		try {
			is = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e) {
			System.out.println("\nERROR. No file found with the given name.\n");
			System.exit(2);
		}

		ANTLRInputStream input = new ANTLRInputStream(is);
		FOOLLexer lexer = new FOOLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		//SIMPLISTIC BUT WRONG CHECK OF THE LEXER ERRORS
		if (lexer.lexicalErrors > 0) {
			System.out.println("The program was not in the right format. Exiting the compilation process now");
		} else {

			FOOLParser parser = new FOOLParser(tokens);

			FoolVisitorImpl visitor = new FoolVisitorImpl();

			Node ast = visitor.visit(parser.prog()); //generazione AST 

			Environment env = new Environment();
			ArrayList<SemanticError> err = ast.checkSemantics(env);

			if (err.size() > 0 ){
				System.out.println("You had: " +err.size()+" error(s):");
				for(SemanticError e : err)
					System.out.println("\t" + e);
			} else {
				System.out.println("Visualizing AST...");
				System.out.println(ast.toPrint(""));

				Node type = ast.typeCheck(); //type-checking bottom-up 
				System.out.println(type.toPrint("Type checking ok! Type of the program is: "));


				// CODE GENERATION  prova.fool.asm
				String code=ast.codeGeneration(); 
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName+".asm")); 
				out.write(code);
				out.close(); 
				System.out.println("Code generated! Assembling and running generated code.");

				FileInputStream isASM = new FileInputStream(fileName+".asm");
				ANTLRInputStream inputASM = new ANTLRInputStream(isASM);
				SVMLexer lexerASM = new SVMLexer(inputASM);
				CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
				SVMParser parserASM = new SVMParser(tokensASM);

				parserASM.assembly();

				System.out.println("You had: "+lexerASM.lexicalErrors+" lexical error(s) and "+parserASM.getNumberOfSyntaxErrors()+" syntax error(s).");
				if (lexerASM.lexicalErrors>0 || parserASM.getNumberOfSyntaxErrors()>0) System.exit(1);

				System.out.println("Starting Virtual Machine...");
				ExecuteVM vm = new ExecuteVM(parserASM.code);
				vm.cpu();
			}
		}
	}
}
