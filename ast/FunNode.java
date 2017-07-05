package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;
import util.STentry;

import ast.types.*;

public class FunNode implements Node {

	private String id;
	private TypeNode type; 
	private ArrowTypeNode arrowType;
	private ArrayList<Node> parList = new ArrayList<Node>();
	private ArrayList<Node> decList;
	private Node body;

	public FunNode (String i, TypeNode t) {
		id = i;
		type = t;
	}

	public String getId() { return id; }

	public TypeNode getType() { return type; }

	public ArrayList<Node> getParList() { return parList; }

	public ArrayList<Node> getDecList() { return decList; }

	public Node getBody() { return body; }

	public void addDecBody (ArrayList<Node> d, Node b) {
		decList = d;
		body = b;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(), env.decOffset());

		
		if (hm.put( id, entry ) != null) {
			res.add( new SemanticError("Function name '" + id + "' has already been used.") );
			return res;
		}

		env.incNestLevel();
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<TypeNode> parTypes = new ArrayList<TypeNode>();
		int paroffset = 1;

		// check parameters
		for (Node a:parList) {
			ParNode par = (ParNode) a;

			STentry parEntry = new STentry(env.getNestLevel(), par.getType(), paroffset++);

			if (par.getType() instanceof ClassTypeNode) {
				boolean classDefined = false;
				HashMap<String,STentry> level_zero = env.getST().get(0);
				String parId = ( (ClassTypeNode)(par.getType()) ).getId();

				for (STentry e : level_zero.values()) {
					if ( e.getType() instanceof ClassTypeNode && ((ClassTypeNode)(e.getType())).getId().equals( parId ) ) {

						ClassTypeNode entryType = (ClassTypeNode)(e.getType());
						
						parEntry.setType( entryType );
						par.setType( entryType );
						classDefined = true;
					}
				}

				if (!classDefined) {
					res.add( new SemanticError("Class " + parId + " has not been defined."));
					return res;
				}
			}

			parTypes.add(par.getType());

			if ( hmn.put( par.getId(), parEntry ) != null  ) {
				res.add( new SemanticError("Parameter name " + par.getId() + " of method " + id + " has already been used.") );
				return res;
			}
		}

		// set function type
		arrowType = new ArrowTypeNode(parTypes, type);
		entry.setType( arrowType );

		if (decList.size() > 0) {
			// check dec list
			env.incNestLevel();
			HashMap<String,STentry> lethm = new HashMap<String,STentry> ();
			env.getST().add(lethm);
			env.setOffset(-2);
			for(Node n:decList)
				res.addAll(n.checkSemantics(env));

			res.addAll(body.checkSemantics(env));

			env.getST().remove(env.decNestLevel());
		} else {
			//check body
			res.addAll(body.checkSemantics(env));
		}

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
				"cfp\n"+ //setta $fp a $sp; this is the Access Link				
				"lra\n"+ //inserimento return address
				declCode+ //inserimento dichiarazioni locali
				body.codeGeneration()+
				"srv\n"+ //pop del return value
				popDecl+
				"sra\n"+ // pop del return address
				"pop\n"+ // pop di AL
				popParl+
				"sfp\n"+  // setto $fp a valore del CL; this is the control link
				"lrv\n"+ // risultato della funzione sullo stack
				"lra\n"+"js\n" // salta a $ra
				);
		
		return "push "+ funl +"\n";
	}

}