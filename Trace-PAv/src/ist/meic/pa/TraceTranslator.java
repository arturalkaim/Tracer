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
					|| Modifier.isNative(ctMethod.getModifiers()))
				continue;
			/*
			 * System.out.println(ctMethod.getLongName() + "   " +
			 * ctMethod.getParameterTypes().length); /* if
			 * (ctMethod.getParameterTypes().length == 0) continue;
			 * 
			 * for (int i = 1; i <= ctMethod.getParameterTypes().length; i++) {
			 * methodSaveArgs += " new ist.meic.pa.History().saveObject(\"-> " +
			 * ctMethod.getLongName() + "\",$" + i + ");"; }
			 * ctMethod.insertBefore(methodSaveArgs);
			 * 
			 * /* methodSaveArgs +=
			 * " new ist.meic.pa.History().saveObject($_,\"-> " +
			 * ctMethod.getLongName() + " on " + ctMethod.getGenericSignature()
			 * + ":" + ctMethod.getMethodInfo().getLineNumber(0) + "\");";
			 */
			/*methodSaveReturn += " new ist.meic.pa.History().saveObject(\"<- "
					+ ctMethod.getLongName() + " on \",$_ );";
			/*
			 * methodSaveReturn = "try {" +
			 * " Class ca = Class.forName(\"ist.meic.pa.History\");" +
			 * "	java.lang.reflect.Method ca = ca.getMethod(\"saveObject\", new Class[]{Object.class,String.class}); "
			 * + "ca.setAccessible(true);" +
			 * "ca.invoke(null, new Object[] {$_	, \"<- " +
			 * ctMethod.getLongName() + " on " + ctMethod.getDeclaringClass() +
			 * ":" + ctMethod.getMethodInfo().getLineNumber(0) + "\"});" +
			 * "	} catch (ClassNotFoundException e) {" +
			 * "		e.printStackTrace();" + "	} catch (NoSuchMethodException e) {"
			 * + "		e.printStackTrace();" + "	} catch (SecurityException e) {" +
			 * "		e.printStackTrace();" + "	}";
			 */
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
								+ ",\" <- "
								+ newEx.getConstructor().getLongName()
								+ " on "
								+ newEx.getFileName()
								+ ":"
								+ newEx.getLineNumber() + "\");";
					} catch (NotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					newEx.replace(src);
				}

				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					try {
						if (m.getClassName().startsWith("ist.meic.pa.History")
								|| 
								m.getMethod().getLongName()
										.startsWith("java.lang")
								|| m.getMethod().getLongName()
										.startsWith("java.util"))
							return;

						/*System.out.println(m.getMethodName() + " "
								//+ m.getMethod().getDeclaringClass() + "  "
								+ m.getFileName() + " at line: "
								+ m.getLineNumber());*/

						String methodCall = "$_=$proceed($$);";
						for (int i = 1; i <= m.getMethod().getParameterTypes().length; i++) {
							methodCall += " if(!$" + i
									+ ".getClass().isPrimitive()) "
									+ "	new ist.meic.pa.History().saveObject($"
									+ i + ",\" -> "
									+ m.getMethod().getLongName() + " on "
									+ m.getFileName() + ":" + m.getLineNumber()
									+ "\"); ";
						}
						methodCall += "if($_ != null && !$_.getClass().isPrimitive()) "
								+ "new ist.meic.pa.History().saveObject($_,\" <- "
								+ m.getMethod().getLongName()
								+ " on "
								+ m.getFileName()
								+ ":"
								+ m.getLineNumber()
								+ "\");";
						
						m.replace(methodCall);
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
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
