package parser;

final class EvalutableConfig {
	private boolean isTrue;
	private boolean evaluateOnlyOnceTime;

	public static EvalutableConfig notEvalutable() {
		return new EvalutableConfig(false);
	}

	public static EvalutableConfig from(Tag tag) {
		return new EvalutableConfig(true, tag.evaluateOnlyOnceTime());
	}

	private EvalutableConfig(boolean isTrue) {
		this(isTrue, false);
	}
	
	private EvalutableConfig(boolean isTrue, boolean evaluateInitially) {
		this.isTrue = isTrue;
		this.evaluateOnlyOnceTime = evaluateInitially;		
	}

	public boolean isTrue() {
		return isTrue;
	}

	public boolean evaluateOnlyOnceTime() {
		return evaluateOnlyOnceTime;
	}
}
