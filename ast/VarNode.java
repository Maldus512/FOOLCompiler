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

		//HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		HashMap<String,STentry> hm = env.getST().get(env.getLastNestLevel());
		STentry entry = new STentry(env.getNestLevel(), type, env.decOffset());


		if ( type instanceof ClassTypeNode ) {	// if we are instantiating an object
			String classId = ((ClassTypeNode)type).getId();
			ClassTypeNode fullClassType = env.classTypeEnvGet(classId);
			
			if (fullClassType == null) {
				res.add( new SemanticError("Class " + classId + " has not been defined."));
				return res;
			}
			type = fullClassType;
			entry.setType(type);

		}

		if ( hm.put(id,entry) != null ) {
			res.add(new SemanticError("Var id " + id + " already declared."));
			return res;
		}

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
