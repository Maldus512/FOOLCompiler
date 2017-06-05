package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

/* Interface for a generic instruction node */
public interface Node {

    /* function that generates the parse tree for the node */
    String toPrint(String indent);

    //fa il type checking e ritorna: 
    //  per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
    //  per una dichiarazione, "null"
    Node typeCheck();

    String codeGeneration();

    ArrayList<SemanticError> checkSemantics(Environment env);

}  
