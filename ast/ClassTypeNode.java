package ast;
import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements Node {

    private String id;
    private HashMap<String,Node> fieldTypeList;
    private HashMap<String,ArrowTypeNode> methodTypeList;

    public ClassTypeNode (String i, HashMap<String,Node> f, HashMap<String,ArrowTypeNode> m) {
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

        for (Node field : fieldTypeList.values())
            fieldstr += field.toPrint(s + "  ");

        for (ArrowTypeNode method : methodTypeList.values())
            methodstr += method.toPrint(s + "  ");

        return s + "ClassType\n"
                + fieldstr
                + methodstr
            ; 
    }

    public HashMap<String,Node> getFields(){
        return fieldTypeList;
    }
    public HashMap<String,ArrowTypeNode> getMethods(){
        return methodTypeList;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        // TODO Auto-generated method stub
        return new ArrayList<SemanticError>();
    }

    //non utilizzato
    public Node typeCheck(Environment env) {
        return null;
    }

    //non utilizzato
    public String codeGeneration() {
        return "";
    }

}  
