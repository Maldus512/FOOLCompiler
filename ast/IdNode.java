package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import util.STentry;
import java.util.HashMap;

import ast.types.*;

public class IdNode implements Node {

	private String id;
	private STentry entry;
	private int nestinglevel;

	public IdNode (String i) {
		id = i;
	}

	public String toPrint(String s) {
		return s + "Id:" + id + " at nestlev " + nestinglevel + "\n"
				+ entry.toPrint(s+"  ") ;
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

		int j = env.getNestLevel();
		STentry tmp = null; 
		
		while (j >= 0 && tmp == null)
			tmp = (env.getST().get(j--)).get(id);
		
		if (tmp == null) {
			res.add( new SemanticError("Id " + id + " not declared.") );
			return res;
		} else if (tmp.getType() instanceof ArrowTypeNode) {
			res.add( new SemanticError("Id " + id + " is a function.") );
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
		String getAR="";
		
		if (!entry.getType().isField()) {
			for (int i=0; i<nestinglevel-entry.getNestLevel(); i++) 
				getAR+="lw\n";

			System.out.println("id: " + id + "; offset " +entry.getOffset());

			return	"push "+entry.getOffset()+"\n"+ //metto offset sullo stack
					"lfp\n"+getAR+ //risalgo la catena statica
					"add\n"+ 
					"lw\n"; //carico sullo stack il valore all'indirizzo ottenuto
		} else {
			System.out.println("field id: " + id + "; offset " +entry.getOffset());
			return  "push 1\n" +
					"lfp\n" +
					"add\n" +
					"lw\n" +
					"push " + entry.getOffset() + "\n" +
					"add\n" +
					"lw\n";
		}

	}
}  