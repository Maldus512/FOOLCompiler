package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ParNode implements Node {

	private String id;
	private Node type;
	// private ClassNode classNode;
	
	public ParNode (String i, Node t) {
		id = i;
		type = t;
	}
	
	public String getId(){ return id; }
	
	public Node getType(){ return type; }

	public void setType(Node t){ type = t; }

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
	public Node typeCheck(Environment env) {
		return null;
	}

	//non utilizzato
	public String codeGeneration() {
		return "";
	}		
}  