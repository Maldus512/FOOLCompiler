package ast.types;
import java.util.ArrayList;
import java.util.HashMap;
import ast.Node;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode extends TypeNode {

    private String id;
    private HashMap<String,TypeNode> fieldTypeList;
    private HashMap<String,ArrowTypeNode> methodTypeList;

    public ClassTypeNode (String i, HashMap<String,TypeNode> f, HashMap<String,ArrowTypeNode> m) {
        id = i;
        fieldTypeList = f;
        methodTypeList = m;
    }

    public String getId() {
        return id;
    }

    @Override
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

    public HashMap<String,TypeNode> getFields(){
        return fieldTypeList;
    }
    public HashMap<String,ArrowTypeNode> getMethods(){
        return methodTypeList;
    }
}  
