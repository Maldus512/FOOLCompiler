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
	private ClassTypeNode classType;
	
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

	public void addMethod (FunNode f) {
		MethodNode m = new MethodNode(f.getId(), f.getType(), f.getParList(), f.getDecList(), f.getBody());
		methodList.add(m);
	}

	public void setSuperClass(String id) {
		superClassId = id;
	}

	public ClassTypeNode getClassType() {
		return classType;
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

		env.incNestLevel();	// nestingLevel is now 1

		// create a new hashmap for fields and methods and add it to the symbol table
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		ArrayList<Node> fieldTypes = new ArrayList<Node>();
		ArrayList<ArrowTypeNode> methodTypes = new ArrayList<ArrowTypeNode>();

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
				MethodNode f = (MethodNode) n;

				res.addAll( f.checkSemantics(env, methodOffset++, this) );

				methodTypes.add(f.getArrowType());
			}

		}

		/*
			Overriding of fields is handled by using a temporary hash table where to insert already declared fields, only for the subclass: if a double entry is found then the field has already been defined for that class.
		*/

		// check fields
		HashMap<String,STentry> temp = new HashMap<String,STentry> ();

		for (Node f:fieldList) {

			FieldNode field = (FieldNode) f;
			fieldTypes.add(field.getType());

			STentry prevEntry = hmn.put( field.getId(), new STentry(env.getNestLevel(), field.getType(), fieldOffset++ ) );
			if ( prevEntry != null) {

				if (temp.put( field.getId(), null) != null) {	// field has been redeclared twice within the same class
					res.add( new SemanticError("Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
					return res;
				}

				// if we are here, we are overriding a field, thus we must update its offset accordingly
				hmn.put( field.getId(), new STentry(env.getNestLevel(), field.getType(), prevEntry.getOffset()) );
				fieldOffset--;
			}
		}

		classType = new ClassTypeNode(id, fieldTypes, methodTypes);
		entry.addType( classType );


		/*
			Overriding of methods is handled by setting, for each method, an "owner class", corresponding to the first class which declares such method: if a class defines a method but an entry is already present, the owner class is checked: if the owner is the class itself, the method has been redefined within the same class.
			Note: this could be done in the same way as for fields, but this is more metal. \m/_
		*/

		// check semantics of class's methods
		for(Node n:methodList) {
			MethodNode f = (MethodNode) n;
			
			res.addAll( f.checkSemantics(env, methodOffset++, this) );

			methodTypes.add(f.getArrowType());

			// adjust methodOffset to correct offset
			methodOffset = f.getOffset();
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
