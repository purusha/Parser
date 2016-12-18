package parser;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public abstract class TemplatePiece {

	protected String piece;

	public TemplatePiece(String piece) {
		this.piece = piece;
	}	
	
	public abstract String evaluate(ParserContext context);
	
	public abstract EvalutableConfig evalutable();
	
	@Override
	public String toString() {
		return 
			ReflectionToStringBuilder.toString(this)
			.replace('\n', ' ');
	}

}
