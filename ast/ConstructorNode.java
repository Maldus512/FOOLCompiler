package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.STentry;
import lib.FOOLlib;
import ast.types.*;

public class ConstructorNode implements Node {

	private String classId;
	private ArrayList<Node> parList = new ArrayList<Node>();
	// private ClassNode classRef;

	public ConstructorNode (String i) {
		classId = i;
	}

	public void addPar(Node p) {
		parList.add(p);
	}

	public String getClassId() {
		return classId;
	}

	// public ClassNode getClassRef() {
		// return classRef;
	// }

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
					// classRef = c;
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

	public Node typeCheck(Environment env) {

		if (classId != null) {
			HashMap<String,STentry> hm = env.getST().get(0);
	      	STentry entry = hm.get( classId );

			ClassTypeNode t=null;
	        if (entry.getType() instanceof ClassTypeNode) t=(ClassTypeNode) entry.getType(); 
	        else {
	            System.out.println("Invocation of a non-class constructor "+classId);  //<----- NON SONO SICURO ABBIA SENSO METTERE QUESTO CONTROLLO, E L'ERRORE VA CAMBIATO
	            System.exit(0);
	        }
	        HashMap<String,Node> p = t.getFields();
	        System.out.println("parametri dichiarati = "+ p.keySet() + " paramatri ricevuti = " + parList.size());
	        if ( !(p.keySet().size() == parList.size()) ) {
	            System.out.println("Wrong number of parameters in the invocation of the constructor "+classId);
	            System.exit(0);
	        } 
	        for (int i=0; i<parList.size(); i++) {
	        	System.out.println("il parametro analizzato è "+i);
	        	System.out.println("il tipo aspettato è  "+(parList.get(i)).typeCheck(env));
	        	System.out.println("il tipo ricevuto è  "+(p.get(i)) );

	            if ( !(FOOLlib.isSubtype( (parList.get(i)).typeCheck(env), p.get(i)) ) ) { //TO DO : ADATTARE CON IL VALORE DELLA HM, COSÌ NON LO POSSO FARE
	                System.out.println("Wrong type for "+(i+1)+"-th parameter in the invocation of "+classId);
	                System.exit(0);
	            } 
	        }
	        return  (ClassTypeNode)entry.getType() ;
    	}
    	return null ;	


	public String codeGeneration() {
		return "";
	}  

}  
