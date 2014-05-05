package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class History {
	static IdentityHashMap<Object, ArrayList<String>> history = new IdentityHashMap<Object, ArrayList<String>>();
	static String temp = "";
	static int temp_nargs;
	public static void saveObject(Object o, String trace) {
		if (o == null)
			return;
		//System.out.println(o.hashCode() + trace);
		if (history.containsKey(o)) {
			history.get(o).add(trace);
		} else {
			ArrayList<String> aux = new ArrayList<String>();
			aux.add(trace);
			history.put(o, aux);
		}
	}

	public void print(Object foo) {
		//System.out.println(foo.hashCode());
		if (history.containsKey(foo)) {
			System.out.println("Tracing for " + foo.toString());
			for (String s : history.get(foo))
				System.out.println(s);
		} else
			System.out.println("Tracing for " + foo + " is nonexistent!");
	}
}
