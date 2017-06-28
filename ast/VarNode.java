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
				ClassNode c = (ClassNode)(e.getClassNode());
				if (c != null) {
					if ( t.getId().equals( c.getId() ) ) {
						classDefined = true;
						type = c.getClassType();
						entry.setClassNode(c);
						entry.setType(type);
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

		// if (id.equals("test")) {
		// 	System.out.println("Visiting id: " + id);
		// 	STentry e = hm.get(id);
		// 	Node t = e.getType();
		// 	if (t instanceof ClassTypeNode) {

		// 	}
		// }

		return res;
	}

	public String toPrint(String s) {
		return s + "Var:" + id + "\n"
				+ type.toPrint(s+"  ")
				+ exp.toPrint(s);
	}

	//valore di ritorno non utilizzato
	public TypeNode typeCheck(Environment env) {

		// TODO: bisogna controllare che il tipo dichiarato per la variabile sia lo stesso utilizzato per l'istanziazione dell'oggetto, o che sia un suo sottotipo.
		// l'id della classe istanziata lo si può ottenere con:
		// String newType = ((NewNode)exp).getClassId();
<<<<<<< HEAD
		TypeNode res = exp.typeCheck(env);
		if (! (FOOLlib.isSubtype(res,type)) ) {
=======
		
		if (! (FOOLlib.isSubtype(exp.typeCheck(env),type)) ) {
>>>>>>> 64d7eb26896849c4c9fcf89bfb1c7805c52fd32e
			System.out.println("incompatible value for variable "+id);
			return new BottomTypeNode();
		} else {
			return res;
		}
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}  

}  
