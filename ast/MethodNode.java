package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;
import util.STentry;

import ast.types.*;

public class MethodNode implements Node {

	private String id;
	private TypeNode type; 
	private STentry methodEntry;
	// private ArrowTypeNode arrowType;
	private ArrayList<Node> parList;
	private ArrayList<Node> decList;
	private Node body;
	// private ClassNode ownerClass;	// class node where this method is first defined
	// private int curMethodOffset;		// offset in callerClass
	
	public MethodNode (String i, TypeNode t) {
		id=i;
		type=t;
	}

	public MethodNode (String _id, TypeNode _type, ArrayList<Node> _parList, ArrayList<Node> _decList, Node _body) {
		id = _id;
		type = _type;
		parList = _parList;
		decList = _decList;
		body = _body;
	}

	public String getId() { return id; }

	public TypeNode getType() { return type; }

	// public ArrowTypeNode getArrowType() { return arrowType; }
	public STentry getEntry() { return methodEntry; }

	public ArrayList<Node> getParList() { return parList; }

	public ArrayList<Node> getDecList() { return decList; }

	public Node getBody() { return body; }

	// public int getOffset() { return curMethodOffset; }

	public void addDecBody (ArrayList<Node> d, Node b) {
		decList = d;
		body = b;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		return checkSemantics(env, 0);
	}

	public ArrayList<SemanticError> checkSemantics(Environment env, int offset) {
		// curMethodOffset = offset + 1;

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(), offset);

		STentry prevEntry = hm.put( id, entry );
		if ( prevEntry != null ) {
			// if we are here we are overriding a method, thus we must update offsets accordingly
			entry.setOffset( prevEntry.getOffset() );
			// curMethodOffset--;
		}

		env.incNestLevel();
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<TypeNode> parTypes = new ArrayList<TypeNode>();
		int paroffset = 0;

		//check args
		for(Node n : parList) {
			ParNode par = (ParNode) n;
			parTypes.add(par.getType());

			if ( hmn.put( par.getId(), new STentry(env.getNestLevel(), par.getType(), paroffset++) ) != null  ) {
				res.add( new SemanticError("Parameter name " + par.getId() + " of method " + id + " has already been used.") );
				return res;
			}
		}

		//set method type
		entry.setType( new ArrowTypeNode(parTypes, type) );

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
	public TypeNode typeCheck(Environment env) {
		if (decList!=null) {
			for (Node dec:decList) {
				if (dec.typeCheck(env) instanceof BottomTypeNode) {
					return new BottomTypeNode();
				}
			}
		}
		
		if ( !(FOOLlib.isSubtype(body.typeCheck(env),type)) ){
			System.out.println("Wrong return type for function " + id);
			return new BottomTypeNode();
		}
		return new VoidTypeNode();
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