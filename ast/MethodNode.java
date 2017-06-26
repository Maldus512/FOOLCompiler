package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class MethodNode implements Node {

	private String id;
	private Node type; 
	private STentry methodEntry;
	// private ArrowTypeNode arrowType;
	private ArrayList<Node> parList;
	private ArrayList<Node> decList;
	private Node body;
	private ClassNode ownerClass;	// class node where this method is first defined
	private int curMethodOffset;		// offset in callerClass
	
	public MethodNode (String i, Node t) {
		id=i;
		type=t;
	}

	public MethodNode (String _id, Node _type, ArrayList<Node> _parList, ArrayList<Node> _decList, Node _body) {
		id = _id;
		type = _type;
		parList = _parList;
		decList = _decList;
		body = _body;
	}

	public String getId() { return id; }

	public Node getType() { return type; }

	// public ArrowTypeNode getArrowType() { return arrowType; }
	public STentry getEntry() { return methodEntry; }

	public ArrayList<Node> getParList() { return parList; }

	public ArrayList<Node> getDecList() { return decList; }

	public Node getBody() { return body; }

	public String getOwnerClassId() { return ownerClass.getId(); }

	public int getOffset() { return curMethodOffset; }

	public void addDecBody (ArrayList<Node> d, Node b) {
		decList = d;
		body = b;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		return checkSemantics(env, 0, null);
		// return checkSemantics(env, 0, -1, null);
	}

	public ArrayList<SemanticError> checkSemantics(Environment env, int offset, ClassNode callerClass) {
		// callerClass is the class from where the semantic check has been called
		curMethodOffset = offset + 1;

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(), offset, ownerClass);

		STentry prevEntry = hm.put( id, entry );
		if ( prevEntry != null) {

			if (callerClass == null) { // if callerClass is null there are no class calling, thus we are not inside a class.
				res.add( new SemanticError("Function name '" + id + "' has already been used.") );
				return res;
			}

			// if the previous entry's class is the same of the calling one, the method has been redefined within the same class
			if (prevEntry.getClassNode().getId().equals( callerClass.getId() ) ) {
				
				res.add( new SemanticError("Method name '" + id + "' for class '" + callerClass.getId() + "' has already been used.") );
				return res;

			}

			// if we are here we are overriding a method, thus we must update offsets accordingly
			entry = new STentry( env.getNestLevel(), prevEntry.getOffset(), ownerClass );

			curMethodOffset--;
		}

		// if this method is defined for the first time, update ownerClass for both method and ST's entry
		if (ownerClass == null && callerClass != null) {
			ownerClass = callerClass;
			hm.get(id).setClassNode(ownerClass);
		}

		env.incNestLevel();
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<Node> parTypes = new ArrayList<Node>();
		int paroffset = 0;

		//check args
		for(Node a:parList) {
			ParNode arg = (ParNode) a;
			parTypes.add(arg.getType());

			if ( hmn.put( arg.getId(), new STentry(env.getNestLevel(), arg.getType(), paroffset++) ) != null  ) {
				res.add( new SemanticError("Parameter name " + arg.getId() + " of method " + id + " has already been used.") );
				return res;
			}
		}

		//set method type
		// arrowType = new ArrowTypeNode(parTypes, type);
		// entry.addType( arrowType );
		entry.addType( new ArrowTypeNode(parTypes, type) );

		methodEntry = entry;
		hm.put( id, entry );

		//check semantics in the dec list
		if(decList.size() > 0) {
			env.setOffset(-2);
			for(Node n:decList)
				res.addAll(n.checkSemantics(env));
		}

		//check body
		res.addAll(body.checkSemantics(env));

		//close scope
		env.getST().remove(env.decNestLevel());

		return res;
	}

	public void addPar (Node p) {
		parList.add(p);
	}  

	public String toPrint(String s) {
		String	parlstr="",
				declstr="";
		
		for (Node par:parList)
			parlstr+=par.toPrint(s+"  ");
		
		if (decList!=null)
			for (Node dec:decList)
				declstr+=dec.toPrint(s+"  ");

		return s+"Fun:" + id +"\n"
			   +type.toPrint(s+"  ")
			   +parlstr
			   +declstr
			   +body.toPrint(s+"  ") ; 
	}

	//valore di ritorno non utilizzato
	public Node typeCheck () {
		if (decList!=null) 
			for (Node dec:decList)
				dec.typeCheck();
		
		if ( !(FOOLlib.isSubtype(body.typeCheck(),type)) ){
			System.out.println("Wrong return type for function " + id);
			System.exit(0);
		}
	return null;
	}

	public String codeGeneration() {
	  
		String declCode="";
		if (decList!=null) for (Node dec:decList)
			declCode+=dec.codeGeneration();
		
		String popDecl="";
		if (decList!=null) for (Node dec:decList)
			popDecl+="pop\n";
		
		String popParl="";
		for (Node dec:parList)
			popParl+="pop\n";
		
		String funl=FOOLlib.freshFunLabel(); 
		FOOLlib.putCode(funl+":\n"+
				"cfp\n"+ //setta $fp a $sp				
				"lra\n"+ //inserimento return address
				declCode+ //inserimento dichiarazioni locali
				body.codeGeneration()+
				"srv\n"+ //pop del return value
				popDecl+
				"sra\n"+ // pop del return address
				"pop\n"+ // pop di AL
				popParl+
				"sfp\n"+  // setto $fp a valore del CL
				"lrv\n"+ // risultato della funzione sullo stack
				"lra\n"+"js\n" // salta a $ra
				);
		
		return "push "+ funl +"\n";
	}

}