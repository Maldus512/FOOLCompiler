package ast;
import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class MethodCallNode implements Node {

	private String id;
	private STentry methodEntry;
	private ArrayList<Node> parList;
	private int nestLevel;
	private String selfId;

	public MethodCallNode(String text, ArrayList<Node> args, String sn) {
		id = text;
		parList = args;
		selfId = sn;
	}

	public String toPrint(String s) {
		String parlstr = "";

		for (Node par:parList)
			parlstr += par.toPrint(s+"  ");

		return s + "Call:" + id + " at nestlev " + nestLevel + "\n" 
			+ methodEntry.toPrint(s+"  ")
			+ parlstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		int j = env.getNestLevel();
		STentry varTmp = null;
		STentry methodTmp = null;
		

		// seek for var (a.k.a. selfName) id
		while (j>=0 && varTmp==null)
			varTmp = (env.getST().get(j--)).get(selfId);

		if (varTmp == null) {
			res.add( new SemanticError("Object id '" + selfId + "' has not been declared.") );
			return res;
		}

		// get actual instance of the object calling the method
		String ownerClass = ((ClassNode)(varTmp.getClassNode())).getId();

		// seek for method definition
		HashMap<String,STentry> level_zero = env.getST().get(0);

		/*// DEBUG
		System.out.println();
		for (String s : level_zero.keySet()) {
			
			STentry e = level_zero.get(s);
			System.out.println( "Key: " + s + ", Type: " + e.getType() + ", NestLevel: " + e.getNestLevel() + ", Offset: " + e.getOffset() );

			if (e.getClassNode() instanceof ClassNode) {
				for (Node n : e.getClassNode().getMethodList()) {
					MethodNode m = (MethodNode)n;

					System.out.println("\tClass: " + e.getClassNode().getId() + ", Id: " + m.getId() + ", NestLevel: " + m.getEntry().getNestLevel() + ", Offset: " + m.getEntry().getOffset() + ", ParList: " + ((ArrowTypeNode)(m.getEntry().getType())).getParList().size() );
				}
			}

		}
		System.out.println();*/


		for (STentry e : level_zero.values()) {
			
			if ( (e.getClassNode() instanceof ClassNode) && (e.getClassNode().getId().equals(ownerClass)) ) {

				for (Node n : e.getClassNode().getMethodList()) {
					MethodNode m = (MethodNode)n;

					if (m.getId().equals(id)) {
						methodTmp = m.getEntry();
						break;
					}
				}
			}
		}

		if (methodTmp == null) {
			res.add( new SemanticError("Method id '" + id + "' has not been declared for class " + ownerClass + ".") );
			return res;
		}

		this.methodEntry = methodTmp;
		this.nestLevel = env.getNestLevel();

		for(Node arg : parList)
			res.addAll(arg.checkSemantics(env));

		return res;
	}

	public Node typeCheck(Environment env) {
		ArrowTypeNode t=null;
		if (methodEntry.getType() instanceof ArrowTypeNode) {
			t = (ArrowTypeNode)methodEntry.getType(); 
		} else {
			System.out.println("Invocation of a non-function "+id);
			System.exit(0);
		}
		ArrayList<Node> p = t.getParList();
		if ( !(p.size() == parList.size()) ) {
			System.out.println("Wrong number of parameters in the invocation of "+id);
			System.exit(0);
		} 
		for (int i=0; i<parList.size(); i++)
			if ( !(FOOLlib.isSubtype( (parList.get(i)).typeCheck(env), p.get(i)) ) ) {
				System.out.println("Wrong type for "+(i+1)+"-th parameter in the invocation of "+id);
				System.exit(0);
			} 
		return t.getRet();
	}

	public String codeGeneration() {
		String parCode="";
		for (int i=parList.size()-1; i>=0; i--)
			parCode+=parList.get(i).codeGeneration();

		String getAR="";
		for (int i=0; i<nestLevel-methodEntry.getNestLevel(); i++)
			getAR+="lw\n";

		return "lfp\n"+ //CL
			parCode+
			"lfp\n"+getAR+ //setto AL risalendo la catena statica
			// ora recupero l'indirizzo a cui saltare e lo metto sullo stack
			"push "+methodEntry.getOffset()+"\n"+ //metto offset sullo stack
			"lfp\n"+getAR+ //risalgo la catena statica
			"add\n"+ 
			"lw\n"+ //carico sullo stack il valore all'indirizzo ottenuto
			"js\n";
	}


}  
