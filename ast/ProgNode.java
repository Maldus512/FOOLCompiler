package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ProgNode implements Node {

  private Node exp;
  
  public ProgNode (Node e) {
    exp=e;
  }
  
  public String toPrint(String s) {
    
    return "Prog\n" + exp.toPrint("  ") ;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		return exp.checkSemantics(env);
	}
  
  public Node typeCheck(Environment env) {
    return exp.typeCheck(env);
  }  
  
  public String codeGeneration() {
		return exp.codeGeneration()+"halt\n";
  }  
  
}  