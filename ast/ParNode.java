package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

import ast.types.*;

public class ParNode implements Node {

	private String id;
	private TypeNode type;
	
	public ParNode (String i, TypeNode t) {
		id=i;
		type=t;
	}
	
	public String getId(){ return id; }
	
	public TypeNode getType(){ return type; }

	public void setType(TypeNode t){ type = t; }

	// public Node getClassNode(){ return classNode; }

	// public void setClassNode(ClassNode c){ classNode = c; }
	
	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		return new ArrayList<SemanticError>();
	}
	
	public String toPrint(String s) {
		return s + "Par:" + id + "\n"
			 	 + type.toPrint(s+"  ") ;
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