package ast;

import java.util.ArrayList;
import java.util.HashMap;
import util.STentry;

import util.Environment;
import util.SemanticError;
import ast.types.*;

public class FieldNode implements Node {

	private String id;
	private TypeNode type;

	public FieldNode(String i, TypeNode t) {
		id = i;
		type = t;
	}

	public String getId() {
		return id;
	}

	public TypeNode getType() {
		return type;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		return checkSemantics(env, 0);
	}

	public ArrayList<SemanticError> checkSemantics(Environment env, int offset) {

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		HashMap<String, STentry> hm = env.getST().get(env.getNestLevel());
		STentry entry = new STentry(env.getNestLevel(), offset);

		if (type instanceof ClassTypeNode) {
			String classId = ((ClassTypeNode) type).getId();
			ClassTypeNode fullClassType = env.classTypeEnvGet(classId);
			boolean field = type.isField();

			if (fullClassType == null) {
				res.add(new SemanticError("Class " + classId + " has not been defined; field is not accessible."));
				return res;
			}
			type = new ClassTypeNode(fullClassType);
			type.isField(field);

		}
		entry.setType(type);

		STentry prevEntry = hm.put(id, entry);
		if (prevEntry != null) {
			// if we are here, we are overriding a field, thus we must update its offset accordingly
			entry.setOffset(prevEntry.getOffset());
		}

		hm.put(id, entry);

		return res;
	}

	public String toPrint(String s) {
		return s + "Field:" + id + "\n" + type.toPrint(s + "  ");
	}

	//non utilizzato
	public TypeNode typeCheck(Environment env) {
		return new VoidTypeNode();
	}

	//non utilizzato
	public String codeGeneration() {
		return "";
	}

}