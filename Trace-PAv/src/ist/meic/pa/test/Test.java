package ist.meic.pa.test;

import ist.meic.pa.Trace;

public class Test {
	public Object foo() {
		return new String("Foo");
	}

	public Object bar() {
		return new String("Bar");
	}

	public Object identity(Object o) {
		return o;
	}

	public void test() {
		Trace.print(foo());
		Object b = bar();
		Trace.print(identity(b));
	}

}
