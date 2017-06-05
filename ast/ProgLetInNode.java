package ast;
import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;


/* Class representing a Let in instruction node */
public class ProgLetInNode implements Node {

    private ArrayList<Node> declist;
    private Node exp;

    /* takes the list of declarations and the final expression */
    public ProgLetInNode (ArrayList<Node> d, Node e) {
        declist=d;
        exp=e;
    }

    public String toPrint(String s) {
        String declstr="";
        for (Node dec:declist)
            declstr+=dec.toPrint(s+"  ");
        return s+"ProgLetIn\n" + declstr + exp.toPrint(s+"  ") ; 
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        /* Add a nesting level then proceed in the declarations*/
        env.nestingLevel++;
        HashMap<String,STentry> hm = new HashMap<String,STentry> ();
        env.symTable.add(hm);

        //declare resulting list
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        //check semantics in the dec list
        if(declist.size() > 0){
            env.offset = -2;
            //if there are children then check semantics for every child and save the results
            for(Node n : declist)
                res.addAll(n.checkSemantics(env));
        }

        //check semantics in the exp body
        res.addAll(exp.checkSemantics(env));

        //clean the scope, we are leaving a let scope
        env.symTable.remove(env.nestingLevel--);

        //return the result
        return res;
    }

    public Node typeCheck () {
        for (Node dec:declist)
            dec.typeCheck();
        return exp.typeCheck();
    }

    public String codeGeneration() {
        String declCode="";
        for (Node dec:declist)
            declCode+=dec.codeGeneration();
        return  "push 0\n"+
            declCode+
            exp.codeGeneration()+"halt\n"+
            FOOLlib.getCode();
    } 
}  
