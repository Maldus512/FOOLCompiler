package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements Node {

    private String id;
    private ArrayList<Node> fieldTypeList;
    private ArrayList<ArrowTypeNode> methodTypeList;

    public ClassTypeNode (String i, ArrayList<Node> f, ArrayList<ArrowTypeNode> m) {
        id = i;
        fieldTypeList = f;
        methodTypeList = m;
    }

    public String getId() {
        return id;
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
