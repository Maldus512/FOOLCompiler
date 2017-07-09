package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import util.STentry;

import util.FOOLlib;
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

	public ClassNode(String name) {
		id = name;
		fieldList = new ArrayList<Node>();
		methodList = new ArrayList<Node>();
	}

	public String toPrint(String s) {
		String fieldstr = "", methodstr = "", superstr = "";

		for (Node n : fieldList) {
			fieldstr += n.toPrint(s + "  ");
		}

		for (Node method : methodList) {
			methodstr += method.toPrint(s + "  ");
		}

		if (superClassId != null)
			superstr += s + "  " + "Implements:" + superClassId + "\n";

		return s + "Class:" + id + "\n" + superstr + fieldstr + methodstr;
	}

	public String getId() {
		return id;
	}

	public void addField(Node f) {
		fieldList.add(f);
	}

	public void addMethod(FunNode f) {
		MethodNode m = new MethodNode(f.getId(), f.getType(), id, f.getParList(), f.getDecList(), f.getBody());
		methodList.add(m);
	}

	public void setSuperClass(String id) {
		superClassId = id;
	}

	public ArrayList<Node> getMethodList() {
		return methodList;
	}

	public ArrayList<Node> getFieldList() {
		return fieldList;
	}

	public ClassTypeNode getType() {
		return type;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hmn = new HashMap<String, STentry>();

		HashMap<Integer, Node> methodsByOffset = new HashMap<Integer, Node>();
		HashMap<String, Integer> methodOffsetByName = new HashMap<String, Integer>();
		HashMap<Integer, Node> fieldsByOffset = new HashMap<Integer, Node>();
		HashMap<String, Integer> fieldOffsetByName = new HashMap<String, Integer>();
		HashMap<String, STentry> fieldEntries = new HashMap<String, STentry>();
		HashMap<String, STentry> methodEntries = new HashMap<String, STentry>();

		int fieldOffset = 0; // offset for class' fields
		int methodOffset = 0; // offset for class' methods

		env.incNestLevel(); // nestingLevel is now 
		// int classOffset = env.getClassOffset();

		// check if the class has already been declared
		if (env.classTypeEnvPut(id, null) != null) {
			res.add(new SemanticError("Class name '" + id + "' has already been used."));
			env.getST().remove(env.decNestLevel());
			return res;
		}

		// create a new hashmap for fields and methods and add it to the symbol table
		env.getST().add(hmn);

		/***************************/
		/** INHERITANCE			   */
		/***************************/

		if (superClassId != null) {
			// If we are a subclass we need to fill fieldList and methodList with the superclass' fields and methods
			ClassTypeNode superClassType = env.classTypeEnvGet(superClassId);
			ClassNode superClass = env.classEnvGet(superClassId);

			if (superClassType == null) {
				res.add(new SemanticError(
						"Superclass '" + superClassId + "' for class '" + id + "' has not been declared."));
				env.getST().remove(env.decNestLevel());
				return res;
			}

			HashMap<String, STentry> fieldEntryMap = superClassType.getFieldEntriesMap();

			// add to the subclass's symbol table every field of its superclass
			for (String key : fieldEntryMap.keySet()) {
				// create new field STentry with nesting level set to superclass fields' nesting level
				STentry fieldEntry = new STentry(env.getNestLevel(), fieldEntryMap.get(key).getType(), fieldOffset++);

				hmn.put(key, fieldEntry);
				fieldEntries.put(key, fieldEntry);
			}
			fieldOffset = 0;

			if (superClass != null && superClassType != null) {
				HashMap<String, STentry> methodEntriesMap = superClassType.getMethodEntriesMap();

				// add to the subclass's symbol table every method of its superclass
				for (String key : methodEntriesMap.keySet()) {
					hmn.put(key, methodEntriesMap.get(key));
					methodEntries.put(key, methodEntriesMap.get(key));
				}

				for (Node m : superClass.getMethodList()) {
					methodsByOffset.put(methodOffset, m);
					methodOffsetByName.put(((MethodNode) m).getId(), methodOffset);
					methodOffset++;
				}

				for (Node m : superClass.getFieldList()) {
					fieldsByOffset.put(fieldOffset, m);
					fieldOffsetByName.put(((FieldNode) m).getId(), fieldOffset);
					fieldOffset++;
				}
			}
		}

		/***************************/
		/** SEMANTIC CHECK (FIELDS)*/
		/***************************/

		// Overriding of fields is handled by using a temporary hash table where to insert already declared fields,
		// only for the subclass: if a double entry is found then the field has already been defined for that class.
		// check fields
		HashMap<String, STentry> temp = new HashMap<String, STentry>();

		for (Node f : fieldList) {
			FieldNode field = (FieldNode) f;
			int fieldNumber = hmn.size();

			if (temp.put(field.getId(), new STentry(-1, -1)) != null) {
				res.add(new SemanticError(
						"Field name '" + field.getId() + "' for class '" + id + "' has already been used."));
				env.getST().remove(env.decNestLevel());
				return res;
			}

			res.addAll(field.checkSemantics(env, fieldOffset));
			fieldEntries.put(field.getId(), hmn.get(field.getId()));

			// if a field has been overridden....., hmn was not changed, thus we must adjust methodOffset to correct offset
			if (fieldNumber == hmn.size()) {
				fieldsByOffset.put(fieldOffsetByName.get(field.getId()), field);
			} else {
				fieldsByOffset.put(fieldOffset, field);
				fieldOffset++;
			}
		}

		TypeNode[] orderedFieldTypes = new TypeNode[fieldEntries.size()];

		for (String key : fieldEntries.keySet()) {
			orderedFieldTypes[fieldEntries.get(key).getOffset()] = fieldEntries.get(key).getType();
		}

		//if we have no superclass this step is useless
		Node[] orderedFields = new Node[fieldsByOffset.size()];

		for (Integer key : fieldsByOffset.keySet()) {
			orderedFields[key] = fieldsByOffset.get(key);
		}

		/****************************/
		/** SEMANTIC CHECK (METHODS)*/
		/****************************/

		// Overriding of methods is handled in the same way as for fields.
		temp = new HashMap<String, STentry>();

		for (Node n : methodList) {
			MethodNode m = (MethodNode) n;
			int methodNumber = hmn.size();

			if (temp.put(m.getId(), new STentry(-1, -1)) != null) {
				res.add(new SemanticError(
						"Method name '" + m.getId() + "' for class '" + id + "' has already been used."));
				env.getST().remove(env.decNestLevel());
				return res;
			}

			/** The offset to insert in the STEntry is:
			 * The current class offset to reach the symbol table;
			 * The current method offset to reach the method;
			 * -1 to step over the constructor (first method in each class)
			 */

			if (methodEntries.put(m.getId(), new STentry(-1, -1)) != null) {
				res.addAll(m.checkSemantics(env, env.getClassOffset() - methodOffsetByName.get(m.getId()) - 1));
			} else {
				res.addAll(m.checkSemantics(env, env.getClassOffset() - methodOffset - 1));
			}
			methodEntries.put(m.getId(), hmn.get(m.getId()));

			// if a method has been overrided, hmn was not changed, 
			// thus we must adjust methodOffset to correct offset
			if (methodNumber == hmn.size() && methodOffsetByName.get(m.getId()) != null) {
				//methodOffset--;
				methodsByOffset.put(methodOffsetByName.get(m.getId()), m);
			} else {
				methodsByOffset.put(methodOffset, m);
				methodOffset++;
			}
		}

		//if we have no superclass this step is useless
		Node[] orderedMethods = new Node[methodsByOffset.size()];

		for (Integer key : methodsByOffset.keySet()) {
			orderedMethods[key] = methodsByOffset.get(key);
		}

		//Replace previous lists with ordered ones
		methodList = new ArrayList<Node>(Arrays.asList(orderedMethods));
		fieldList = new ArrayList<Node>(Arrays.asList(orderedFields));

		type = new ClassTypeNode(id, fieldEntries, methodEntries);
		env.classTypeEnvPut(id, type);
		env.classEnvPut(id, this);

		env.getST().remove(env.decNestLevel());

		ArrowTypeNode constructor = new ArrowTypeNode(new ArrayList<TypeNode>(Arrays.asList(orderedFieldTypes)), type);

		//Nesting level should ALWAYS be 0 here. We refer to it as env.getNestLevel()
		//for coherence purposes.
		env.getST().get(env.getNestLevel()).put(id, new STentry(env.getNestLevel(), constructor, env.getClassOffset()));

		return res;
	}

	public TypeNode typeCheck(Environment env) {

		for (Node method : methodList) {
			if (method.typeCheck(env) instanceof BottomTypeNode) {
				return new BottomTypeNode();
			}
		}

		if (superClassId != null) {

			ClassTypeNode superType = env.classTypeEnvGet(superClassId);

			if (!FOOLlib.isSubtype(type, superType)) {
				System.out.println("Error: " + id + " is not a subclass of " + superType.getId());
				return new BottomTypeNode();
			}
		}

		return type;
	}

	public String constructorCodeGeneration() {

		String initFields = "";
		String popParl = "";
		int offset = 1;
		for (Node dec : fieldList) {
			popParl += "pop\n"; //Pop each parameter/field
			initFields += "lfp\n" + "push " + offset + "\n" + "add\n" + // Each field is offset away from the FP
					"lw\n" + "lfp\n" + "push -2\n" + // The heap pointer of the object is -2 from
					"add\n" + // the FP
					"lw\n" + "push " + (offset - 1) + "\n" + "add\n" + "sw\n";
			offset++;
		}

		String funl = id;
		FOOLlib.putCode(funl + ":\n" + "cfp\n" + //setta $fp a $sp; this is the Access Link				
				"lra\n" + //inserimento return address
				"mall " + fieldList.size() + "\n" + initFields + "srv\n" + //pop del return value
				"sra\n" + // pop del return address
				"pop\n" + // pop di AL
				popParl + "sfp\n" + // setto $fp a valore del CL; this is the control link
				"lrv\n" + // risultato della funzione sullo stack
				"lra\n" + "js\n" // salta a $ra
		);
		return "push " + funl + "\n";
	}

	public String codeGeneration() {
		String methods = "";
		//The string methods is just a list of "push labeln"
		//The actual code is put by the recursive call of codeGeneration
		//in the static string of FOOLlib
		methods += constructorCodeGeneration();
		for (Node m : methodList) {
			methods += m.codeGeneration();
		}
		return methods;
	}
}
