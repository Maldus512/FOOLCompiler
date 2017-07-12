package ast.types;

public class VoidTypeNode extends TypeNode {

	public VoidTypeNode() {
	}

	@Override
	public String toPrint(String s) {
		return s + "Void\n";
	}
}