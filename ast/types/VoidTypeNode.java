package ast.types;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class VoidTypeNode extends TypeNode {

	public VoidTypeNode() {
	}

	@Override
	public String toPrint(String s) {
		return s + "Void\n";
	}
}