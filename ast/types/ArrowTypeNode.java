package ast.types;

import java.util.ArrayList;

import ast.Node;

/* node for a function type */
public class ArrowTypeNode extends TypeNode {
	private ArrayList<TypeNode> parList;
	private TypeNode ret;

	public ArrowTypeNode(ArrayList<TypeNode> p, TypeNode r) {
		parList = p;
		ret = r;
	}

	public String toPrint(String s) {
		String parstr = "";

		for (Node par : parList)
			parstr += par.toPrint(s + "  ");

		return s + "ArrowType\n" + parstr + ret.toPrint(s + "  ->");
	}

	public TypeNode getRet() {
		return ret;
	}

	public ArrayList<TypeNode> getParList() {
		return parList;
	}
}
