package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class ParserTree {
	
	private static final Comparator<Pair<Tag, Range<Integer>>> COMPARATOR = new Comparator<Pair<Tag, Range<Integer>>>() {			
		@Override
		public int compare(Pair<Tag, Range<Integer>> o1, Pair<Tag, Range<Integer>> o2) {					
			return o1.getValue().isBeforeRange(o2.getValue()) ? -1 : +1; 
		}
	};
	
	private static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

	public static ParserTree build(String template) {				
		final List<Pair<Tag, Range<Integer>>> ranges = new ArrayList<>();
		
		Arrays.stream(Tag.values()).forEach(tag -> {
			final Pattern p = Pattern.compile("(" + escape(tag.getStart()) + ".*?" + escape(tag.getEnd()) + ")");			
			final Matcher m = p.matcher(template);			
			
			while(m.find()) {	
				ranges.add(Pair.of(tag, Range.between(m.start(), m.end())));
			}
		});
		
		final LinkedList<TemplatePiece> pieces = new LinkedList<TemplatePiece>();
		final AtomicInteger notEvalutablePoint = new AtomicInteger(0);
		
		ranges.stream()
			.sorted(COMPARATOR)
			.forEach(pair -> {				
				final Range<Integer> range = pair.getValue();
				
				pieces.add(new NotEvalutable(
					substring(template, notEvalutablePoint.get(), range.getMinimum())
				));
				
				pieces.add(new Evalutable(
					substring(template, range.getMinimum() + 2, range.getMaximum() -2), //tutti i tag devo essere di 2 char 
					pair.getKey()
				));
				
				notEvalutablePoint.getAndSet(range.getMaximum());				
			});		
		
		//tail of template without Evalutable code ... if exist!
		if (notEvalutablePoint.get() < template.length()) {
			pieces.add(new NotEvalutable(
				substring(template, notEvalutablePoint.get(), template.length())
			));					
		}
		
		return new ParserTree(pieces);
	}
	
	private static String substring(String s, int from, int to) {
		return StringUtils.substring(s, from, to);
		//return s.substring(from, to);
	}	
	
	private static String escape(String str) {
	    return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}

	private final List<TemplatePiece> pieces;
		
	private ParserTree(LinkedList<TemplatePiece> pieces) {						
		final LinkedList<TemplatePiece> preparsed = new LinkedList<>();
		
		for (TemplatePiece tp : pieces) {
			if (tp.evalutable().isTrue() && tp.evalutable().evaluateOnlyOnceTime()) {
				preparsed.add(new NotEvalutable(tp.evaluate(null)));				
			} else {
				preparsed.add(tp);
			}
		}
		
		this.pieces = preparsed;
	}
	
	public Map<MessageParts, Object> eval(ParserContext context) {
		final StringBuilder bodyBuff = new StringBuilder();		
		pieces.stream().forEach(e -> bodyBuff.append(e.evaluate(context)));

		return new HashMap<MessageParts, Object>(){
			private static final long serialVersionUID = -1480982044084365704L;

		{
			put(MessageParts.BODY, bodyBuff);
		}};
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
