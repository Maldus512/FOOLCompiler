package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import util.STentry;

import ast.types.*;

public class IdNode implements Node {

	private String id;
	private STentry entry;
	private int nestinglevel;

	public IdNode(String i) {
		id = i;
	}

	public String toPrint(String s) {
		return s + "Id:" + id + " at nestlev " + nestinglevel + "\n" + entry.toPrint(s + "  ");
	}

	public TypeNode getType() {
		return entry.getType();
	}

	public String getId() {
		return id;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		//int j = env.getNestLevel();
		int j = env.getLastNestLevel();
		STentry tmp = null;

		while (j >= 0 && tmp == null)
			tmp = (env.getST().get(j--)).get(id);

		if (tmp == null) {
			res.add(new SemanticError("Symbol " + id + " has not been declared."));
			return res;
		} else if (tmp.getType() instanceof ArrowTypeNode) {
			res.add(new SemanticError("Symbol " + id + " is a function."));
			return res;
		}

		entry = tmp;
		nestinglevel = env.getNestLevel();

		return res;
	}

	public TypeNode typeCheck(Environment env) {
		if (entry.getType() instanceof ArrowTypeNode) {
			System.out.println("Wrong usage of function identifier.");
			return new BottomTypeNode();
		}
		return entry.getType();
	}

	public String codeGeneration() {
		String getAR = "";

		if (!entry.getType().isField()) {
			for (int i = 0; i < nestinglevel - entry.getNestLevel(); i++)
				getAR += "lw\n";
			return "push " + entry.getOffset() + "\n" + //metto offset sullo stack
					"lfp\n" + getAR + //risalgo la catena statica
					"add\n" + "lw\n"; //carico sullo stack il valore all'indirizzo ottenuto
		} else {
			// -1 because field references in a class need to have 1 less nesting level.
			// This is because the class' nesting level does not immediatly translates
			// into one more Activation Record
			for (int i = 0; i < nestinglevel - entry.getNestLevel() -1; i++)
				getAR += "lw\n";
			return "push 1\n" + "lfp\n" + getAR + "add\n" + "lw\n" + "push " + entry.getOffset() + "\n" + "add\n" + "lw\n";
		}

	}
}