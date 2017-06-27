package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import ast.types.*;

public class FieldNode implements Node {

	private String id;
	private TypeNode type;

	public FieldNode (String i, TypeNode t) {
		id=i;
		type=t;
	}

	public String getId(){
		return id;
	}

	public TypeNode getType(){
		return type;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		return new ArrayList<SemanticError>();
	}

	public String toPrint(String s) {
		return s+"Field:" + id +"\n"+type.toPrint(s+"  ") ; 
	}

	//non utilizzato
	public TypeNode typeCheck(Environment env) {
		return null;
	}

	//non utilizzato
	public String codeGeneration() {
		return "";
	}

}  