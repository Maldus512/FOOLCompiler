package util;

import java.util.ArrayList;
import java.util.HashMap;


public class Environment {

	private ArrayList<HashMap<String,STentry>> symTable = new ArrayList<HashMap<String,STentry>>();
	private int nestingLevel;
	private int offset;
	private int staticOffset;	// offset for classes

	public Environment() {
		nestingLevel = -1;
		offset = 0;
		staticOffset = 0;
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

	public int getStaticOffset() {
		return staticOffset;
	}

	public int incStaticOffset() {
		return staticOffset++;
	}

	public int decStaticOffset() {
		return staticOffset--;
	}

	public void setStaticOffset(int n) {
		staticOffset = n;
	}

	//livello ambiente con dichiarazioni piu' esterno � 0 (prima posizione ArrayList) invece che 1 (slides)
	//il "fronte" della lista di tabelle � symTable.get(nestingLevel)
}
