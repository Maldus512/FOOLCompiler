package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class FunNode implements Node {

	private String id;
	private Node type; 
	private ArrayList<Node> parlist = new ArrayList<Node>();
	private ArrayList<Node> declist;
	private Node body;
	private ClassNode ownerClass;	// class node where this method is first defined
	// TODO: ownerClass e callerClass can be simple Strings

	public FunNode (String i, Node t) {
		id=i;
		type=t;
	}

	public String getId() {
		return id;
	}

	public Node getType() {
		return type;
	}

	public void addDecBody (ArrayList<Node> d, Node b) {
		declist = d;
		body = b;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		return checkSemantics(env, 0, null);
	}

	// @Override
	public ArrayList<SemanticError> checkSemantics(Environment env, int offset, ClassNode callerClass) {
		// callerClass is the class from where the semantic check has been called

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(), offset, ownerClass);

		STentry prevEntry = hm.put( id, entry );
		if ( prevEntry != null) {
			
			// if the previous entry's class is the same of the calling one, the method has been redefined within the same class
			if (prevEntry.getClassNode().getId().equals( callerClass.getId() ) ) {
				
				res.add( new SemanticError("Method name '" + id + "' for class '" + callerClass.getId() + "' has already been used.") );
				return res;

			} else if (callerClass == null) { // if callerClass is null there are no class calling, thus we are not inside a class.
				res.add( new SemanticError("Function name '" + id + "' has already been used.") );
				return res;
			}
		}

		// A new function has just been added to the symbol table (see above code). If it was owned by a class, then set ownerClass for both method and ST's entry
		if (ownerClass == null && callerClass != null) {
			ownerClass = callerClass;
			entry.setClassNode(ownerClass);
		}

		env.incNestLevel();
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<Node> parTypes = new ArrayList<Node>();
		int paroffset=1;

		//check args
		for(Node a:parlist) {
			ParNode arg = (ParNode) a;
			parTypes.add(arg.getType());

			if ( hmn.put( arg.getId(), new STentry(env.getNestLevel(), arg.getType(), paroffset++) ) != null  ) {
				res.add( new SemanticError("Parameter name " + arg.getId() + " of method " + id + " has already been used.") );
				return res;
			}
		}

		//set method type
		entry.addType( new ArrowTypeNode(parTypes, type) );

		//check semantics in the dec list
		if(declist.size() > 0) {
			env.setOffset(-2);
			for(Node n:declist)
				res.addAll(n.checkSemantics(env));
		}

		//check body
		res.addAll(body.checkSemantics(env));

		//close scope
		env.getST().remove(env.decNestLevel());

		return res;
	}

	public void addPar (Node p) {
		parlist.add(p);
	}  

	public String toPrint(String s) {
		String	parlstr="",
				declstr="";
		
		for (Node par:parlist)
			parlstr+=par.toPrint(s+"  ");
		
		if (declist!=null)
			for (Node dec:declist)
				declstr+=dec.toPrint(s+"  ");

		return s+"Fun:" + id +"\n"
			   +type.toPrint(s+"  ")
			   +parlstr
			   +declstr
			   +body.toPrint(s+"  ") ; 
	}

	//valore di ritorno non utilizzato
	public Node typeCheck () {
		if (declist!=null) 
			for (Node dec:declist)
				dec.typeCheck();
		
		if ( !(FOOLlib.isSubtype(body.typeCheck(),type)) ){
			System.out.println("Wrong return type for function " + id);
			System.exit(0);
		}
	return null;
	}

	public String codeGeneration() {
	  
		String declCode="";
		if (declist!=null) for (Node dec:declist)
			declCode+=dec.codeGeneration();
		
		String popDecl="";
		if (declist!=null) for (Node dec:declist)
			popDecl+="pop\n";
		
		String popParl="";
		for (Node dec:parlist)
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