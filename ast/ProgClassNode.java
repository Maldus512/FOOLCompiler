package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;


/* Class representing a Classexp instruction node */
public class ProgClassNode implements Node {

	private ArrayList<Node> classList;
	private ArrayList<Node> decList;
	private Node exp;

	public ProgClassNode (ArrayList<Node> l, ArrayList<Node> d, Node e) {
		classList = l;
		decList = d;
		exp = e;
	}

	public String toPrint(String s) {
		String 	fieldstr = "",
				decstr = "";

		if (decList.size() > 0) {
			for (Node d : decList)
				decstr += d.toPrint(s+"  ");
		}

		for (Node c : classList)
			fieldstr += c.toPrint(s+"  ");
		
		return 	s + "ProgClass\n"
				+ fieldstr
				+ decstr
				+ exp.toPrint(s+"  ");
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		env.incNestLevel();	// nestingLevel is now 0

		// create a new hashmap and add it to the symbol table
		HashMap<String,STentry> hm = new HashMap<String,STentry> ();
		env.getST().add(hm);

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// check semantics for every class declaration
		for(Node n:classList)
			res.addAll(n.checkSemantics(env));

		// if there are lets
		if (decList.size() > 0) {
			env.setOffset(-2);

			for(Node n:decList)
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

	public Node typeCheck(Environment env) {
		for (Node c:classList)
			c.typeCheck(env);

		return exp.typeCheck(env);
	}

	public String codeGeneration() {
		String classes = "";
		String declCode="";
		for (Node dec:decList)
			declCode+=dec.codeGeneration();
		for (Node c : classList) {
			classes += c.codeGeneration();
		}
		return "push 0\n" 
			+ "# .DATA\n"
			+ classes
			+ declCode
			+ "##\n"
			+ "halt\n"
			+ FOOLlib.getCode(); 
	} 
}  
