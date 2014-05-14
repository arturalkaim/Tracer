/**
 * 
 */
package ist.meic.pa;

import javassist.*;
import javassist.expr.*;

import java.io.*;
import java.lang.reflect.*;


/**
 * @author artur
 * 
 */
public class TraceVMExtended {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length < 1) {
		} else {
			Translator translator = new ExTraceTranslator();
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
