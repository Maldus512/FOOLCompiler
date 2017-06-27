package ast.types;
import java.util.ArrayList;

import ast.Node;
import util.Environment;
import util.SemanticError;

/* node for a function type */
public class ArrowTypeNode extends TypeNode {
    private ArrayList<TypeNode> parlist; 
    private TypeNode ret;

    public ArrowTypeNode (ArrayList<TypeNode> p, TypeNode r) {
        parlist=p;
        ret=r;
    }

    public String toPrint(String s) { //
        String parlstr="";
        for (Node par:parlist)
            parlstr+=par.toPrint(s+"  ");
        return s+"ArrowType\n" + parlstr + ret.toPrint(s+"  ->") ; 
    }

    public TypeNode getRet () { //
        return ret;
    }

    public ArrayList<TypeNode> getParList () { //
        return parlist;
    }
}  
