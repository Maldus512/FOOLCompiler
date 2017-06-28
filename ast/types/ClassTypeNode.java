package ast.types;
import java.util.ArrayList;
import java.util.HashMap;
import util.STentry;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode extends TypeNode {

	private String id;
	private HashMap<String,STentry> fieldEntries;
	private HashMap<String,STentry> methodEntries;

	public ClassTypeNode (String i, HashMap<String,STentry> f, HashMap<String,STentry> m) {
		id = i;
		fieldEntries = f;
		methodEntries = m;
	}

	public String getId() {
		return id;
	}

	// public ArrayList<String> getMethodIds() {
	// 	return new ArrayList<String>(methodTypeMap.keySet());
	// }

	// public ArrayList<TypeNode> getFieldTypeList() {
	// 	return new ArrayList<TypeNode>(fieldTypeMap.values());
	// }

	// public ArrayList<TypeNode> getMethodTypeList() {
	// 	return new ArrayList<TypeNode>(methodTypeMap.values());
	// }

	public HashMap<String,STentry> getFieldEntriesMap() {
		return fieldEntries;
	}

	public HashMap<String,STentry> getMethodEntriesMap() {
		return methodEntries;
	}

	public HashMap<String,TypeNode> getFieldTypeMap(){
		HashMap<String,TypeNode> fieldTypeMap = new HashMap<String,TypeNode>();

		for (String key : fieldEntries.keySet())
			fieldTypeMap.put( key, ((STentry)(fieldEntries.get(key))).getType() );

		return fieldTypeMap;
	}

	public HashMap<String,ArrowTypeNode> getMethodTypeMap(){
		HashMap<String,ArrowTypeNode> methodTypeMap = new HashMap<String,ArrowTypeNode>();

		for (String key : methodEntries.keySet())
			methodTypeMap.put( key, (ArrowTypeNode)(((STentry)(methodEntries.get(key))).getType()) );

		return methodTypeMap;
	}

	@Override
	public String toPrint(String s) {
		String  fieldstr = "",
				methodstr = "";

		for (String key : fieldEntries.keySet())
			fieldstr += ((TypeNode)(fieldEntries.get(key).getType())).toPrint(s + "  ");

		for (String key : methodEntries.keySet())
			methodstr += ((TypeNode)(methodEntries.get(key).getType())).toPrint(s + "  ");

		return s + "ClassType\n"
				+ fieldstr
				+ methodstr
			; 
	}
}  
