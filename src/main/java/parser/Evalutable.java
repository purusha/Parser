package parser;

public class Evalutable extends TemplatePiece {
	private Tag tag;

	public Evalutable(String piece, Tag tag) {
		super(piece);
		this.tag = tag;
	}
	
	@Override
	public String evaluate(ParserContext context) {
		return tag.name() + "(" + piece + "," + context +")";
	}

	@Override
	public EvalutableConfig evalutable() {
		return EvalutableConfig.from(tag);
	}
}
