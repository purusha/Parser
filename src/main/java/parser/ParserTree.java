package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

public class ParserTree {
	
	private static final Comparator<Range<Integer>> COMPARATOR_BY_RANGE = new Comparator<Range<Integer>>() {			
		@Override
		public int compare(Range<Integer> o1, Range<Integer> o2) {					
			return o1.isBeforeRange(o2) ? -1 : +1; 
		}
	};
	
//	private static final Comparator<Pair<Tag, Range<Integer>>> COMPARATOR_BY_PAIR = new Comparator<Pair<Tag, Range<Integer>>>() {
//		@Override
//		public int compare(Pair<Tag, Range<Integer>> o1, Pair<Tag, Range<Integer>> o2) {
//			return COMPARATOR_BY_RANGE.compare(o1.getValue(), o2.getValue());
//		}
//	};
	
	private static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

	public static ParserTree build(String template) {		
		final Map<Tag, List<Range<Integer>>> tagPartition = new HashMap<Tag, List<Range<Integer>>>();
		final AtomicInteger evalutableCounter = new AtomicInteger(0);
		
		Arrays.stream(Tag.values()).forEach(tag -> {
			final Pattern p = Pattern.compile("(" + escape(tag.getStart()) + ".*?" + escape(tag.getEnd()) + ")");			
			final Matcher m = p.matcher(template);			
			final List<Range<Integer>> ranges = new ArrayList<Range<Integer>>();
			
			while(m.find()) {		
				ranges.add(Range.between(m.start(), m.end()));
			}
			
			tagPartition.put(tag, ranges);
			evalutableCounter.getAndAdd(ranges.size());
		});

//		System.out.println("--------\nBEFORE\n--------");
//		tagPartition.forEach((t, l) -> {
//			System.out.println(t);
//			l.forEach(System.out::println);
//		});
		
		//sort all list once time
		//solo per SOLUZIONE 1
//		tagPartition.forEach((t, l) -> {
//			Collections.sort(l, COMPARATOR_BY_RANGE);
//		});

//		System.out.println("--------\nSORT\n--------");
//		tagPartition.forEach((t, l) -> {
//			System.out.println(t);
//			l.forEach(System.out::println);
//		});

		final LinkedList<TemplatePiece> pieces = new LinkedList<TemplatePiece>();
		final AtomicInteger notEvalutablePoint = new AtomicInteger(0);
		
		//SOLUTION 2		
		final Map<Range<Integer>, Tag> reverse = new HashMap<Range<Integer>, Tag>();
		tagPartition.entrySet().stream().forEach(e -> e.getValue().stream().forEach(i -> reverse.put(i, e.getKey())));
				
		tagPartition.values().stream()
			.flatMap(Collection::stream)
			.sorted(COMPARATOR_BY_RANGE)
			.forEach(range -> {				
				pieces.add(new NotEvalutable(
					substring(template, notEvalutablePoint.get(), range.getMinimum())
				));
				
				pieces.add(new Evalutable(
					substring(template, range.getMinimum() + 2, range.getMaximum() -2), //tutti i tag devo essere di 2 char 
					reverse.get(range)
				));
				
				notEvalutablePoint.getAndSet(range.getMaximum());				
			});		
		
		//SOLUTION 1		
//		while(evalutableCounter.decrementAndGet() >= 0) {
//			final List<Pair<Tag, Range<Integer>>> partialMin = tagPartition.entrySet().stream()
//				.filter(e -> e.getValue().size() > 0)	
//				.map(e -> Pair.of(e.getKey(), e.getValue().get(0)))
//				.collect(Collectors.toList());
//
//			final Pair<Tag, Range<Integer>> min = Collections.min(partialMin, COMPARATOR_BY_PAIR);			
//			final Range<Integer> range = min.getValue();
//			
//			pieces.add(new NotEvalutable(
//				substring(template, notEvalutablePoint.get(), range.getMinimum())
//			));
//			
//			pieces.add(new Evalutable(
//				substring(template, range.getMinimum() + 2, range.getMaximum() -2), //tutti i tag devo essere di 2 char 
//				min.getKey()
//			));
//			
//			tagPartition.get(min.getKey()).remove(0);			
//			notEvalutablePoint.getAndSet(range.getMaximum());
//		}

		//tail of template without Evalutable code ... if exist!
		if (notEvalutablePoint.get() < template.length()) {
			pieces.add(new NotEvalutable(
				substring(template, notEvalutablePoint.get(), template.length())
			));					
		}
		
//		System.out.println("--------\nEXTRACT PIECE\n--------");
//		tagPartition.forEach((t, l) -> {
//			System.out.println(t);
//			l.forEach(System.out::println);
//		});
		
//		System.out.println("--------\nEXTRACTED PIECE'S\n--------");		
//		pieces.forEach(System.out::println);
		
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
