package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import util.FOOLlib;

import ast.types.*;

public class GreatNode implements Node {

    private Node left;
    private Node right;

    public GreatNode(Node l, Node r) {
        left = l;
        right = r;
    }

    public String toPrint(String s) {
        return s + "Great\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create the result
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        //check semantics in the left and in the right exp

        res.addAll(left.checkSemantics(env));
        res.addAll(right.checkSemantics(env));

        return res;
    }

    public TypeNode typeCheck(Environment env) {
        TypeNode l = left.typeCheck(env);
        TypeNode r = right.typeCheck(env);
        if (!(FOOLlib.isSubtype(l, r) || FOOLlib.isSubtype(r, l))) {
            System.out.println("Incompatible types for '>' operand:\n"+
                                l.toPrint("    ") +
                                r.toPrint("    "));
            return new BottomTypeNode();
        }
        return new BoolTypeNode();
    }

    public String codeGeneration() {
        String l1 = FOOLlib.freshLabel();
        String l2 = FOOLlib.freshLabel();
        return left.codeGeneration() + right.codeGeneration() + "bg " + l1 + "\n" + "push 0\n" + "b " + l2 + "\n" + l1
                + ":\n" + "push 1\n" + l2 + ":\n";

    }

}
