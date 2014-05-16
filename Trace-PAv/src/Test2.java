import ist.meic.pa.Trace;

import java.util.*;

class toto extends Test{
	
	toto(Integer i){
	}
	@Override
	public Object identity(Object o) {
		Object a = super.identity(o);
		return a;
	}
	
	
}

class Test {

	static public class toti{
		toto t;
		Object ola;
		toti(){
			 t = new toto(1);
		}
		 public toti tete() {
			ola = t.identity(ola);
			return this;
		 }
		public Object identity(Object o) {
			ola = o;
			return ola;
		}
		public void trou(){
			throw new NullPointerException("Olá: Trou");
		}
		
	}
	
    Map m = new HashMap();

    public Object identity(Object o) {
        return o;
    }

    public void test() {
    	
    	String s = new String("MyObj");
        Object o = new String(s);
        Integer ia= new Integer(12);
        toto t = new toto(ia);
        
        identity(o);
        m.put(2,o);
        m.get(2);

        Trace.print(o);
        Trace.print(ia);

        for (Object obj : m.values()) {
            System.out.println(obj);
        }
        
        try {
        	Test.toti asd= new Test.toti();
        	System.out.println("asd.trou()");
        	asd.trou();
		} catch (Exception e) {
			identity(e);
			Trace.print(e);
		}
        
        Trace.print(o);

    }
}

public class Test2 {
    public static void main(String args[]) {
    	Test.toti aaa= new Test.toti();
    	Test bbb = new Test();
    	bbb.test();
    	aaa.identity(aaa);
        aaa.tete().tete();
        Trace.print(aaa);
    }
}