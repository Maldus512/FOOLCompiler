package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements Node {

    private ArrayList<Node> fieldTypeList;
    private ArrayList<ArrowTypeNode> methodTypeList;

    public ClassTypeNode (ArrayList<Node> f, ArrayList<ArrowTypeNode> m) {
        fieldTypeList = f;
        methodTypeList = m;
    }

    public String toPrint(String s) { //
        String  fieldstr = "",
                methodstr = "";

        for (Node field : fieldTypeList)
            fieldstr += field.toPrint(s + "  ");

        for (ArrowTypeNode method : methodTypeList)
            methodstr += method.toPrint(s + "  ");

        return s + "ClassType\n"
                + fieldstr
                + methodstr
            ; 
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        // TODO Auto-generated method stub
        return new ArrayList<SemanticError>();
    }

    //non utilizzato
    public Node typeCheck () {
        return null;
    }

    //non utilizzato
    public String codeGeneration() {
        return "";
    }

}  
