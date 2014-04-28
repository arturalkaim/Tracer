package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;

public class History {
	static HashMap<Integer, ArrayList<String>> history = new HashMap<Integer, ArrayList<String>>();

	public static void saveObject(Object o, String trace) {
		if (history.containsKey(o.hashCode()))
			history.get(o.hashCode()).add(trace);
		else {
			ArrayList<String> aux = new ArrayList<String>();
			aux.add(trace);
			history.put(o.hashCode(), aux);
		}
	}

	public void print(Object foo) {
		if (history.containsKey(foo.hashCode())) {
			System.out.println("Tracing for "+foo.toString());
			for (String s : history.get(foo.hashCode()))
				System.out.println(s);
		} else
			System.out.println("Tracing for " + foo + " is nonexistent!");
	}
}
