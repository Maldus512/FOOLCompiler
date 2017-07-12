package ast.types;

public class IntTypeNode extends TypeNode {

	public IntTypeNode() {
	}

	@Override
	public String toPrint(String s) {
		return s + "IntType\n";
	}
}