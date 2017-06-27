package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

import ast.types.*;

//Node representing a boolean value: False or True
public class BoolNode implements Node {

    //The boolean value
    private boolean val;

    public BoolNode (boolean n) {
        val=n;
    }

    public String toPrint(String s) {
        if (val) return s+"Bool:true\n";
        else return s+"Bool:false\n";  
    }

    //A bool node is of type Bool, always
    public TypeNode typeCheck(Environment env) {
        return new BoolTypeNode();
    }    

    //Can't mess this up
    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        return new ArrayList<SemanticError>();
    }

    public String codeGeneration() {
        return "push "+(val?1:0)+"\n";
    }

}  
