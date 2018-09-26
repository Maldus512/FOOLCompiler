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

/**
 * FOOL JAVA COMPILER
 */
public class FoolProject {

	public static void main(String[] args) throws Exception{

		String fileName = "test_special_4_turing.fool";

		FileInputStream is = null;

		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("\nERROR. No file found with the given name.\n");
			System.exit(-1);
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

        System.out.println("Visualizing AST...");
        System.out.println(ast.toPrint(""));

		Node type = ast.typeCheck(env); //type-checking bottom-up 

        if (type instanceof ClassTypeNode) {
            System.out.println("Type of the program is: " + ((ClassTypeNode) type).getId());
        } else {
            System.out.println(type.toPrint("Type of the program is: "));
        }

		if (type instanceof BottomTypeNode) {
			System.out.println("Type checking of the program not successful.");
			System.exit(2);
		}

        try {
            // CODE GENERATION  prova.fool.asm
            String code = ast.codeGeneration();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName + ".asm"));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
        System.out.println("Code generated: " + fileName+".asm");

        fileName = fileName+".asm";

		FileInputStream isASM = new FileInputStream(fileName);
		CharStream inputASM = CharStreams.fromStream(isASM);
		SVMLexer lexerASM = new SVMLexer(inputASM);
		CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
		errorListener = new SyntaxErrorListener();
		SVMParser parserASM = new SVMParser(tokensASM);
		parserASM.removeErrorListeners();
		parserASM.addErrorListener(errorListener);

		parserASM.assembly();

		if (errorListener.errors > 0) {
			System.out.println("The program was not in the right format. Exiting the compilation process now");
			System.exit(1);
		}

		int flags = 1;
        System.out.println("Starting Virtual Machine...");
		ExecuteVM vm = new ExecuteVM(parserASM.code, flags);
		vm.cpu();

		System.exit(0);
	}
}
