package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.Handler;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class ExTraceTranslator extends TraceTranslator {


	protected void makeTracable(CtClass ctClass, final ClassPool pool)
			throws CannotCompileException, NotFoundException {
		String methodSaveReturn;
		super.makeTracable(ctClass, pool);
		
		for (final CtBehavior ctMethod : ctClass.getDeclaredBehaviors()) {
			methodSaveReturn = "";

			ctMethod.instrument(new ExprEditor() {
				
				@Override
				public void edit(Cast c) throws CannotCompileException {
					String src = "";
					try {
						src = castReplacer(c, src);
						c.replace(src);
					} catch (NotFoundException e1) {
						e1.printStackTrace();
					}

				}

				@Override
				public void edit(FieldAccess f) throws CannotCompileException {
					fieldAccessProcess(f);
				}

				@Override
				public void edit(Handler h) throws CannotCompileException {
					try {
						CtClass type = h.getType();
						String handle = "";
						if (type != null) {
							handle = handleCatchReplacer(h);
						} else {
							handle = handleFinallyReplacer(h);
						}
						h.insertBefore(handle);
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}


			});
		}

	}

	/**
	 * @param h
	 * @return
	 */
	private String handleFinallyReplacer(Handler h) {
		String handle;
		handle = " ist.meic.pa.History.saveObject($1,\"  <- finally block on "
				+ h.getFileName()
				+ ":"
				+ h.getLineNumber()
				+ "\");";
		return handle;
	}
	
	/**
	 * @param h
	 * @return
	 * @throws NotFoundException
	 */
	private String handleCatchReplacer(Handler h)
			throws NotFoundException {
		String handle;
		handle = " ist.meic.pa.History.saveObject($1,\"  <- "
				+ h.getType().getName() + " handled on "
				+ h.getFileName() + ":" + h.getLineNumber()
				+ "\");";
		return handle;
	}
	/**
	 * @param f
	 * @throws CannotCompileException
	 */
	private void fieldAccessProcess(FieldAccess f)
			throws CannotCompileException {
		String src;
		if (f.isStatic())
			return;
		if (f.isReader()) {
			src = fieldAccessReadReplacer(f);
			f.replace(src);

		} else if (f.isWriter()) {
			src = fieldAccessWriterReplacer(f);
			f.replace(src);
		}
	}

	/**
	 * @param f
	 * @return
	 */
	private String fieldAccessWriterReplacer(FieldAccess f) {
		String src;
		src = "ist.meic.pa.History.saveObject($1" + ",\" SET "
				+ f.getFieldName() + " on " + f.getFileName() + ":"
				+ f.getLineNumber() + "\");" + "$0." + f.getFieldName()
				+ " = $1;";
		return src;
	}

	/**
	 * @param c
	 * @param src
	 * @return
	 * @throws NotFoundException
	 */
	private String castReplacer(Cast c, String src) throws NotFoundException {
		src += "$_=$proceed($$); ist.meic.pa.History.saveObject($_"
				+ ",\" CAST " + c.getType().getName() + " on "
				+ c.getFileName() + ":" + c.getLineNumber() + "\");";
		return src;
	}

	/**
	 * @param f
	 * @return
	 */
	private String fieldAccessReadReplacer(FieldAccess f) {
		String src;
		src = "$_=$0."
				+ f.getFieldName()
				+ ";"
				+ " ist.meic.pa.History.saveObject($_"// +
														// f.getFieldName()
				+ ",\" GET " + f.getFieldName() + " on \"+$_+\""
				+ f.getFileName() + ":" + f.getLineNumber() + "\");";
		return src;
	}

	/**
	 * @param newEx
	 * @param src
	 * @return
	 * @throws NotFoundException
	 */
	private String newExprReplacer(NewExpr newEx, String src)
			throws NotFoundException {
		src += "$_=$proceed($$); ist.meic.pa.History.saveObject($_"
				+ ",\"  <- " + newEx.getConstructor().getLongName() + " on "
				+ newEx.getFileName() + ":" + newEx.getLineNumber() + "\");";
		return src;
	}
}
