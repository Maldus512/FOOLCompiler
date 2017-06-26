package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class ConstructorNode implements Node {

	private String classId;
	private ArrayList<Node> parList = new ArrayList<Node>();
	private ClassNode classRef;

	public ConstructorNode (String i) {
		classId = i;
	}

	public void addPar(Node p) {
		parList.add(p);
	}

	public String getClassId() {
		return classId;
	}

	public ClassNode getClassRef() {
		return classRef;
	}

	public String toPrint(String s) {
		String parstr = "";

		for (Node par : parList) {
			parstr += par.toPrint(s+"  ");
		}
		
		return s + "New:" + classId +"\n"
				+ parstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		boolean classDefined = false;
		HashMap<String,STentry> level_zero = env.getST().get(0);

		for (STentry e : level_zero.values()) {
			if (e.getClassNode() instanceof ClassNode) {
				ClassNode c = (ClassNode)(e.getClassNode());

				// In the symbol table, objects have the same type of their classes, thus they would be included in this search. We exclude them by checking whether they have methods or not.
				if ( classId.equals( c.getId() ) && c.getMethodList().size() > 0 ) {	
					classDefined = true;
					classRef = c;
					break;
				}
			}
		}

		if (!classDefined) {
			res.add( new SemanticError("Class " + classId + " has not been defined.") );
			return res;
		}

		for (Node par : parList){
			res.addAll(par.checkSemantics(env));
		}
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
