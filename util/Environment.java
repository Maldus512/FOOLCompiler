package util;

import java.util.ArrayList;
import java.util.HashMap;
import ast.types.ClassTypeNode;

public class Environment {

	private ArrayList<HashMap<String,STentry>> symTable = new ArrayList<HashMap<String,STentry>>();
	private HashMap<String,ClassTypeNode> classEnv = new HashMap<String,ClassTypeNode>();
	private int nestingLevel;
	private int offset;
	private int classOffset;	// offset for classes

	public Environment() {
		nestingLevel = -1;
		offset = 0;
		classOffset = 0;
	}

	public ArrayList<HashMap<String,STentry>> getST() {
		return symTable;
	}


	public int getNestLevel() {
		return nestingLevel;
	}

	public ClassTypeNode classEnvGet(String key){
		return classEnv.get(key);
	}

	public ClassTypeNode classEnvPut(String key, ClassTypeNode value){
		return classEnv.put(key,value);
	}

	public int incNestLevel() {
		return nestingLevel++;
	}

	public int decNestLevel() {
		return nestingLevel--;
	}

	public int getOffset() {
		return offset;
	}

	public int incOffset() {
		return offset++;
	}

	public int decOffset() {
		return offset--;
	}

	public void setOffset(int n) {
		offset = n;
	}

	public int getClassOffset() {
		return classOffset;
	}

	public int incClassOffset() {
		return classOffset++;
	}

	public int decClassOffset() {
		return classOffset--;
	}

	public void setClassOffset(int n) {
		classOffset = n;
	}

	//livello ambiente con dichiarazioni piu' esterno � 0 (prima posizione ArrayList) invece che 1 (slides)
	//il "fronte" della lista di tabelle � symTable.get(nestingLevel)
}
