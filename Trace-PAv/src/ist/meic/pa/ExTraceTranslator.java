package ist.meic.pa;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class ExTraceTranslator implements Translator {

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		makeTracable(ctClass, pool);

	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {

	}

	private void makeTracable(CtClass ctClass, final ClassPool pool)
			throws CannotCompileException, NotFoundException {
		String methodSaveReturn = "";
		for (final CtBehavior ctMethod : ctClass.getDeclaredBehaviors()) {
			methodSaveReturn = "";

			ctMethod.instrument(new ExprEditor() {
				public void edit(NewExpr newEx) throws CannotCompileException {
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

				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					try {

						String methodCall = "";
						for (int i = 1; i <= m.getMethod().getParameterTypes().length; i++) {
							methodCall = saveObjectArgString(m, methodCall, i);
						}
						methodCall = saveObjectRetString(m, methodCall);
						m.replace(methodCall);
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void edit(Cast c) throws CannotCompileException {
					String src = "";
					System.out.println("PRINT CAST");
					try {
						src += "$_=$proceed($$); ist.meic.pa.History.saveObject($_"
								+ ",\" CAST "
								+ c.getType().getName()
								+ " on "
								+ c.getFileName()
								+ ":"
								+ c.getLineNumber()
								+ "\");";
						c.replace(src);
					} catch (NotFoundException e1) {
						e1.printStackTrace();
					}

				}

				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					String src = "";
					if (f.isStatic())
						return;
					if (f.isReader()) {
						// System.out.println("Coisas: " + f.getFieldName() +
						// " " + f.getLineNumber());
						src = "$_=$0."
								+ f.getFieldName()
								+ ";"
								+ " ist.meic.pa.History.saveObject($_"// +
																		// f.getFieldName()
								+ ",\" GET " + f.getFieldName()
								+ " on \"+$_+\"" + f.getFileName() + ":"
								+ f.getLineNumber() + "\");";
						f.replace(src);

					} else if (f.isWriter()) {
						src = "ist.meic.pa.History.saveObject($1" + ",\" SET "
								+ f.getFieldName() + " on " + f.getFileName()
								+ ":" + f.getLineNumber() + "\");";
						src += "$0." + f.getFieldName() + " = $1;";
						f.replace(src);
					}
				}

				@Override
				public void edit(Handler h) throws CannotCompileException {
					try {
						System.out.println(" COISAS " + h.getType().getName()
								+ " " + h.getEnclosingClass().getSimpleName());

					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}

			});
		}

	}

	private String saveObjectArgString(MethodCall m, String methodCall, int i)
			throws NotFoundException {
		methodCall += " if(!(($w)$" + i + ").getClass().isPrimitive()) "
				+ "	ist.meic.pa.History.saveObject((($w)$" + i + "),\"  -> "
				+ m.getMethod().getLongName() + " on " + m.getFileName() + ":"
				+ m.getLineNumber() + "\");";
		return methodCall;
	}

	private String saveObjectRetString(MethodCall m, String methodCall)
			throws NotFoundException {
		methodCall += "$_=$proceed($$);"
				+ "if((($w)$_) != null && !(($w)$_).getClass().isPrimitive()) "
				+ " ist.meic.pa.History.saveObject(($w)$_,\"  <- "
				+ m.getMethod().getLongName() + " on " + m.getFileName() + ":"
				+ m.getLineNumber() + "\");";
		return methodCall;
	}
}
