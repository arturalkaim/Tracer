import ist.meic.pa.Trace;

public class Test0 {

	public static void main(String[] args) {
		(new Test()).test();
	}

}
class Test {

	public Test() {
		// System.out.println("NEW TEST");
	}

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