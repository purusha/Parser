package parser;

public class NotEvalutable extends TemplatePiece {
	public NotEvalutable(String piece) {
		super(piece);
	}

	@Override
	public String evaluate(ParserContext context) {
		return piece;
	}

	@Override
	public EvalutableConfig evalutable() {
		return EvalutableConfig.notEvalutable();
	}
}
