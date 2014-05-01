import ist.meic.pa.Trace;

import java.util.*;

class Test {

	Map m = new HashMap();

	public Object identity(Object o) {
		return o;
	}

	public void test() {
		Object o = new String("MyObj");
		Object[] arr= new Object[1];
		arr[0]=o;
		identity(o);
		m.put(2, o);
		m.get(2);

		Trace.print(arr);
		Trace.print(arr[0]);
		for (Object obj : m.values()) {
			System.out.println(obj);
		}
//		System.out.println(m.get(2));
		Trace.print(o);

	}
}

public class Test2 {
	public static void main(String args[]) {
		(new Test()).test();
	}
}