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
		STentry entry = new STentry(env.getNestLevel(), type, env.decOffset());

		if ( type instanceof ClassTypeNode ) {	// if we are instantiating an object
			boolean classDefined = false;
			ClassTypeNode t = (ClassTypeNode)type;

			HashMap<String,STentry> level_zero = env.getST().get(0);
			for (STentry e : level_zero.values()) {	// iterate over definition of classes
				if (e.getType() instanceof ClassTypeNode) {
					ClassTypeNode classType = (ClassTypeNode)(e.getType());

					if ( ((String)(t.getId())).equals(classType.getId()) ) {
						classDefined = true;
						type = classType;
						entry.setType( classType );
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
		return s + "Var:" + id + "\n"
				+ type.toPrint(s+"  ")
				+ exp.toPrint(s);
	}

	//valore di ritorno non utilizzato
	public TypeNode typeCheck(Environment env) {

		TypeNode expType = exp.typeCheck(env);

		if (! (FOOLlib.isSubtype(expType, type)) ) {
			System.out.println("incompatible value for variable "+id);
			return new BottomTypeNode();
		}

		return expType;
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}  

}  
