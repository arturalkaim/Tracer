package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;

public class History {
	static HashMap<Object, ArrayList<String>> history = new HashMap<Object, ArrayList<String>>();
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
	
	public static void saveObject(String trace, Object o) {
		if (o == null)
			return;
		/*System.out.println(o.toString() + " " + trace + temp + "  " + temp_nargs);
		if(temp_nargs!=0){
			trace += temp;
			temp_nargs--;
		}else
			return;*/
		trace += temp;
		if (history.containsKey(o)) {
			history.get(o).add(trace);
		} else {
			ArrayList<String> aux = new ArrayList<String>();
			aux.add(trace);
			history.put(o, aux);
		}
	}
	public static void saveCall(String trace, int nargs) {
		temp = trace;
		temp_nargs = nargs;
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
