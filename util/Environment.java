package util;

import java.util.ArrayList;
import java.util.HashMap;
import ast.ClassNode;
import ast.types.ClassTypeNode;

public class Environment {

	private ArrayList<HashMap<String,STentry>> symTable = new ArrayList<HashMap<String,STentry>>();
	private HashMap<String,ClassTypeNode> classTypeEnv = new HashMap<String,ClassTypeNode>();
	private HashMap<String,ClassNode> classEnv = new HashMap<String,ClassNode>();
	private int nestingLevel;
	private int offset;
	private int classOffset;	// offset for classes

	public Environment() {
		nestingLevel = -1;
		offset = 0;
		classOffset = -2;
	}

	public ArrayList<HashMap<String,STentry>> getST() {
		return symTable;
	}

	public int getLastNestLevel() {
		return symTable.size()-1;
	}

	public int getNestLevel() {
		return nestingLevel;
	}

	public ClassTypeNode classTypeEnvGet(String key){
		return classTypeEnv.get(key);
	}

	public ClassTypeNode classTypeEnvPut(String key, ClassTypeNode value){
		return classTypeEnv.put(key,value);
	}

	public ClassNode classEnvGet(String key){
		return classEnv.get(key);
	}

	public ClassNode classEnvPut(String key, ClassNode value){
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
