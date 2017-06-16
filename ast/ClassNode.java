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
	private ArrayList<Node> methodList;
	private String superClassId;
	
	public ClassNode (String name) {
		id = name;
		fieldList = new ArrayList<Node>();
		methodList = new ArrayList<Node>();
	}

	public String getId() {
		return id;
	}

	public String toPrint(String s) {
		String  fieldstr = "",
				methodstr = "",
				superstr = "";

		for (Node n:fieldList) {
			fieldstr += n.toPrint(s+"  ");
		}

		for (Node method:methodList) {
			methodstr+=method.toPrint(s+"  ");
		}

		if (superClassId != null)
			superstr += s + "  " + "Implements:" + superClassId + "\n";

		return s + "Class:" + id + "\n"
			+ superstr
			+ fieldstr
			+ methodstr
		;
	}

	public void addField (Node f) {
		fieldList.add(f);
	}

	public void addMethod (Node m) {
		methodList.add(m);
	}

	public void setSuperClass(String id) {
		superClassId = id;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		
		// get symtable at nestingLevel, which is now 0
		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		// add entry with current nestingLevel at offset 0, and decrement offset
		STentry entry = new STentry(env.getNestLevel(), env.decStaticOffset(), this);

		// check if the class has already been declared
		if ( hm.put( id, entry ) != null ) {
			res.add( new SemanticError("Class name '" + id + "' has already been used.") );
			return res;
		}

		// System.out.println("Class: " + id + ", off: " + (env.getStaticOffset()+1) + ", nestlevel: " + env.getNestLevel());

		env.incNestLevel();	// nestingLevel is now 1

		// create a new hashmap for fields and methods and add it to the symbol table
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<Node> fieldTypes = new ArrayList<Node>();

		int fieldOffset = 0;	// offset for class's fields
		int methodOffset = 0;	// offset for class's methods

		if (superClassId != null) {
			// check if super class has been declared
			STentry superClassEntry = hm.get( superClassId );

			if (superClassEntry == null) {
				res.add( new SemanticError("Superclass '" + superClassId + "' for class '" + id + "' has not been declared."));
				return res;
			}
			
			// add to the subclass's symbol table every field of its superclass
			for (Node f:superClassEntry.getClassNode().fieldList) {
				FieldNode field = (FieldNode) f;
				fieldTypes.add(field.getType());

				// add fields with nesting level set to superclass fields' nesting level
				hmn.put( field.getId(), new STentry(superClassEntry.getNestLevel()+1, field.getType(), fieldOffset++ ) );
			}

			// add to the subclass's symbol table every method of its superclass
			for(Node n:superClassEntry.getClassNode().methodList) {
				FunNode f = (FunNode) n;
				res.addAll( f.checkSemantics(env, methodOffset++, this) );
			}

		}

		// check fields
		for (Node f:fieldList) {
			FieldNode field = (FieldNode) f;
			fieldTypes.add(field.getType());

			// Assumption: cannot redefine fields of subclasses
			if ( hmn.put( field.getId(), new STentry(env.getNestLevel(), field.getType(), fieldOffset++ ) ) != null  ) {
				res.add( new SemanticError("Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
				return res;
			}
		}

		// TODO: define a ClassTypeNode, similar to ArrowTypeNode
		// entry.addType( new ClassTypeNode(fieldTypes, type) );

		// check semantics of class's methods
		for(Node n:methodList) {
			FunNode f = (FunNode) n;
			res.addAll( f.checkSemantics(env, methodOffset++, this) );
		}

		//close scope
		env.getST().remove(env.decNestLevel());

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
