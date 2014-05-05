import ist.meic.pa.Trace;

import java.lang.reflect.Modifier;
import java.util.*;

import javassist.NotFoundException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class Test {

	Map m = new HashMap();

	public Object identity(Object o) {
		return o;
	}
	private class toto{
		public Integer i;
		public String sa;
		toto(Integer i){
			this.i = i;
			this.sa = "coisas";
		}
		
		public void trou() throws NotFoundException{
			throw new NotFoundException("olá");
		}
	}

	public void test() {
		Object o = new String("MyCastedObj");
		String s = (String) identity(o);
		m = new HashMap();
		Object[] arr= new Object[1];
		arr[0]=o;
		identity(o);
		m.put(2, o);
		m.get(2);
		int ji = 0;
		ji = 3;
		toto t = new toto(ji);
		t.i = 12;
		Trace.print(t.i);
		t.sa = "SERA QUE SIM";

		Trace.print(arr);
		Trace.print(arr[0]);
		for (Object obj : m.values()) {
			System.out.println(obj);
		}
//		System.out.println(m.get(2));
		Trace.print(o);
		Trace.print(t.sa);
		
		try {
			t.trou();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

public class Test2 {
	public static void main(String args[]) {
		(new Test()).test();
	}
}
