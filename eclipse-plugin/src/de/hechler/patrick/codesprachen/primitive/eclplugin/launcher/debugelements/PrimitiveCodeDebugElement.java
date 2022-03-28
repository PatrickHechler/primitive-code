package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.File;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;

public abstract class PrimitiveCodeDebugElement implements IDebugElement {

	public static final String PVM_MODEL_IDENTIFIER = "primitive virtaul mashine";
	@Override
	public abstract <T> T getAdapter(Class<T> adapter);

	public static <T> T findAdapter(Class<T> cls, Object... candidates) {
		for (Object candidate : candidates) {
			if (cls.isInstance(candidate)) {
				return cls.cast(candidate);
			}
		}
		return null;
	}

	@Override
	public String getModelIdentifier() {
		return PVM_MODEL_IDENTIFIER;
	}

	@Override
	public abstract PrimitiveCodeDebugTarget getDebugTarget();

	@Override
	public ILaunch getLaunch() {
		return getDebugTarget().launch;
	}

	public abstract String getName();

	/**
	 * execute the given command {@code cmd} on the given
	 * {@link PrimitiveCodeDebugTarget} {@code debug}.
	 * <p>
	 * 
	 * The command will be executed on a separate {@link Thread}.<br>
	 * 
	 * The {@link PrimitiveCodeDebugTarget} will run the {@code type}s
	 * {@link PrimitiveCodeCommandTypes#init} method before executing the given
	 * command {@code cmd}.<br>
	 * 
	 * The {@link PrimitiveCodeDebugTarget} will ensure, that only one command
	 * will be executed at each time.<br>
	 * 
	 * 
	 * When an an other command wants to be executed the
	 * {@link PrimitiveCodeThread#stateChangeRequested()} will return
	 * <code>true</code>, to indicate that the command {@code cmd} should stop
	 * as soon as possible with it's execution<br>
	 * the {@link PrimitiveCodeThread#stateChangeRequested()} flag will be set
	 * to <code>false</code> after the given command {@code cmd} returns and the
	 * {@link PrimitiveCodeDebugTarget} {@code debug} has run it's cleanup.<br>
	 * 
	 * The cleanup of the {@link PrimitiveCodeDebugTarget} {@code debug} will be
	 * automatically called after the command {@code cmd} finished its execution.
	 * 
	 * @param cmd
	 *            the command to be executed
	 * @param debug
	 *            the {@link PrimitiveCodeDebugTarget} on which the command
	 *            should be executed
	 * @param type
	 *            the type of command to be executed
	 * @see PrimitiveCodeThread#stateChangeRequested()
	 */
	public void executeCommand(Runnable cmd, PrimitiveCodeCommandTypes type) {
		executeCommand(this, getDebugTarget(), getName(), cmd, type);
	}

	public static void executeCommand(Object caller, PrimitiveCodeDebugTarget debug, String callerName, Runnable cmd, PrimitiveCodeCommandTypes type) {
		if (caller == null) {
			throw new NullPointerException("caller is null");
		}
		if (debug == null) {
			throw new NullPointerException("debug is null");
		}
		new Thread(() -> {
			boolean successful = false;
			debug.thread.commandInit(caller, type);
			try {
				cmd.run();
				successful = true;
			} finally {
				debug.thread.commandCleanup(caller, type, successful);
			}
		}, callerName + ": " + type).start();
	}

	public static void fireEvent(DebugEvent... event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(event);
		}
	}

	public static AnythingContext getAnythingContextFromLine(int line, ParseContext pc) {
		for (ParseTree pt : pc.children) {
			if (pt instanceof AnythingContext) {
				AnythingContext ac = (AnythingContext) pt;
				if (ac.command == null && ac.CONSTANT_POOL == null || ac.command != null && ac.command.LABEL_DECLARATION != null) {
					continue;
				}
				if (ac.start.getLine() >= line) {
					return ac;
				}
			}
		}
		return null;
	}

	public static AnythingContext getAnythingContextFromPosition(long pos, boolean retLast, ParseContext pc) {
		AnythingContext last = null;
		for (ParseTree pt : pc.children) {
			if (pt instanceof AnythingContext) {
				AnythingContext ac = (AnythingContext) pt;
				if (ac.command == null && ac.CONSTANT_POOL == null || ac.command != null && ac.command.LABEL_DECLARATION != null) {
					continue;
				}
				if (ac.pos_ >= pos) {
					if (retLast) {
						return last;
					} else {
						return ac;
					}
				}
				last = ac;
			}
		}
		return null;
	}

	public static Token getToken(AnythingContext ac, boolean direction) {
		if (ac.command != null) {
			if (direction) {
				return ac.command.start;
			} else {
				return ac.command.stop;
			}
		} else if (ac.CONSTANT_POOL != null) {
			return ac.CONSTANT_POOL;
		} else {
			throw new InternalError("illegal state/line");
		}
	}

	public static IFile findSourceFile(String absolute) {
		if (absolute.endsWith(".pmc")) {
			absolute = absolute.substring(0, absolute.length() - 3) + "psc";
		}
		IFile sourcefile = findSourceFile0(absolute);
		if (sourcefile == null && absolute.startsWith("/mnt/")) { // maybe
																	// running
																	// on WSL
			sourcefile = findSourceFile(absolute.substring(5, 6) + ":" + absolute.substring(6).replaceAll("\\/", "\\\\"));
		}
		return sourcefile;
	}

	private static IFile findSourceFile0(String absolute) {
		IFile[] files;
		files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(absolute).toURI());
		IFile backup = null;
		for (IFile check : files) {
			if (check.exists()) {
				DocumentValue docVal = ValidatorDocumentSetupParticipant.getDocVal(check);
				if (docVal != null) {
					return check;
				}
				backup = check;
			}
		}
		return backup;
	}

}
