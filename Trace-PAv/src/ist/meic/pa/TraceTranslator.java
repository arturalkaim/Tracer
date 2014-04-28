package ist.meic.pa;

import javassist.*;
import javassist.expr.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;

public class TraceTranslator implements Translator {

	protected String methodSaveArgs = "";

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		makeTracable(ctClass, pool);

	}

	private void makeTracable(CtClass ctClass, final ClassPool pool)
			throws CannotCompileException {
		if (ctClass.getPackageName().equals("ist.meic.pa"))
			return;
		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			ctMethod.instrument(new ExprEditor() {
				public void edit(NewExpr newEx) throws CannotCompileException {
					String src = "";
					/*
					 * System.out.println(newEx.getClassName() + " " +
					 * newEx.getFileName() + " at line: " +
					 * newEx.getLineNumber());
					 *
					methodSaveArgs += "try {"
							+ "$_ = $proceed($$);"
							+ " Class ca = Class.forName(\"ist.meic.pa.History\");"
							+ "	java.lang.reflect.Method ca = ca.getMethod(\"saveObject\", new Class[]{Object.class,String.class}); "
							+ "ca.setAccessible(true);"
							+ "ca.invoke(null, new Object[] {$_"
							+ ", \"-> "
							+ newEx.getSignature()
							+ " on " + newEx.getFileName() + ":" + newEx.getLineNumber() + "\"});"
							+ "	} catch (ClassNotFoundException e) {"
							+ "		e.printStackTrace();"
							+ "	} catch (NoSuchMethodException e) {"
							+ "		e.printStackTrace();"
							+ "	} catch (SecurityException e) {"
							+ "		e.printStackTrace();" + "	}";*/
					try {
						newEx.getConstructor().insertAfter(src);
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				public void edit(MethodCall methCall)
						throws CannotCompileException {
					/*
					 * System.out.println(methCall.getMethodName() + " @: " +
					 * methCall.getLineNumber());
					 */
					try {
						/*
						 * methodSaveArgs += "System.err.println(\"CENAS " +
						 * methCall.getMethodName() + "\");";
						 */
						int j = Modifier.isStatic(methCall.getMethod()
								.getModifiers()) ? 1 : 1;
						for (int i = 1; i <= methCall.getMethod()
								.getParameterTypes().length; i++) {
						
							methodSaveArgs += "try {"
									+ " Class ca = Class.forName(\"ist.meic.pa.History\");"
									+ "	java.lang.reflect.Method ca = ca.getMethod(\"saveObject\", new Class[]{Object.class,String.class}); "
									+ "ca.setAccessible(true);"
									+ "ca.invoke(null, new Object[] {$"
									+ // new ist.meic.pa.test.Test()"
									+i + ", \"-> "
									+ methCall.getMethod().getLongName()
									+ " on " + methCall.getFileName() + ":" + methCall.getLineNumber() + "\"});"
									+ "	} catch (ClassNotFoundException e) {"
									+ "		e.printStackTrace();"
									+ "	} catch (NoSuchMethodException e) {"
									+ "		e.printStackTrace();"
									+ "	} catch (SecurityException e) {"
									+ "		e.printStackTrace();" + "	}";

						}
						// methodSaveArgs += " $_ = $proceed($$);";
						methCall.getMethod().insertBefore(
								"{" + methodSaveArgs + "}");

					} catch (NotFoundException e) {
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
