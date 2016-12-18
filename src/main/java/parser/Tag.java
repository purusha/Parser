package parser;

public enum Tag {
	BOOO1("${", "}$", false),
	BOOO2("#%", "%#", true),
	BOOO3("$^", "^$", false);
	
	private String start;
	private String end;
	private boolean evaluateOnlyOnceTime;

	private Tag(String start, String end, boolean evaluateOnlyOnceTime) {
		this.start = start;
		this.end = end;
		this.evaluateOnlyOnceTime = evaluateOnlyOnceTime;
	}
	
	public String getStart() {
		return start;
	}
	
	public String getEnd() {
		return end;
	}
	
	public boolean evaluateOnlyOnceTime() {
		return evaluateOnlyOnceTime;
	}
}
