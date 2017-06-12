package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;


/* Class representing a Let in instruction node */
public class ClassNode implements Node {

    private ArrayList<Node> fieldlist;
    private String name;
    
    /* takes the list of declarations and the final expression */
    public ClassNode (String id) {
        fieldlist = new ArrayList<Node>();
        name = id;
    }

    public String toPrint(String s) {
        String fieldstr="";
        for (Node dec:fieldlist)
            fieldstr+=dec.toPrint(s+"  ");
        return s+"Class:"+name+"\n" + fieldstr ; 
    }

    public void addPar (Node p) {
        fieldlist.add(p);
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
