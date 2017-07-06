package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

import ast.types.*;

public class PrintNode implements Node {

  private Node val;
  
  public PrintNode (Node v) {
    val=v;
  }
  
  public String toPrint(String s) {
    return s+"Print\n" + val.toPrint(s+"  ") ;
  }
  
  public TypeNode typeCheck(Environment env) {
    //return val.typeCheck(env);
    return new VoidTypeNode();
  }  
  
  @Override
 	public ArrayList<SemanticError> checkSemantics(Environment env) {

 	  return val.checkSemantics(env);
 	}
  
  public String codeGeneration() {
		return val.codeGeneration()+"print\n";
  }
    
}  