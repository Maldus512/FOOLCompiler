package ast.types;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class BottomTypeNode extends TypeNode {
  
  public BottomTypeNode () {
  }
  
  @Override
  public String toPrint(String s) {
	return s+"Exception\n";  
  }
}  