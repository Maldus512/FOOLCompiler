package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import util.STentry;
import lib.FOOLlib;
import ast.types.*;

public class CallNode implements Node {

    protected String id;
    protected STentry entry;
    protected ArrayList<Node> parList;
    protected int nestingLevel;

    public CallNode(String i, STentry e, ArrayList<Node> p, int nl) {
        id = i;
        entry = e;
        parList = p;
        nestingLevel = nl;
    }

    public CallNode(String text, ArrayList<Node> args) {
        id = text;
        parList = args;
    }

    public String toPrint(String s) {
        String parlstr = "";
        for (Node par : parList)
            parlstr += par.toPrint(s + "  ");

        return s + "Call:" + id + " at nestlev " + nestingLevel + "\n" + entry.toPrint(s + "  ") + parlstr;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        //int j = env.getNestLevel();
        int j = env.getLastNestLevel();
        STentry tmp = null;

        while (j >= 0 && tmp == null)
            tmp = (env.getST().get(j--)).get(id);

        if (tmp == null) {
            res.add(new SemanticError("Symbol '" + id + "' was not previously declared."));
            return res;
        } else if (!(tmp.getType() instanceof ArrowTypeNode)) {
            res.add(new SemanticError("Symbol '" + id + "' is not a function."));
            return res;
        }

        this.entry = tmp;
        this.nestingLevel = env.getNestLevel();

        for (Node par : parList)
            res.addAll(par.checkSemantics(env));

        return res;
    }

    public TypeNode typeCheck(Environment env) { //                           
        ArrowTypeNode t = null;
        if (entry.getType() instanceof ArrowTypeNode) {
            t = (ArrowTypeNode) entry.getType();
        } else {
            System.out.println("Invocation of a non-function " + id + ".");
            return new BottomTypeNode();
        }

        ArrayList<TypeNode> p = t.getParList();
        if (!(p.size() == parList.size())) {
            System.out.println("Wrong number of parameters in the invocation of " + id + 
                "; found "+parList.size() + " paramenters, " + p.size() + " expected.");
            return new BottomTypeNode();
        }

        for (int i = 0; i < parList.size(); i++) {
            if (!(FOOLlib.isSubtype((parList.get(i)).typeCheck(env), p.get(i)))) {
                System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id + 
                    "; found\n" + parList.get(i).toPrint("    ") + "Needed \n" +
                    p.get(i).toPrint("    "));
                return new BottomTypeNode();
            }
        }
        return t.getRet();
    }

    public String codeGeneration() {
        String parCode = "";
        for (int i = parList.size() - 1; i >= 0; i--)
            parCode += parList.get(i).codeGeneration();

        String getAR = "";
        for (int i = 0; i < nestingLevel - entry.getNestLevel(); i++)
            getAR += "lw\n";

        return "lfp\n" + //Load frame pointer on stack - expected to go back
                         //CL 
                parCode + //generate all the parameters
                "lfp\n" + getAR + // load words from stack (starting from the current 
                                  // frame pointer) until you find the reference of
                                  //the function. Works in the same way of a normal
                                  //variable. Jumps from frame pointer to frame pointer
                                  //over the linking chain.
                                  //(put AL on the stack)
                "push " + entry.getOffset() + "\n" + //pushing the offset on the stack
                // to add it to the frame pointer obtained following
                // the linking chain.
                "lfp\n" + getAR + 
                "add\n" + "lw\n" + 
                "js\n";
    }
    //NOTE: AL = Access Link; CL = Control Link
    //A control link from record A points to the previous record on the stack. 
    //The chain of control links traces the dynamic execution of the program.
    //An access link from record A points to the record of the closest enclosing 
    //block in the program. The chain of access links traces the static structure (think: scopes) of the program.
}
