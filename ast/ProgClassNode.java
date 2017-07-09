package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.FOOLlib;
import util.Environment;
import util.SemanticError;
import util.STentry;

import ast.types.*;

/* Class representing a Classexp instruction node */
public class ProgClassNode implements Node {

	private ArrayList<ClassNode> classList;
	private ArrayList<Node> decList;
	private Node exp;

	public ProgClassNode(ArrayList<ClassNode> l, ArrayList<Node> d, Node e) {
		classList = l;
		decList = d;
		exp = e;
	}

	public String toPrint(String s) {
		String fieldstr = "", decstr = "";

		if (decList.size() > 0) {
			for (Node d : decList)
				decstr += d.toPrint(s + "  ");
		}

		for (ClassNode c : classList)
			fieldstr += c.toPrint(s + "  ");

		return s + "ProgClass\n" + fieldstr + decstr + exp.toPrint(s + "  ");
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		env.incNestLevel(); // nestingLevel is now 0
		env.setClassOffset(-2);

		// create a new hashmap and add it to the symbol table
		HashMap<String, STentry> hm = new HashMap<String, STentry>();
		env.getST().add(hm);

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		int initialClassOffset = env.getClassOffset();
		// check semantics for every class declaration
		for (ClassNode n : classList) {
			res.addAll(n.checkSemantics(env));
			initialClassOffset -= n.getMethodList().size() + 1;
			env.setClassOffset(initialClassOffset);
		}

		// if there are lets
		if (decList.size() > 0) {
			env.setOffset(env.getClassOffset());

			for (Node n : decList)
				res.addAll(n.checkSemantics(env));
		}

		if (res.size() > 0)
			return res;

		//check semantics in the exp node
		res.addAll(exp.checkSemantics(env));

		// leave the class scope
		//env.getST().remove(env.decNestLevel());
		env.decNestLevel();

		return res;
	}

	@Override
	public TypeNode typeCheck(Environment env) {
		for (ClassNode c : classList) {
			if (c.typeCheck(env) instanceof BottomTypeNode) {
				return new BottomTypeNode();
			}
		}

		for (Node d : decList) {
			if (d.typeCheck(env) instanceof BottomTypeNode) {
				return new BottomTypeNode();
			}
		}

		return exp.typeCheck(env);
	}

	@Override
	public String codeGeneration() {
		String classes = "";
		String declCode = "";
		for (Node dec : decList)
			declCode += dec.codeGeneration();
		for (ClassNode c : classList) {
			classes += "#VTABLE\n" + c.codeGeneration();
		}
		return "push 0\n" + "## .DATA\n" + classes + "\n## LET\n" + declCode + "\n## IN\n" + exp.codeGeneration()
				+ "halt\n" + FOOLlib.getCode();
	}
}
