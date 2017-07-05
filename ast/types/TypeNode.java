package ast.types;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import ast.Node;

/* Interface for a generic type node */
// No actual difference from Node - just clarity purposes
public class TypeNode implements Node {

    protected boolean isField = false;

    public boolean isField() {
        return isField;
    }

    public void isField(boolean field) {
        isField = field;
    }

    /* function that generates the parse tree for the node */
    public String toPrint(String indent) {
        return indent;
    }

    //fa il type checking e ritorna: 
    //  per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
    //  per una dichiarazione, "null"
    public TypeNode typeCheck(Environment env) {
        return this;
    }

    public String codeGeneration() {
        return "";
    }

    public ArrayList<SemanticError> checkSemantics(Environment env) {
        return new ArrayList<SemanticError>();
    }

}  
