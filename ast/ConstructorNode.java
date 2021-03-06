package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import util.STentry;
import ast.types.*;

public class ConstructorNode extends CallNode {

	public ConstructorNode(String i, ArrayList<Node> p) {
		super(i, p);
	}

	public ConstructorNode(String i, STentry e, ArrayList<Node> p, int nl) {
		super(i, e, p, nl);
	}

	public String getId() {
		return id;
	}

	// public ClassNode getClassRef() {
	// return classRef;
	// }

	@Override
	public String toPrint(String s) {
		String parstr = "";

		for (Node par : parList) {
			parstr += par.toPrint(s + "  ");
		}

		return s + "New:" + id + "\n" + parstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		if (env.classTypeEnvGet(id) == null) {
			res.add(new SemanticError("Class " + id + " has not been defined; cannot be instantiated."));
			return res;
		}

		res.addAll(super.checkSemantics(env));
		return res;
	}

	//valore di ritorno non utilizzato
	@Override
	public TypeNode typeCheck(Environment env) {
		return super.typeCheck(env);
	}

	@Override
	public String codeGeneration() {
		return super.codeGeneration();
	}

}
