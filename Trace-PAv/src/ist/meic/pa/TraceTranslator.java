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
		if (ctClass.getPackageName() != null
				&& ctClass.getPackageName().equals("ist.meic.pa")
				&& !ctClass.getName().equals("ist.meic.pa.Trace"))
			return;
		for (final CtBehavior ctMethod : ctClass.getDeclaredBehaviors()) {

			ctMethod.instrument(new ExprEditor() {
				public void edit(NewExpr newEx) throws CannotCompileException {
					newExprEval(newEx);
				}

				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					try {
						if (m.getClassName().startsWith("ist.meic.pa.History")
								|| m.getMethod().getLongName()
										.startsWith("java.lang"))
							return;

						/*System.out.println(m.getMethodName() + " "
								+ m.getFileName() + " at line: "
								+ m.getLineNumber());*/

						String methodCall = "";
						for (int i = 1; i <= m.getMethod().getParameterTypes().length; i++) {
							methodCall = saveObjectArg(m, methodCall, i);
						}
						methodCall = saveObjectRet(m, methodCall);
						m.replace(methodCall);
					} catch (NotFoundException e) {
						e.printStackTrace();
					}catch (Exception e) {
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

	private void newExprEval(NewExpr newEx) throws CannotCompileException {
		if (newEx.getClassName().startsWith("ist.meic.pa.History"))
			return;
		String src = "";
		/*
		 * System.out.println(newEx.getClassName() + " " +
		 * newEx.getFileName() + " at line: " +
		 * newEx.getLineNumber());
		 */
		try {
			src += "$_=$proceed($$); ist.meic.pa.History.saveObject($_"
					+ ",\"  <- "
					+ newEx.getConstructor().getLongName()
					+ " on "
					+ newEx.getFileName()
					+ ":"
					+ newEx.getLineNumber() + "\");";
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}

		newEx.replace(src);
	}

	private String saveObjectArg(MethodCall m, String methodCall, int i)
			throws NotFoundException {
		methodCall += " if(!$" + i
				+ ".getClass().isPrimitive()) "
				+ "	ist.meic.pa.History.saveObject($"
				+ i + ",\"  -> "
				+ m.getMethod().getLongName() + " on "
				+ m.getFileName() + ":" + m.getLineNumber()
				+ "\"); ";
		return methodCall;
	}

	private String saveObjectRet(MethodCall m, String methodCall)
			throws NotFoundException {
		methodCall += "$_=$proceed($$);"
				+ "if((($w)$_) != null && !(($w)$_).getClass().isPrimitive()) "
				+ " ist.meic.pa.History.saveObject(($w)$_,\"  <- "
				+ m.getMethod().getLongName() + " on "
				+ m.getFileName() + ":" + m.getLineNumber()
				+ "\");";
		return methodCall;
	}

}
