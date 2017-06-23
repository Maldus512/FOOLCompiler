package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class NewNode implements Node {

	private String classId;
	private ArrayList<Node> parList = new ArrayList<Node>();
	
	public NewNode (String i) {
		classId = i;
	}

	public void addPar(Node p) {
		parList.add(p);
	}

	public String getClassId() {
		return classId;
	}

	public String toPrint(String s) {
		String parstr = "";

		for (Node par : parList) {
			parstr += par.toPrint("  ");
		}
		
		return s + "Par:\n"
				+ parstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		return res;
	}

	//valore di ritorno non utilizzato
	public Node typeCheck () {
		// if (! (FOOLlib.isSubtype(exp.typeCheck(),type)) ){      
		//     System.out.println("incompatible value for variable "+id);
		//     System.exit(0);
		// }     
		return null;
	}

	public String codeGeneration() {
		return "";
	}  

}  
