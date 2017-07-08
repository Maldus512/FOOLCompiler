package ast.types;

import java.util.HashMap;
import util.STentry;

public class ClassTypeNode extends TypeNode {

	private String id;
	private HashMap<String, STentry> fieldEntries;
	private HashMap<String, STentry> methodEntries;

	public ClassTypeNode(ClassTypeNode clone) {
		id = clone.getId();
		fieldEntries = clone.getFieldEntriesMap();
		methodEntries = clone.getMethodEntriesMap();
		isField = clone.isField();
	}

	public ClassTypeNode(String i, HashMap<String, STentry> f, HashMap<String, STentry> m) {
		id = i;
		fieldEntries = f;
		methodEntries = m;
	}

	public String getId() {
		return id;
	}

	public HashMap<String, STentry> getFieldEntriesMap() {
		return fieldEntries;
	}

	public HashMap<String, STentry> getMethodEntriesMap() {
		return methodEntries;
	}

	public HashMap<String, TypeNode> getFieldTypeMap() {
		HashMap<String, TypeNode> fieldTypeMap = new HashMap<String, TypeNode>();

		for (String key : fieldEntries.keySet())
			fieldTypeMap.put(key, fieldEntries.get(key).getType());

		return fieldTypeMap;
	}

	public HashMap<String, ArrowTypeNode> getMethodTypeMap() {
		HashMap<String, ArrowTypeNode> methodTypeMap = new HashMap<String, ArrowTypeNode>();

		for (String key : methodEntries.keySet())
			methodTypeMap.put(key, (ArrowTypeNode) methodEntries.get(key).getType());

		return methodTypeMap;
	}

	@Override
	public String toPrint(String s) {
		String fieldstr = "", methodstr = "";

		for (String key : fieldEntries.keySet())
			fieldstr += fieldEntries.get(key).getType().toPrint(s + "  ");

		for (String key : methodEntries.keySet())
			methodstr += methodEntries.get(key).getType().toPrint(s + "  ");

		return s + "ClassType\n" + fieldstr + methodstr;
	}
}
