import ist.meic.pa.Trace;

import java.util.*;

class Test {

	Map m = new HashMap();

	public Object identity(Object o) {
		return o;
	}
	private class toto{
		public Integer i;
		
		toto(Integer i){
			this.i = i;
		}
	}

	public void test() {
		Object o = new String("MyCastedObj");
		

		
		String s = (String) identity(o);
		m = new HashMap();
		m.put(2, o);
		m.get(2);
		int ji = 0;
		ji = 2;
		toto t = new toto(ji);
		t.i = 12;
		Trace.print(t.i);

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