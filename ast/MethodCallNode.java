package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.STentry;
import lib.FOOLlib;

import ast.types.*;

public class MethodCallNode implements Node {

	private String id;
	private STentry methodEntry;
	private ArrayList<Node> parList;
	private int nestLevel;
	private IdNode varNode;
	private String ownerClass;

	public MethodCallNode(String text, ArrayList<Node> args, Node sn) {
		id = text;
		parList = args;
		varNode = (IdNode) sn;
	}

	public String toPrint(String s) {
		String parlstr = "";

		for (Node par : parList)
			parlstr += par.toPrint(s + "  ");

		return s + "Call:" + id + " at nestlev " + nestLevel + "\n" + methodEntry.toPrint(s + "  ") + parlstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		//int j = env.getNestLevel();
		int j = env.getLastNestLevel();
		STentry methodTmp = null;

		res.addAll(varNode.checkSemantics(env));
		if (res.size() > 0) {
			return res;
		}

		if (!(varNode.getType() instanceof ClassTypeNode)) {
			res.add(new SemanticError("Var id '" + varNode.getId() + "' is not an object; cannot invoke method " + id + "."));
			return res;
		}

		ownerClass = ((ClassTypeNode) (varNode.getType())).getId();

		// seek for method definition
		ClassTypeNode classType = env.classTypeEnvGet(ownerClass);

		HashMap<String, STentry> methodEntries = classType.getMethodEntriesMap();

		for (String key : methodEntries.keySet()) {
			if (key.equals(id)) {
				methodTmp = methodEntries.get(key);
				break;
			}
		}

		if (methodTmp == null) {
			res.add(new SemanticError("Method '" + id + "' has not been declared for class " + ownerClass + "."));
			return res;
		} else if (!(methodTmp.getType() instanceof ArrowTypeNode)) {
			res.add(new SemanticError("Id '" + id + "' is not a method for class " + ownerClass + "."));
			return res;
		}

		this.methodEntry = methodTmp;
		this.nestLevel = env.getNestLevel();

		for (Node arg : parList)
			res.addAll(arg.checkSemantics(env));

		return res;
	}

	public TypeNode typeCheck(Environment env) {

		ArrowTypeNode t = null;
		if (methodEntry.getType() instanceof ArrowTypeNode) {
			t = (ArrowTypeNode) methodEntry.getType();
		} else {
			System.out.println("Invocation of a non-function " + id + ".");
			return new BottomTypeNode();
		}
		ArrayList<TypeNode> p = t.getParList();
		if (!(p.size() == parList.size())) {
			System.out.println(
					"Wrong number of parameters in the invocation of " + id + " method of class " + ownerClass + ".");
			return new BottomTypeNode();
		}
		for (int i = 0; i < parList.size(); i++)
			if (!(FOOLlib.isSubtype((parList.get(i)).typeCheck(env), p.get(i)))) {
				System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id
						+ " method of class " + ownerClass + ".");
				return new BottomTypeNode();
			}
		return t.getRet();
	}

	public String codeGeneration() {
		String parCode = "";
		for (int i = parList.size() - 1; i >= 0; i--)
			parCode += parList.get(i).codeGeneration();

		String getAR = "";
		//meno uno percheè le classi non creano un nuovo record di attivazione
		for (int i = 0; i < nestLevel - methodEntry.getNestLevel(); i++)
			getAR += "lw\n";

		String thisRef = varNode.codeGeneration();

		return "lfp\n" + //CL
				parCode + thisRef + "lfp\n" + getAR + //setto AL risalendo la catena statica
				// ora recupero l'indirizzo a cui saltare e lo metto sullo stack
				"push " + methodEntry.getOffset() + "\n" + //metto offset sullo stack
				"lfp\n" + getAR + //risalgo la catena statica
				"add\n" + "lw\n" + //carico sullo stack il valore all'indirizzo ottenuto
				"js\n";
	}

}
