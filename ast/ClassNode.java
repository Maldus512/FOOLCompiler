package ast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
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
		
		env.incNestLevel();	// nestingLevel is now 1

		// check if the class has already been declared
		if ( env.classEnvPut( id, null ) != null ) {
			res.add( new SemanticError("Class name '" + id + "' has already been used.") );
			env.getST().remove(env.decNestLevel());
			return res;
		}

		

		// create a new hashmap for fields and methods and add it to the symbol table
		HashMap<String,STentry> hmn = new HashMap<String,STentry> ();
		env.getST().add(hmn);

		HashMap<String,STentry> fieldEntries = new HashMap<String,STentry>();
		HashMap<String,STentry> methodEntries = new HashMap<String,STentry>();

		int fieldOffset = 0;	// offset for class's fields
		int methodOffset = 0;	// offset for class's methods

		if (superClassId != null) {
			// check if super class has been declared
			ClassTypeNode superClass = env.classEnvGet( superClassId );

			if (superClass == null) {
				res.add( new SemanticError("Superclass '" + superClassId + "' for class '" + id + "' has not been declared."));
				env.getST().remove(env.decNestLevel());
				return res;
			}

			HashMap<String,STentry> fieldEntryMap = superClass.getFieldEntriesMap();

			// add to the subclass's symbol table every field of its superclass
			for ( String key : fieldEntryMap.keySet() ) {


				// create new field STentry with nesting level set to superclass fields' nesting level
				STentry fieldEntry = new STentry( env.getNestLevel(), fieldEntryMap.get(key).getType(), fieldOffset++ );
				
				hmn.put( key, fieldEntry );
				fieldEntries.put ( key, fieldEntry );
			}


			HashMap<String,STentry> methodEntriesMap = superClass.getMethodEntriesMap();

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
			int fieldNumber = hmn.size();

			if (temp.put(field.getId(), new STentry(-1, -1)) != null) {
				res.add( new SemanticError("Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
				env.getST().remove(env.decNestLevel());
				return res;
			}

			res.addAll(field.checkSemantics(env, fieldOffset++));

			fieldEntries.put( field.getId(), hmn.get(field.getId()) );

			// if a field has been overrided....., hmn was not changed, thus we must adjust methodOffset to correct offset
			if (fieldNumber == hmn.size())
				fieldOffset--;
		}

		TypeNode[] orderedFields = new TypeNode[fieldEntries.size()];

		for (String key : fieldEntries.keySet()){
			orderedFields[fieldEntries.get(key).getOffset()] = fieldEntries.get(key).getType();
		}

		// Overriding of methods is handled in the same way as for fields.
		
		// check semantics of class's methods
		temp = new HashMap<String,STentry> ();
		
		for(Node n:methodList) {
			MethodNode f = (MethodNode) n;
			int methodNumber = hmn.size();

			if ( temp.put( f.getId(), new STentry(-1, -1) ) != null ) {
				res.add( new SemanticError("Method name '" + f.getId() + "' for class '" + id + "' has already been used.") );
				env.getST().remove(env.decNestLevel());
				return res;
			}
			
			res.addAll( f.checkSemantics(env, methodOffset++) );

			methodEntries.put( f.getId(), hmn.get(f.getId()) );

			// if a method has been overrided, hmn was not changed, thus we must adjust methodOffset to correct offset
			if (methodNumber == hmn.size())
				methodOffset--;
		}


		type = new ClassTypeNode(id, fieldEntries, methodEntries);
		env.classEnvPut( id, type );

		env.getST().remove(env.decNestLevel());

		ArrowTypeNode constructor = 
			new ArrowTypeNode(new ArrayList<TypeNode>(Arrays.asList(orderedFields)), type );

		//Nesting level should ALWAYS be 0 here. We refer to it as env.getNestLevel()
		//for coherence purposes.
			//TODO: Gestire bene gli offset.
		env.getST().get(env.getNestLevel()).put( id, new STentry(env.getNestLevel(), constructor, env.getClassOffset() ));

		return res;
	}

	public TypeNode typeCheck(Environment env) {
		if (superClassId != null) {

			ClassTypeNode superType = env.classEnvGet(superClassId);

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
