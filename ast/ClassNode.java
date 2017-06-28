package ast;
import java.util.ArrayList;
import java.util.HashMap;
import util.STentry;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;
import ast.types.*;


/* Class representing a Classdec instruction node */
public class ClassNode implements Node {

	private String id;
	private ArrayList<Node> fieldList;
	private ArrayList<Node> methodList;
	private String superClassId;
	private ClassTypeNode type;
	
	public ClassNode (String name) {
		id = name;
		fieldList = new ArrayList<Node>();
		methodList = new ArrayList<Node>();
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

	public String getId() { return id; }

	public void addField (Node f) { fieldList.add(f); }

	public void addMethod (FunNode f) {
		MethodNode m = new MethodNode(f.getId(), f.getType(), f.getParList(), f.getDecList(), f.getBody());
		methodList.add(m);
	}

	public void setSuperClass(String id) { superClassId = id; }

	public ClassTypeNode getType() { return type; }

	public ArrayList<Node> getMethodList() { return methodList; }

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		
		// get symtable at nestingLevel, which is now 0
		HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
		// add entry with current nestingLevel at offset 0, and decrement offset
		STentry entry = new STentry(env.getNestLevel(), type, env.decStaticOffset());

		// check if the class has already been declared
		if ( hm.put( id, entry ) != null ) {
			res.add( new SemanticError("Class name '" + id + "' has already been used.") );
			return res;
		}

		env.incNestLevel();	// nestingLevel is now 1

		// create a new hashmap for fields and methods and add it to the symbol table
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		HashMap<String,STentry> fieldEntries = new HashMap<String,STentry>();
		HashMap<String,STentry> methodEntries = new HashMap<String,STentry>();

		int fieldOffset = 0;	// offset for class's fields
		int methodOffset = 0;	// offset for class's methods

		if (superClassId != null) {
			// check if super class has been declared
			STentry superClassEntry = hm.get( superClassId );

			if (superClassEntry == null) {
				res.add( new SemanticError("Superclass '" + superClassId + "' for class '" + id + "' has not been declared."));
				return res;
			}

			HashMap<String,TypeNode> fieldTypeMap = ((ClassTypeNode)(superClassEntry.getType())).getFieldTypeMap();
			
			// add to the subclass's symbol table every field of its superclass
			for ( String key : fieldTypeMap.keySet() ) {

				// create new field STentry with nesting level set to superclass fields' nesting level
				STentry fieldEntry = new STentry( superClassEntry.getNestLevel()+1, fieldTypeMap.get(key), fieldOffset++ );
				
				hmn.put( key, fieldEntry );
				fieldEntries.put ( key, fieldEntry );
			}

			HashMap<String,STentry> methodEntriesMap = ((ClassTypeNode)(superClassEntry.getType())).getMethodEntriesMap();

			// add to the subclass's symbol table every method of its superclass
			for( String key : methodEntriesMap.keySet() ) {
				// MethodNode f = (MethodNode) n;

				// res.addAll( f.checkSemantics(env, methodOffset++, this) );

				hmn.put( key, methodEntriesMap.get(key) );
				methodEntries.put( key, methodEntriesMap.get(key) );
				methodOffset++;
			}

		}


		
		// Overriding of fields is handled by using a temporary hash table where to insert already declared fields, only for the subclass: if a double entry is found then the field has already been defined for that class.

		// check fields
		HashMap<String,STentry> temp = new HashMap<String,STentry>();

		for (Node f:fieldList) {

			FieldNode field = (FieldNode) f;
			STentry fieldEntry = new STentry( env.getNestLevel(), field.getType(), fieldOffset++ );
			
			fieldEntries.put ( field.getId(), fieldEntry );
			STentry prevEntry = hmn.put( field.getId(), fieldEntry );
			if ( prevEntry != null) {

				if (temp.put( field.getId(), null) != null) {	// field has been redeclared twice within the same class
					res.add( new SemanticError("Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
					return res;
				}

				// if we are here, we are overriding a field, thus we must update its offset accordingly
				fieldEntry.setOffset( prevEntry.getOffset() );
				hmn.put( field.getId(), fieldEntry );
				fieldEntries.put ( field.getId(), fieldEntry );
				fieldOffset--;
			}
		}

		
		
		// Overriding of methods is handled in the same way as for fields.
		
		// check semantics of class's methods
		temp = new HashMap<String,STentry> ();
		
		for(Node n:methodList) {
			MethodNode f = (MethodNode) n;
			int methodNumber = hmn.size();

			if ( temp.put( f.getId(), null) != null ) {
				res.add( new SemanticError("Method name '" + f.getId() + "' for class '" + id + "' has already been used.") );
				return res;
			}
			
			res.addAll( f.checkSemantics(env, methodOffset++) );

			methodEntries.put( f.getId(), f.getEntry() );

			// if a method has been overrided, hmn was not changed, thus we must adjust methodOffset to correct offset
			if (methodNumber == hmn.size())
				methodOffset--;
		}

		type = new ClassTypeNode(id, fieldEntries, methodEntries);
		entry.setType( type );


		// // DEBUG
		// System.out.println("\n###############");
		// System.out.println("Class:  " + id);
		// System.out.println("\tFields:");
		// for (Node n : fieldList) {
		// 	FieldNode f = (FieldNode)n;
		// 	System.out.println( "\t\tId: " + f.getId() + ", NestLevel: " + hmn.get(f.getId()).getNestLevel() + ", Offset: " + hmn.get(f.getId()).getOffset() );
		// }
		// System.out.println("\tMethods:");
		// for (Node n : methodList) {
		// 	MethodNode m = (MethodNode)n;
		// 	System.out.println( "\t\tId: " + m.getId() + ", NestLevel: " + hmn.get(m.getId()).getNestLevel() + ", Offset: " + hmn.get(m.getId()).getOffset() );
		// }
		// System.out.println("###############\n");

		//close scope
		env.getST().remove(env.decNestLevel());

		return res;
	}

	public TypeNode typeCheck(Environment env) {
		if (superClassId != null) {

			HashMap<String,STentry> hm = env.getST().get(0);

			ClassTypeNode superType = (ClassTypeNode)( (hm.get( superClassId )).getType() );

			if ( !FOOLlib.isSubtype(type, superType) ) {
				System.out.println("Error: " + id + " is not a subclass of " + superType.getId());
				return new BottomTypeNode();
			}
		}

		return type;
	}

	public String codeGeneration() {
		String methods ="";
		for(Node m : methodList) {
			methods += m.codeGeneration();
		}
		return methods;
	} 
}  
