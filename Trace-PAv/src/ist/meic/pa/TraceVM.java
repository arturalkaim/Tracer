/**
 * 
 */
package ist.meic.pa;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;


/**
 * @author artur
 * 
 */
public class TraceVM {

	
	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length < 1) {
			System.err.println("No program to run!");
		} else {
			Translator translator = new TraceTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.delegateLoadingOf("ist.meic.pa.History");
			classLoader.delegateLoadingOf("ist.meic.pa.Trace");
			classLoader.addTranslator(pool, translator);
			String[] restArgs = new String[args.length - 1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			classLoader.run(args[0], restArgs);
		}

	}
}
