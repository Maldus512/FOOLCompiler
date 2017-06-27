package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.STentry;
import lib.FOOLlib;

import ast.types.*;

/* Class representing a variable declaration and assignment */
public class VarNode implements Node {

	private String id;
	private TypeNode type;
	private Node exp;

	public VarNode (String i, TypeNode t, Node v) {
		id = i;
		type = t;
		exp = v;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(),type, env.decOffset());

		if ( type instanceof ClassTypeNode ) {
			boolean classDefined = false;
			ClassTypeNode t = (ClassTypeNode)type;

			HashMap<String,STentry> level_zero = env.getST().get(0);
			for (STentry e : level_zero.values()) {
				ClassNode c = (ClassNode)(e.getClassNode());
				if (c != null) {
					if ( t.getId().equals( c.getId() ) ) {
						classDefined = true;
						type = c.getClassType();
						entry.setClassNode(c);
						break;
					}
				}
			}

			if (!classDefined) {
				res.add( new SemanticError("Class " + t.getId() + " has not been defined."));
				return res;
			}

		}

		if ( hm.put(id,entry) != null )
			res.add(new SemanticError("Var id "+id+" already declared"));

		res.addAll(exp.checkSemantics(env));

		return res;
	}

	public String toPrint(String s) {
		return s+"Var:" + id +"\n"
			+type.toPrint(s+"  ")
			+exp.toPrint(s);
	}

	//valore di ritorno non utilizzato
	public TypeNode typeCheck(Environment env) {

		// TODO: bisogna controllare che il tipo dichiarato per la variabile sia lo stesso utilizzato per l'istanziazione dell'oggetto, o che sia un suo sottotipo.
		// l'id della classe istanziata lo si può ottenere con:
		// String newType = ((NewNode)exp).getClassId();

		if (! (FOOLlib.isSubtype(exp.typeCheck(env),type)) ) {
			System.out.println("incompatible value for variable "+id);
			System.exit(0);
		}
		return null;
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}  

}  
