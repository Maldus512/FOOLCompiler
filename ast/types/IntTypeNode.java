package ast.types;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class IntTypeNode extends TypeNode {
  
  public IntTypeNode () {
  }
  
  @Override
  public String toPrint(String s) {
	return s+"IntType\n";  
  }
}  