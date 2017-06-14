package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;


/* Class representing a Classdec instruction node */
public class ClassNode implements Node {

	private String id;
	private ArrayList<Node> fieldList;
	private ArrayList<FunNode> methodList;
	
	public ClassNode (String name) {
		id = name;
		fieldList = new ArrayList<Node>();
		methodList = new ArrayList<FunNode>();
	}

	public String toPrint(String s) {
		String  fieldstr = "",
				methodstr = "";

		for (Node n:fieldList) {
			fieldstr += n.toPrint(s+"  ");
		}

		// Printing functions declarations requires a working
		// semantic check to calculate nesting levels.
		
		for (Node method:methodList) {
			methodstr+=method.toPrint(s+"  ");
		}

		return s + "Class:" + id + "\n"
		+ fieldstr
		+ methodstr
		;
	}

	public void addField (Node f) {
		fieldList.add(f);
	}

	public void addMethod (FunNode m) {
		methodList.add(m);
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		// // res.add(new SemanticError("############ PROVA ###########"));

		//env.offset = -2;

		// get symtablet at nestingLevel, which is now 0
		HashMap<String,STentry> hm = env.getInstance().getST().get(env.getInstance().getNestLevel());
		// add entry with current nestingLevel at offset 0, and decrement offset
		STentry entry = new STentry(env.getInstance().getNestLevel(), env.getInstance().decStaticOffset());

		// check if the class has already been declared
		if ( hm.put( id, entry ) != null ) {
			res.add( new SemanticError("Class name '" + id + "' has already been used.") );
			return res;
		}
		
		env.getInstance().incNestLevel();	// nestingLevel is now 1

		// create a new hashmap and add it to the symbol table
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getInstance().getST().add(hmn);

		ArrayList<Node> fieldTypes = new ArrayList<Node>();
		int fieldOffset = 0;

		// check fields
		for (Node f:fieldList) {
			FieldNode field = (FieldNode) f;
			fieldTypes.add(field.getType());

			if ( hmn.put( field.getId(), new STentry(env.getInstance().getNestLevel(), field.getType(), fieldOffset++ ) ) != null  ) {
				res.add( new SemanticError("Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
				return res;
			}
		}

		// TODO: must define a ClassTypeNode, similar to ArrowTypeNode
		// entry.addType( new ClassTypeNode(fieldTypes, type) );

		int methodOffset = 0;
		
		// check semantics of class's methods
		for(FunNode n:methodList) {
			// if ( hmn.put( n.getId(), new STentry(env.getInstance().getNestLevel(), n.getType(), methodOffset++ ) ) != null  ) {
			// 	res.add( new SemanticError("Method name '" + n.getId() + "' for class '" + id + "' has already been used.") );
			// 	return res;
			// }

			res.addAll(n.checkSemantics(env, methodOffset++));
		}

		//close scope
		env.getInstance().getST().remove(env.getInstance().decNestLevel());

		return res;
	}

	public Node typeCheck () {
		// not used
		return null;
	}

	public String codeGeneration() {
		
		return  "halt\n";
	} 
}  
