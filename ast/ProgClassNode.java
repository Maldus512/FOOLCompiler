package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;


/* Class representing a Let in instruction node */
public class ProgClassNode implements Node {

    private ArrayList<Node> classList;
    private Node exp;

    /* takes the list of declarations and the final expression */
    public ProgClassNode (ArrayList<Node> l, Node e) {
        classList = l;
        exp = e;
    }

    public String toPrint(String s) {
        String fieldstr="";
        for (Node c:classList)
            fieldstr+=c.toPrint(s+"  ");
        return s+"ProgClass\n" + fieldstr + exp.toPrint(s+"  ") ;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        
        return res;
    }

    public Node typeCheck () {
        
        return new IntTypeNode();
    }

    public String codeGeneration() {
        
        return  "halt\n";
    } 
}  
