package ist.meic.pa;

import javassist.*;
import javassist.expr.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;

public class TraceTranslator implements Translator {

	protected String methodSaveArgs = "";
	private String methodSaveReturn = "";

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		makeTracable(ctClass, pool);

	}

	private void makeTracable(CtClass ctClass, final ClassPool pool)
			throws CannotCompileException, NotFoundException {
		// System.out.println(ctClass.getName());
		if (ctClass.getPackageName() != null
				&& ctClass.getPackageName().equals("ist.meic.pa")
				&& !ctClass.getName().equals("ist.meic.pa.Trace"))
			return;
		for (final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			methodSaveArgs = "";
			methodSaveReturn = "";
			if (ctMethod.isEmpty()
					|| Modifier.isNative(ctMethod.getModifiers())
					|| Modifier.isTransient(ctMethod.getModifiers()))
				continue;

			ctMethod.insertAfter(methodSaveReturn);

			ctMethod.instrument(new ExprEditor() {
				public void edit(NewExpr newEx) throws CannotCompileException {
					if (newEx.getClassName().startsWith("ist.meic.pa.History"))
						return;
					String src = "";
					/*
					 * System.out.println(newEx.getClassName() + " " +
					 * newEx.getFileName() + " at line: " +
					 * newEx.getLineNumber());
					 */
					try {
						src += "$_=$proceed($$); new ist.meic.pa.History().saveObject($_"
								+ ",\"  <- "
								+ newEx.getConstructor().getLongName()
								+ " on "
								+ newEx.getFileName()
								+ ":"
								+ newEx.getLineNumber() + "\");";
						newEx.replace(src);
					} catch (NotFoundException e1) {
						e1.printStackTrace();
					}

				}

				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					try {
						if (m.getClassName().startsWith("ist.meic.pa.History"))/*
								|| m.getMethod().getLongName()
										.startsWith("java.lang"))*/
							return;

						/*
						 * System.out.println(m.getMethodName() + " " +
						 * m.getFileName() + " at line: " + m.getLineNumber());
						 */

						String methodCall = "";
						for (int i = 1; i <= m.getMethod().getParameterTypes().length; i++) {
							methodCall += " if(!$" + i
									+ ".getClass().isPrimitive()) "
									+ "	new ist.meic.pa.History().saveObject($"
									+ i + ",\"  -> "
									+ m.getMethod().getLongName() + " on "
									+ m.getFileName() + ":" + m.getLineNumber()
									+ "\"); ";
						}
						methodCall += "$_=$proceed($$);"
								+ "if($_ != null && !$_.getClass().isPrimitive()) "
								+ "new ist.meic.pa.History().saveObject($_,\"  <- "
								+ m.getMethod().getLongName() + " on "
								+ m.getFileName() + ":" + m.getLineNumber()
								+ "\");";
						m.replace(methodCall);
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}

				@Override
				public void edit(NewArray arr) throws CannotCompileException {
					try {
						String src = "";

						src += "$_= ($r) $proceed("+arr.getDimension()+"); new ist.meic.pa.History().saveObject($_"
								+ ",\"  <- NEW "
								+ arr.getComponentType().getName()
								+ " ARRAY on "
								+ arr.getFileName()
								+ ":"
								+ arr.getLineNumber()
								+ "\");";
						arr.replace(src);

					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}

			});
		}

	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {

	}

}
