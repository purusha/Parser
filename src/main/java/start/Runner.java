package start;

import java.util.Map;
import java.util.stream.IntStream;

import parser.MessageParts;
import parser.ParserContext;
import parser.ParserTree;
import parser.Tag;


public class Runner {

	public static void main(String[] args) {
				
		String template = 
			"kasdjkajsd " + Tag.BOOO1.getStart() + "XXX-1" + Tag.BOOO1.getEnd() + " fauhbsfuh\n" +
			"faksjdf asjdf aljsdfh ajshdf ajshdf ajsdhf ajshd f\n" +
			"kasdjkajsd " + Tag.BOOO1.getStart() + "XXX-2" + Tag.BOOO1.getEnd() + " fauhbsfuh\n" +
			"kasdjkajsd " + Tag.BOOO2.getStart() + "XXX-3" + Tag.BOOO2.getEnd() + " fauhbsfuh\n" +
			"faksjdf asjdf aljsdfh ajshdf ajshdf ajsdhf ajshd f\n" +
			"faksjdf asjdf aljsdfh ajshdf ajshdf ajsdhf ajshd f\n" +
			"kasdjkajsd " + Tag.BOOO3.getStart() + "XXX-4" + Tag.BOOO3.getEnd() + " fauhbsfuh\n" +
			"kasdjkajsd " + Tag.BOOO3.getStart() + "XXX-5" + Tag.BOOO3.getEnd() + " fauhbsfuh\n" +
			"faksjdf asjdf aljsdfh ajshdf ajshdf ajsdhf ajshd f\n" +
			"kasdjkajsd " + Tag.BOOO2.getStart() + "XXX-6" + Tag.BOOO2.getEnd() + " fauhbsfuh\n" +
			".";
		
		System.out.println("-----------------");
		System.out.println(template);
		System.out.println("-----------------");
		long before = System.currentTimeMillis();
		
		//do only once per delivery
		final ParserTree tree = ParserTree.build(template);
//		System.out.println("-----------------");
//		System.out.println("tree" + tree);
//		System.out.println("-----------------");		
				
		IntStream.rangeClosed(0, 1_000_000).forEach(i -> {
			tree.eval(new ParserContext());
		});				
		
		long after = System.currentTimeMillis();
		System.out.println("time elapsed: " + (after-before) + "\n\n");
		
		//do this for each recipient
		System.out.println("--------- RESULT --------");
		Map<MessageParts, Object> eval = tree.eval(new ParserContext());
		System.out.println(eval.get(MessageParts.BODY));
		System.out.println("-----------------");
								
	}

}
