package util;

import java.util.ArrayList;
import java.util.HashMap;

import ast.STentry;

public class Environment {

	private static Environment instance = null;
	private ArrayList<HashMap<String,STentry>> symTable = new ArrayList<HashMap<String,STentry>>();
	private int nestingLevel = -1;
	private int offset = 0;

	protected Environment() {}

	public static Environment getInstance() {
		if (instance == null) {
			instance = new Environment();
		}
		return instance;
	}

	public ArrayList<HashMap<String,STentry>> getST() {
		return symTable;
	}

	public int getNestLevel() {
		return nestingLevel;
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

	//livello ambiente con dichiarazioni piu' esterno � 0 (prima posizione ArrayList) invece che 1 (slides)
	//il "fronte" della lista di tabelle � symTable.get(nestingLevel)
}
