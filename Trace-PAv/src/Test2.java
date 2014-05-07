import ist.meic.pa.Trace;

import java.util.*;

class Test {
	Integer ias;
	{
		 ias = new Integer(1);
		
	}	
	static Object obj;
	static {
		obj = new Object();
		
	}
	Map m = new HashMap();
	
	public Test(){
		
	}
	
	public Object foo(int a){
		return new String("FOO"+a);
	}
	public Test(Object o){
		identity(o);
	}
	public Object identity(Object o) {
		return o;
	}

	public void test() {
		Object o = foo(12);
		Object[] arr = {new String("TEST"),new String("TEST")};
		identity(o);
		
		new Test(o);
		
		m.put(5, o);
		m.put(2, arr);
		m.get(2);
		Trace.print(o);
		Trace.print(obj);
		Trace.print(ias);
		
		for(;forTest(o);){}
		
		for(Iterator<Object> i = m.entrySet().iterator(); i.hasNext(); ) {
			Object item = i.next();
		    System.out.println(item);
		}
		
		for (Object obj : m.values()) {
			System.out.println(obj);
		}
//		System.out.println(m.get(2));
		Trace.print(m);
	}

	private Boolean forTest(Object o) {
		return false;
	}
}

public class Test2 {
	public static void main(String args[]) {
		(new Test()).test();
	}
}