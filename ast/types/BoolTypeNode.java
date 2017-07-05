package ast.types;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class BoolTypeNode extends TypeNode {
  
  public BoolTypeNode () {
  }

  public String toPrint(String s) {
	return s+"BoolType\n";  
  }
}  