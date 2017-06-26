package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class CallNode implements Node {

    private String id;
    private STentry entry; 
    private ArrayList<Node> parlist; 
    private int nestinglevel;


    public CallNode (String i, STentry e, ArrayList<Node> p, int nl) {
        id=i;
        entry=e;
        parlist = p;
        nestinglevel=nl;
    }

    public CallNode(String text, ArrayList<Node> args) {
        id=text;
        parlist = args;
    }

    public String toPrint(String s) {  //
        String parlstr="";
        for (Node par:parlist)
            parlstr+=par.toPrint(s+"  ");		
        return s+"Call:" + id + " at nestlev " + nestinglevel +"\n" 
            +entry.toPrint(s+"  ")
            +parlstr;        
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        //create the result
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        int j=env.getNestLevel();
        STentry tmp=null; 
        while (j>=0 && tmp==null)
            tmp=(env.getST().get(j--)).get(id);
        if (tmp==null)
            res.add(new SemanticError("Id '" + id + "' not declared."));

        else{
            this.entry = tmp;
            this.nestinglevel = env.getNestLevel();

            for(Node arg : parlist)
                res.addAll(arg.checkSemantics(env));
        }
        return res;
    }

    public Node typeCheck(Environment env) {  //                           
        ArrowTypeNode t=null;
        if (entry.getType() instanceof ArrowTypeNode) t=(ArrowTypeNode) entry.getType(); 
        else {
            System.out.println("Invocation of a non-function "+id);
            System.exit(0);
        }
        ArrayList<Node> p = t.getParList();
        if ( !(p.size() == parlist.size()) ) {
            System.out.println("Wrong number of parameters in the invocation of "+id);
            System.exit(0);
        } 
        for (int i=0; i<parlist.size(); i++) 
            if ( !(FOOLlib.isSubtype( (parlist.get(i)).typeCheck(env), p.get(i)) ) ) {
                System.out.println("Wrong type for "+(i+1)+"-th parameter in the invocation of "+id);
                System.exit(0);
            } 
        return t.getRet();
    }

    public String codeGeneration() {
        String parCode="";
        for (int i=parlist.size()-1; i>=0; i--)
            parCode+=parlist.get(i).codeGeneration();

        String getAR="";
        for (int i=0; i<nestinglevel-entry.getNestLevel(); i++) 
            getAR+="lw\n";

        return "lfp\n"+ //Load frame pointer on stack - needed to go back
                        //CL 
            parCode+    //generate all the parameters
            "lfp\n"+getAR+ // load words from stack (starting from the current 
                           // frame pointer) until you find the reference of
                           //the function. Works in the same way of a normal
                           //variable. Jumps from frame pointer to frame pointer
                           //over the linking chain.
                            //setto AL risalendo la catena statica (put AL on the stack)
            // ora recupero l'indirizzo a cui saltare e lo metto sullo stack
            // Mettere questo AL serve? nella funzione poi ne fa il pop senza usarlo.
            // Probabilmente serve a fare riferimenti  di variabili.
            "push "+entry.getOffset()+"\n"+ //pushing the offset on the stack
                            // to add it to the frame pointer obtained following
                            // the linking chain.
            "lfp\n"+getAR+ //risalgo la catena statica
            "add\n"+ 
            "lw\n"+ //carico sullo stack il valore all'indirizzo ottenuto
            "js\n";
    }
//NOTE: AL = Access Link; CL = Control Link
//A control link from record A points to the previous record on the stack. 
//The chain of control links traces the dynamic execution of the program.
//An access link from record A points to the record of the closest enclosing 
//block in the program. The chain of access links traces the static structure (think: scopes) of the program.

}  
