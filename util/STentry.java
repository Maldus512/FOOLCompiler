package util;

import ast.types.*;
import ast.*;
public class STentry {
 
	private int nl;
	private TypeNode type;
	private int offset;
	// private ClassNode classInfo;		// needed for inheritance

	public STentry (int n, int os) {
		nl = n;
		offset = os;
	}

	public STentry (int n, TypeNode t, int os) {
		nl = n;
		type = t;
		offset = os;
	}

	// public STentry (int n, int os, ClassNode c) {
	// 	nl = n;
	// 	classInfo = c;
	// 	offset = os;
	// }

	public void setType (TypeNode t) {
		type = t;
	}

	public TypeNode getType () {
		return type;
	}

	public void setOffset (int o) {
		offset = o;
	}

	public int getOffset () {
		return offset;
	}

	public int getNestLevel () {
		return nl;
	}

	// public ClassNode getClassNode() {
	// 	return classInfo;
	// }

	// public void setClassNode(ClassNode c) {
	// 	classInfo = c;
	// }

	public String toPrint(String s) {
		return	s+"STentry: nestlev " + Integer.toString(nl) +"\n"+
				s+"STentry: type\n" +
				type.toPrint(s+"  ") +
				s+"STentry: offset " + Integer.toString(offset) + "\n";
	}
}