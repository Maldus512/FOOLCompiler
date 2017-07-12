package ast.types;

public class BottomTypeNode extends TypeNode {

	public BottomTypeNode() {
	}

	@Override
	public String toPrint(String s) {
		return s + "Exception\n";
	}
}