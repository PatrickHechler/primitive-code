package de.patrick.hechler.codesprachen.primitive.asmeditor.objects.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;
import de.patrick.hechler.codesprachen.primitive.asmeditor.objects.HL;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.CpanythingContext;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.PrimitiveAssembler;

public class PrimAsmEditorWindow {
	
	private static final Color COLOR_CONST_CALC_OP = Color.MAGENTA;
	private static final Color COLOR_PRE_AND_ALIGN = Color.YELLOW;
	private static final Color HP_COLOR_COMMENT_OR_DISABLED = Color.GRAY;
	private static final Color COLOR_WRONG = Color.RED;
	private static final Color COLOR_NUMBER = Color.BLUE;
	private static final Color COLOR_PARENS = Color.DARK_GRAY;
	private static final Color COLOR_REGISTER = Color.ORANGE;
	private static final Color COLOR_STRING = Color.GREEN;
	private static final Color COLOR_PARAM_SEP = Color.CYAN;
	private static final Color COLOR_COMMAND = new Color(63, 63, 255);
	private static final Color COLOR_LABEL = new Color(127, 63, 63);
	private static final Color COLOR_CONSTANT = new Color(127, 31, 31);
	private static final Color COLOR_CP_WRITE = new Color(15, 15, 15);
	
	
	
	private static final FileFilter FF_PSC_PMC = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "*.psc, *.pmc";
		}
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String name = f.getName();
			if ("psc".equals(name.substring(name.lastIndexOf('.') + 1))) {
				return true;
			}
			if ("pmc".equals(name.substring(name.lastIndexOf('.') + 1))) {
				return true;
			}
			return false;
		}
		
	};
	private static final FileFilter FF_PSC = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "*.psc";
		}
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String name = f.getName();
			if ("psc".equals(name.substring(name.lastIndexOf('.') + 1))) {
				return true;
			}
			return false;
		}
		
	};
	private static final FileFilter FF_PMC = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "*.pmc";
		}
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String name = f.getName();
			if ("pmc".equals(name.substring(name.lastIndexOf('.') + 1))) {
				return true;
			}
			return false;
		}
		
	};
	private static final String[] EXEC_PARAMS = new String[] {"pvm", "--wait", "--port=" };
	private static final int EXEC_PARAMS_PORT_INDEX = 2;
	
	private JFrame frame;
	private JTextPane textPane;
	private JScrollPane textScrollPane;
	private JButton save;
	private JButton saveAs;
	private JButton load;
	private JButton assemble;
	private JButton debug;
	private File openFile = new File("./my-asm.psc");
	private String lastSaved = "";
	private File currentDir = new File("./");
	
	private PrimAsmEditorWindow() {
	}
	
	public static PrimAsmEditorWindow create() {
		final PrimAsmEditorWindow paew = new PrimAsmEditorWindow();
		paew.frame = new JFrame("Primitive Assembler");
		paew.frame.setLayout(null);
		paew.frame.setBounds(0, 0, 1280, 720);
		paew.frame.setLocationByPlatform(true);
		paew.frame.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				paew.textScrollPane.setBounds(0, 30, paew.frame.getWidth(), paew.frame.getHeight() - 30 - 30);
			}
			
		});
		paew.frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (paew.abortSavesConfirm(null, null)) {
					paew.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				} else {
					paew.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
			}
			
		});
		paew.save = new JButton("save");
		paew.saveAs = new JButton("save as");
		paew.load = new JButton("load");
		paew.assemble = new JButton("assemble");
		paew.debug = new JButton("debug");
		paew.save.setBounds(0, 0, 100, 20);
		paew.saveAs.setBounds(110, 0, 100, 20);
		paew.load.setBounds(220, 0, 100, 20);
		paew.assemble.setBounds(330, 0, 100, 20);
		paew.debug.setBounds(440, 0, 100, 20);
		paew.frame.add(paew.save);
		paew.frame.add(paew.saveAs);
		paew.frame.add(paew.load);
		paew.frame.add(paew.assemble);
		paew.frame.add(paew.debug);
		paew.save.addActionListener(ae -> {
			try (OutputStream out = new FileOutputStream(paew.openFile)) {
				String ls = paew.textPane.getText();
				out.write(ls.getBytes(StandardCharsets.UTF_8));
				paew.lastSaved = ls;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(paew.frame, "save error", e.getClass() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		});
		paew.saveAs.addActionListener(ae -> {
			JFileChooser fc = new JFileChooser(paew.currentDir);
			fc.addChoosableFileFilter(FF_PSC);
			fc.addChoosableFileFilter(FF_PSC_PMC);
			fc.showSaveDialog(paew.frame);
			paew.openFile = fc.getSelectedFile();
			try (OutputStream out = new FileOutputStream(paew.openFile)) {
				String ls = paew.textPane.getText();
				out.write(ls.getBytes(StandardCharsets.UTF_8));
				paew.lastSaved = ls;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(paew.frame, "save error", e.getClass() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		});
		paew.load.addActionListener(ae -> {
			if ( !paew.abortSavesConfirm(null, null)) {
				return;
			}
			JFileChooser fc = new JFileChooser(paew.currentDir);
			fc.addChoosableFileFilter(FF_PSC);
			fc.addChoosableFileFilter(FF_PSC_PMC);
			int a = fc.showOpenDialog(paew.frame);
			switch (a) {
			case JFileChooser.CANCEL_OPTION:
				return;
			case JFileChooser.APPROVE_OPTION: {
				if ( !paew.abortSavesConfirm(null, null)) {
					return;
				}
				break;
			}
			default:
				throw new InternalError("unknown value returned from the JFileChooser: " + a);
			}
			paew.openFile = fc.getSelectedFile();
			paew.currentDir = fc.getCurrentDirectory();
			paew.readFile(paew.openFile, fc);
			paew.rebuild();
		});
		paew.assemble.addActionListener(ae -> {
			JFileChooser fc = new JFileChooser(paew.currentDir);
			fc.addChoosableFileFilter(FF_PMC);
			fc.addChoosableFileFilter(FF_PSC_PMC);
			int a = fc.showSaveDialog(paew.frame);
			switch (a) {
			case JFileChooser.CANCEL_OPTION:
				return;
			case JFileChooser.APPROVE_OPTION: {
				break;
			}
			default:
				throw new InternalError("unknown value returned from the JFileChooser: " + a);
			}
			try (OutputStream out = new FileOutputStream(fc.getSelectedFile())) {
				PrimitiveAssembler asm = new PrimitiveAssembler(out);
				asm.assemble(new StringReader(paew.textPane.getText()));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(paew.frame, "IO EXCEPTION", e.getClass() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
		});
		paew.debug.addActionListener(ae -> {
			JFileChooser fc = new JFileChooser(paew.currentDir);
			fc.addChoosableFileFilter(FF_PSC);
			fc.addChoosableFileFilter(FF_PSC_PMC);
			int a = fc.showSaveDialog(paew.frame);
			switch (a) {
			case JFileChooser.CANCEL_OPTION:
				return;
			case JFileChooser.APPROVE_OPTION: {
				break;
			}
			default:
				throw new InternalError("unknown value returned from the JFileChooser: " + a);
			}
			new Thread(() -> {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					PrimitiveAssembler asm = new PrimitiveAssembler(baos);
					asm.assemble(new StringReader(paew.textPane.getText()));
					paew.startDebugSession(baos.toByteArray());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(paew.frame, "IO EXCEPTION", e.getClass() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
					return;
				}
			}).start();
		});
		paew.textPane = new JTextPane();
		paew.textPane.setFont(new Font("Consolas", Font.PLAIN, 16));
		TabSet tabset = paew.new MyTabSet();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabset);
		paew.textPane.setParagraphAttributes(aset, false);
		// Document doc = paew.textArea.getDocument();
		// if (doc instanceof DefaultStyledDocument) { //JTextPane
		// } else
		// if (doc instanceof PlainDocument) {//JEditorPane
		// ((PlainDocument) doc).putProperty(PlainDocument.tabSizeAttribute, 4);
		// } else {
		// throw new InternalError("unknown document type: " + doc.getClass().getName());
		// }
		paew.textPane.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 0x13:// STRNG + S
					paew.save.doClick();
					break;
				default:
					paew.rebuild();
				}
			}
			
		});
		paew.textPane.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotation = e.getWheelRotation();
				if (e.isControlDown()) {
					zoom(rotation);
				} else {
					scroll(rotation, e.isShiftDown());
				}
			}
			
			private void zoom(int rotation) {
				int textSize = Math.max(2, paew.textPane.getFont().getSize() + (rotation * -2));
				paew.textPane.setFont(new Font("Consolas", Font.BOLD, textSize));
				paew.textPane.invalidate();
				paew.textPane.validate();
				paew.textPane.repaint();
				paew.frame.invalidate();
				paew.frame.validate();
				paew.frame.repaint();
				// paew.buildTabs();
			}
			
			private void scroll(int scroll, boolean horizontalScroll) {
				JScrollBar sb;
				if (horizontalScroll) {
					sb = paew.textScrollPane.getHorizontalScrollBar();
				} else {
					sb = paew.textScrollPane.getVerticalScrollBar();
				}
				sb.setValue(sb.getValue() + scroll * (paew.textPane.getFont().getSize() + 1));
			}
			
		});
		//TODO size JScrollPane/JTextPane
		paew.textScrollPane = new JScrollPane(paew.textPane);;
		paew.textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paew.textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		paew.frame.add(paew.textScrollPane);
		if (paew.openFile.exists()) {
			paew.readFile(paew.openFile, paew.frame);
		}
		return paew;
	}
	//
	// private void buildTabs() {
	// TabStop[] tabs = new TabStop[TAB_COUNT];
	// for (int i = 0; i < TAB_COUNT; i ++ ) {
	// tabs[i] = new TabStop(textSize * 2 * (i + 1), TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
	// }
	// TabSet tabset = new TabSet(tabs);
	// StyleContext sc = StyleContext.getDefaultStyleContext();
	// AttributeSet aset = this.textArea.getParagraphAttributes();
	// aset = sc.addAttribute(aset == null ? SimpleAttributeSet.EMPTY : aset, StyleConstants.TabSet,
	// tabset);
	// this.textArea.setParagraphAttributes(aset, true);
	// }
	
	private class MyTabSet extends TabSet {
		
		/** UID */
		private static final long serialVersionUID = -3292895183865552261L;
		
		public MyTabSet() {
			super(null);
		}
		
		@Override
		public int getTabCount() {
			return Integer.MAX_VALUE;
		}
		
		@Override
		public TabStop getTab(int index) {
			return new TabStop(tabSize() * (index + 1), TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
		}
		
		@Override
		public TabStop getTabAfter(float location) {
			location += tabSize() - (location % tabSize());
			return new TabStop(location, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
		}
		
		@Override
		public int getTabIndex(TabStop tab) {
			return (int) (tab.getPosition() / tabSize());
		}
		
		@Override
		public int getTabIndexAfter(float location) {
			return (int) (location / tabSize()) + 1;
		}
		
		private int tabSize() {
			return PrimAsmEditorWindow.this.textPane.getFontMetrics(PrimAsmEditorWindow.this.textPane.getFont()).charWidth(' ') * 4;
		}
		
		@Override
		public int hashCode() {
			return PrimAsmEditorWindow.this.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( !super.equals(obj)) return false;
			if (getClass() != obj.getClass()) return false;
			MyTabSet other = (MyTabSet) obj;
			if ( !PrimAsmEditorWindow.this.equals(other.getEnclosingInstance())) return false;
			return true;
		}
		
		private final PrimAsmEditorWindow getEnclosingInstance() {
			return PrimAsmEditorWindow.this;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MyTabSet [tabSize(forNow)=").append(tabSize()).append("]");
			return builder.toString();
		}
		
	}
	
	private void startDebugSession(byte[] code) {
		try {
			int port = new Random().nextInt(65535 - 2024) + 2024;
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			String[] execParams = EXEC_PARAMS.clone();
			execParams[EXEC_PARAMS_PORT_INDEX] += port;
			Process pvm = Runtime.getRuntime().exec(execParams);
			final PVMDebugingComunicator dcom = new PVMDebugingComunicator(pvm, new Socket("localhost", port));
			JFrame debugFrame = new JFrame("debug: " + openFile.getPath());
			debugFrame.setBounds(0, 0, 1280, 720);
			debugFrame.setLocationByPlatform(true);
			JTextField pvmsn = new JTextField(257);
			pvmsn.setFont(new Font("Consolas", Font.BOLD, 16));
			pvmsn.setText(dcom.getSnapshot().toString());
			JScrollPane sp = new JScrollPane(pvmsn);
			debugFrame.add(sp);
			JButton next = new JButton("next");
			JButton run = new JButton("run");
			JButton pause = new JButton("pause");
			JButton untilExit = new JButton("until exit");
			JButton untilError = new JButton("until error");
			JButton addBreak = new JButton("add break");
			JButton remBreak = new JButton("rem break");
			JButton toggleBreaks = new JButton("disable breaks");
			next.setBounds(0, 0, 100, 20);
			run.setBounds(110, 0, 100, 20);
			pause.setBounds(220, 0, 100, 20);
			untilExit.setBounds(330, 0, 100, 20);
			untilError.setBounds(440, 0, 100, 20);
			addBreak.setBounds(550, 0, 100, 20);
			remBreak.setBounds(660, 0, 100, 20);
			toggleBreaks.setBounds(770, 0, 100, 20);
			next.addActionListener(ae -> {
				new Thread(() -> {
					try {
						dcom.executeNext();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
			});
			run.addActionListener(ae -> {
				new Thread(() -> {
					try {
						dcom.run();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(PrimAsmEditorWindow.this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
			});
			pause.addActionListener(ee -> {
				new Thread(() -> {
					try {
						dcom.pause();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(PrimAsmEditorWindow.this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
			});
			untilExit.addActionListener(ae -> {
				new Thread(() -> {
					try {
						dcom.executeUntilExit();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(PrimAsmEditorWindow.this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
			});
			untilError.addActionListener(ae -> {
				new Thread(() -> {
					try {
						dcom.executeUntilErrorOrExitCall();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(PrimAsmEditorWindow.this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
			});
			// TODO
			addBreak.addActionListener(ae -> {
			});
			remBreak.addActionListener(ae -> {
			});
			toggleBreaks.addActionListener(ae -> {
			});
			debugFrame.add(next);
			debugFrame.add(run);
			debugFrame.add(untilExit);
			debugFrame.add(untilError);
			debugFrame.add(addBreak);
			debugFrame.add(remBreak);
			debugFrame.add(toggleBreaks);
			
			// TODO
			
			debugFrame.setVisible(true);
			
			
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.frame, e.getClass().getName() + " " + e.getMessage(), "debug error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void readFile(File selected, Component errorHost) {
		String text;
		String name = selected.getName();
		boolean def = false;
		byte[] bytes;
		if (name.endsWith("pmc")) { // disassemble machine files
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrimitiveDisassembler deasm;
			try {
				deasm = new PrimitiveDisassembler(new PrintStream(baos, false, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				def = true;
				deasm = new PrimitiveDisassembler(new PrintStream(baos, false));
			}
			try (InputStream in = new FileInputStream(selected)) {
				deasm.deassemble(0L, in);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this.frame, e.getClass().getSimpleName() + " " + e.getMessage(), "IO EXCEPTION", JOptionPane.ERROR_MESSAGE);
				return;
			}
			bytes = baos.toByteArray();
		} else { // just show other files
			try (InputStream in = new FileInputStream(selected)) {
				long length = selected.length();
				if (length > Integer.MAX_VALUE) {
					JOptionPane.showMessageDialog(errorHost, "too large file: length=" + length + " maxLen=" + Integer.MAX_VALUE, "Large File", JOptionPane.ERROR_MESSAGE);
					return;
				}
				bytes = new byte[(int) length];
				in.read(bytes);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(errorHost, e.getClass().getSimpleName() + " " + e.getMessage(), "IO EXCEPTION", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (def) {
			text = new String(bytes);
		} else {
			try {
				text = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(this.frame,
						e.getClass().getSimpleName() + " " + e.getMessage() + " UTF-8 (part of the StandardCharsets) was supported at the read time, but now it isn't",
						"coruupted charsets", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		this.textPane.setText(text);
		this.lastSaved = text;
		this.rebuild();
	}
	
	/**
	 * returns <code>true</code>, when the {@link #textPane} has been saved, or if the user told to
	 * ignore the changes and <code>false</code> if the {@link #textPane} is not saved and the used
	 * decided to abort the operation.
	 * 
	 * @return
	 *             <code>true</code>, when the {@link #textPane} has been saved, or if the user told to
	 *             ignore the changes and <code>false</code> if the {@link #textPane} is not saved and
	 *             the used
	 *             decided to abort the operation.
	 * @throws InternalError
	 */
	private boolean abortSavesConfirm(String msg, String title) throws InternalError {
		if ( !this.textPane.getText().isEmpty() && !this.lastSaved.equals(this.textPane.getText())) {
			int abort = JOptionPane.showConfirmDialog(this.frame, msg == null ? "abort saves?" : msg, title == null ? "unsaved state!" : title, JOptionPane.YES_NO_OPTION);
			switch (abort) {
			case JOptionPane.NO_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return false;
			case JOptionPane.YES_OPTION:
				break;
			default:
				throw new InternalError("unknown value returned from the JOptionPane: " + abort);
			}
		}
		return true;
	}
	
	public void makeVisible() {
		this.frame.setVisible(true);
	}
	
	private volatile boolean rebuilding = false;
	private volatile boolean rebuildrestart;
	
	private void rebuild() {
		new Thread(this::rebuildExecute).start();
	}
	
	private void rebuildExecute() {
		synchronized (this) {
			if (rebuilding) {
				rebuildrestart = true;
				return;
			}
			rebuilding = true;
		}
		RebuildObj rebuildObj;
		do {
			rebuildrestart = false;
			rebuildObj = prepareRebuild();
			if (rebuildrestart) continue;
			if (rebuildObj == null) {
				rebuildObj = prepareRebuild();
				if (rebuildObj == null) {
					System.err.println("can't highligth the text");
					return;
				}
			}
			makeRebuild(rebuildObj);
		} while (rebuilding);
	}
	
	/**
	 * internal look object used by the {@link #makeRebuild(RebuildObj)} method
	 */
	private final Object makeRebuildLook = new Object();
	
	/**
	 * sets on a successful rebuild {@link #rebuilding} to <code>false</code>
	 * 
	 * @param rebuildObj
	 *            the rebuild object
	 */
	private void makeRebuild(final RebuildObj rebuildObj) {
		synchronized (makeRebuildLook) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					synchronized (makeRebuildLook) {
						try {
							synchronized (this) {
								if (rebuildrestart) {
									return;
								}
								List <HL> hls = new ArrayList <>();
								for (ParseTree parseTree : rebuildObj.parse.children) {
									colorizeHead(parseTree, hls);
								}
								Highlighter highlight = PrimAsmEditorWindow.this.textPane.getHighlighter();
								StyledDocument sdoc = PrimAsmEditorWindow.this.textPane.getStyledDocument();
								highlight.removeAllHighlights();
								SimpleAttributeSet keyWord = new SimpleAttributeSet();
								StyleConstants.setForeground(keyWord, Color.DARK_GRAY);
								sdoc.setCharacterAttributes(0, rebuildObj.text.length(), keyWord, false);
								for (HL hl : hls) {
									if (hl.ishp) {
										try {
											highlight.addHighlight(hl.start, hl.stop + 1, new DefaultHighlightPainter(hl.val));
										} catch (BadLocationException e) {
											throw new InternalError(e);
										}
									} else {
										keyWord = new SimpleAttributeSet();
										StyleConstants.setForeground(keyWord, hl.val);
										sdoc.setCharacterAttributes(hl.start, hl.stop + 1 - hl.start, keyWord, false);
									}
								}
								rebuilding = false;
							}
						} finally {
							makeRebuildLook.notify();// always sync with makeRebuild(...)
						}
					}
				}
				
				private void colorizeHead(ParseTree parseTree, List <HL> hls) {
					if (parseTree instanceof AnythingContext) {
						AnythingContext ac = (AnythingContext) parseTree;
						if (ac.enabled_) {
							for (ParseTree child : ac.children) {
								colorize(child, hls, ac);
							}
						} else {
							int start = ac.getStart().getStartIndex();
							int stop = ac.getStop().getStopIndex();
							hls.add(new HL(HP_COLOR_COMMENT_OR_DISABLED, true, start, stop));
						}
					} else {
						int tok = ((TerminalNode) parseTree).getSymbol().getType();
						assert tok == PrimitiveFileGrammarLexer.EOF;
					}
				}
				
				private void colorize(ParseTree child, List <HL> hls, AnythingContext ac) {
					try {
						colorize0(child, hls, ac);
					} catch (Exception e) {
						int start, end;
						if (child instanceof TerminalNode) {
							TerminalNode tn = (TerminalNode) child;
							start = tn.getSymbol().getStartIndex();
							end = tn.getSymbol().getStopIndex();
						} else if (child instanceof RuleNode) {
							RuleNode rn = (RuleNode) child;
							Interval si = rn.getSourceInterval();
							start = si.a;
							end = si.b;
						} else if (ac != null) {
							Interval si = ac.getSourceInterval();
							start = si.a;
							end = si.b;
						} else {
							throw e;
						}
						hls.add(new HL(COLOR_WRONG, false, start, end));
					}
				}
				
				private void colorize0(ParseTree child, List <HL> hls, AnythingContext ac) {
					if (child instanceof TerminalNode) {
						Token tok = ((TerminalNode) child).getSymbol();
						Color val;
						boolean ishp = false;
						switch (tok.getType()) {
						case PrimitiveFileGrammarLexer.ADD:
						case PrimitiveFileGrammarLexer.ADDC:
						case PrimitiveFileGrammarLexer.ADDFP:
						case PrimitiveFileGrammarLexer.AND:
						case PrimitiveFileGrammarLexer.CALL:
						case PrimitiveFileGrammarLexer.CMP:
						case PrimitiveFileGrammarLexer.DEC:
						case PrimitiveFileGrammarLexer.DEL:
						case PrimitiveFileGrammarLexer.DIV:
						case PrimitiveFileGrammarLexer.DIVFP:
						case PrimitiveFileGrammarLexer.FPTN:
						case PrimitiveFileGrammarLexer.INC:
						case PrimitiveFileGrammarLexer.INT:
						case PrimitiveFileGrammarLexer.IRET:
						case PrimitiveFileGrammarLexer.JMP:
						case PrimitiveFileGrammarLexer.JMPCC:
						case PrimitiveFileGrammarLexer.JMPCS:
						case PrimitiveFileGrammarLexer.JMPEQ:
						case PrimitiveFileGrammarLexer.JMPGE:
						case PrimitiveFileGrammarLexer.JMPGT:
						case PrimitiveFileGrammarLexer.JMPLE:
						case PrimitiveFileGrammarLexer.JMPLT:
						case PrimitiveFileGrammarLexer.JMPNE:
						case PrimitiveFileGrammarLexer.JMPZC:
						case PrimitiveFileGrammarLexer.JMPZS:
						case PrimitiveFileGrammarLexer.LEA:
						case PrimitiveFileGrammarLexer.MOV:
						case PrimitiveFileGrammarLexer.MUL:
						case PrimitiveFileGrammarLexer.MULFP:
						case PrimitiveFileGrammarLexer.NEG:
						case PrimitiveFileGrammarLexer.NOT:
						case PrimitiveFileGrammarLexer.NTFP:
						case PrimitiveFileGrammarLexer.PLUS:
						case PrimitiveFileGrammarLexer.POP:
						case PrimitiveFileGrammarLexer.POS:
						case PrimitiveFileGrammarLexer.PUSH:
						case PrimitiveFileGrammarLexer.RASH:
						case PrimitiveFileGrammarLexer.RET:
						case PrimitiveFileGrammarLexer.RLSH:
						case PrimitiveFileGrammarLexer.SUB:
						case PrimitiveFileGrammarLexer.SUBC:
						case PrimitiveFileGrammarLexer.SUBFP:
						case PrimitiveFileGrammarLexer.SWAP:
							val = COLOR_COMMAND;
							break;
						case PrimitiveFileGrammarLexer.COMMA:
							val = COLOR_PARAM_SEP;
							break;
						case PrimitiveFileGrammarLexer.STR_STR:
							val = COLOR_STRING;
							break;
						case PrimitiveFileGrammarLexer.IP:
						case PrimitiveFileGrammarLexer.SP:
						case PrimitiveFileGrammarLexer.STATUS:
						case PrimitiveFileGrammarLexer.INTCNT:
						case PrimitiveFileGrammarLexer.INTP:
						case PrimitiveFileGrammarLexer.XNN:
							val = COLOR_REGISTER;
							break;
						case PrimitiveFileGrammarLexer.RND_KL_AUF:
						case PrimitiveFileGrammarLexer.RND_KL_ZU:
						case PrimitiveFileGrammarLexer.ECK_KL_AUF:
						case PrimitiveFileGrammarLexer.ECK_KL_ZU:
							val = COLOR_PARENS;
							break;
						case PrimitiveFileGrammarLexer.CONSTANT_POOL:
							val = null;
							ConstsContext cp = Command.parseCP(tok.getText(), ac.constants, ac.labels, ac.pos, ac.align, tok.getLine(), tok.getCharPositionInLine());
							int add = tok.getStartIndex();
							for (ParseTree pt : cp.children) {
								if (pt instanceof CpanythingContext) {
									cpColorizeHead((CpanythingContext) pt, add, hls);
								} else {
									assert ((TerminalNode) pt).getSymbol().getType() == ConstantPoolGrammarLexer.EOF;
								}
							}
							break;
						case PrimitiveFileGrammarLexer.BLOCK_COMMENT:
						case PrimitiveFileGrammarLexer.LINE_COMMENT:
							val = HP_COLOR_COMMENT_OR_DISABLED;
							ishp = true;
							break;
						case PrimitiveFileGrammarLexer.CD_ALIGN:
						case PrimitiveFileGrammarLexer.CD_NOT_ALIGN:
						case PrimitiveFileGrammarLexer.IF:
						case PrimitiveFileGrammarLexer.ELSE:
						case PrimitiveFileGrammarLexer.ELSE_IF:
						case PrimitiveFileGrammarLexer.ENDIF:
							val = COLOR_PRE_AND_ALIGN;
							break;
						case PrimitiveFileGrammarLexer.DOPPELPUNKT:
						case PrimitiveFileGrammarLexer.EXCLUSIVPDER:
						case PrimitiveFileGrammarLexer.EXIST_CONSTANT:
						case PrimitiveFileGrammarLexer.FRAGEZEICHEN:
						case PrimitiveFileGrammarLexer.GETEILT:
						case PrimitiveFileGrammarLexer.GLEICH_GLEICH:
						case PrimitiveFileGrammarLexer.GROESSER:
						case PrimitiveFileGrammarLexer.GROESSER_GLEICH:
						case PrimitiveFileGrammarLexer.OR:
						case PrimitiveFileGrammarLexer.XOR:
						case PrimitiveFileGrammarLexer.UND:
						case PrimitiveFileGrammarLexer.UNGLEICH:
						case PrimitiveFileGrammarLexer.INCLUSIVODER:
						case PrimitiveFileGrammarLexer.MAL:
						case PrimitiveFileGrammarLexer.MINUS:
						case PrimitiveFileGrammarLexer.MODULO:
						case PrimitiveFileGrammarLexer.KLEINER:
						case PrimitiveFileGrammarLexer.KLEINER_GLEICH:
						case PrimitiveFileGrammarLexer.ARITMETISCHER_RECHTS_SCHUB:
							val = COLOR_CONST_CALC_OP;
							break;
						case PrimitiveFileGrammarLexer.NAME:
							if (rebuildObj.parse.constants.containsKey(tok.getText().substring(1))) {
								val = COLOR_CONSTANT;
							} else {
								val = COLOR_LABEL;
							}
							break;
						case PrimitiveFileGrammarLexer.CONSTANT:
							val = COLOR_CONSTANT;
							break;
						case PrimitiveFileGrammarLexer.LABEL_DECLARATION:
							val = COLOR_LABEL;
							break;
						case PrimitiveFileGrammarLexer.UNSIGNED_HEX_NUM:
						case PrimitiveFileGrammarLexer.BIN_NUM:
						case PrimitiveFileGrammarLexer.DEC_FP_NUM:
						case PrimitiveFileGrammarLexer.DEC_NUM:
						case PrimitiveFileGrammarLexer.DEC_NUM0:
						case PrimitiveFileGrammarLexer.OCT_NUM:
						case PrimitiveFileGrammarLexer.HEX_NUM:
						case PrimitiveFileGrammarLexer.NEG_BIN_NUM:
						case PrimitiveFileGrammarLexer.NEG_DEC_NUM:
						case PrimitiveFileGrammarLexer.NEG_DEC_NUM0:
						case PrimitiveFileGrammarLexer.NEG_HEX_NUM:
						case PrimitiveFileGrammarLexer.NEG_OCT_NUM:
							val = COLOR_NUMBER;
							break;
						case PrimitiveFileGrammarLexer.ERROR:
						case PrimitiveFileGrammarLexer.ERROR_HEX:
						case PrimitiveFileGrammarLexer.ERROR_MESSAGE_END:
						case PrimitiveFileGrammarLexer.ERROR_MESSAGE_START:
						case PrimitiveFileGrammarLexer.ANY:
						default:
							val = COLOR_WRONG;
							break;
						}
						if (val != null) {
							hls.add(new HL(val, ishp, tok.getStartIndex(), tok.getStopIndex()));
						}
					} else {
						RuleContext rc = (RuleContext) child;
						int len = rc.getChildCount();
						for (int i = 0; i < len; i ++ ) {
							colorize(rc.getChild(i), hls, null);
						}
					}
				}
				
				private void cpColorizeHead(CpanythingContext ac, int add, List <HL> hls) {
					for (ParseTree pt : ac.children) {
						cpColorize(pt, add, hls, ac);
					}
				}
				
				private void cpColorize(ParseTree pt, int add, List <HL> hls, CpanythingContext ac) {
					try {
						cpColorize0(pt, add, hls, ac);
					} catch (Exception e) {
						if (ac == null) {
							throw e;
						}
						Interval si = ac.getSourceInterval();
						hls.add(new HL(COLOR_WRONG, false, add + si.a, add + si.b));
					}
				}
				
				private void cpColorize0(ParseTree pt, int add, List <HL> hls, CpanythingContext ac) {
					if (pt instanceof RuleContext) {
						RuleContext rc = (RuleContext) pt;
						int len = rc.getChildCount();
						for (int i = 0; i < len; i ++ ) {
							cpColorize(rc.getChild(i), add, hls, null);
						}
					} else {
						Token tok = ((TerminalNode) pt).getSymbol();
						Color val;
						boolean ishp = false;
						switch (tok.getType()) {
						case ConstantPoolGrammarLexer.BLOCK_COMMENT:
						case ConstantPoolGrammarLexer.LINE_COMMENT:
							ishp = true;
							val = HP_COLOR_COMMENT_OR_DISABLED;
							break;
						case ConstantPoolGrammarLexer.CD_ALIGN:
						case ConstantPoolGrammarLexer.CD_NOT_ALIGN:
							val = COLOR_PRE_AND_ALIGN;
							break;
						case ConstantPoolGrammarLexer.WRITE:
							val = COLOR_CP_WRITE;
							break;
						case ConstantPoolGrammarLexer.NAME:
							if (ac.constants_.containsKey(tok.getText())) {
								val = COLOR_CONSTANT;
							} else {
								val = COLOR_LABEL;
							}
							break;
						case ConstantPoolGrammarLexer.MULTI_STR_END:
						case ConstantPoolGrammarLexer.MULTI_STR_START:
							val = COLOR_CONST_CALC_OP;
							break;
						case ConstantPoolGrammarLexer.STR_STR:
							val = COLOR_STRING;
							break;
						case ConstantPoolGrammarLexer.CHAR_STR:
						case ConstantPoolGrammarLexer.CHARS:
							val = COLOR_STRING;
							break;
						case ConstantPoolGrammarLexer.START:
						case ConstantPoolGrammarLexer.ENDE:
							val = COLOR_CONST_CALC_OP;
							break;
						case ConstantPoolGrammarLexer.BYTE:
						case ConstantPoolGrammarLexer.ANY_NUM:
						case ConstantPoolGrammarLexer.DEC_FP_NUM:
						case ConstantPoolGrammarLexer.DEC_NUM:
						case ConstantPoolGrammarLexer.DEC_NUM0:
						case ConstantPoolGrammarLexer.BIN_NUM:
						case ConstantPoolGrammarLexer.OCT_NUM:
						case ConstantPoolGrammarLexer.HEX_NUM:
						case ConstantPoolGrammarLexer.UNSIGNED_HEX_NUM:
						case ConstantPoolGrammarLexer.NEG_BIN_NUM:
						case ConstantPoolGrammarLexer.NEG_DEC_NUM:
						case ConstantPoolGrammarLexer.NEG_DEC_NUM0:
						case ConstantPoolGrammarLexer.NEG_HEX_NUM:
						case ConstantPoolGrammarLexer.NEG_OCT_NUM:
							val = COLOR_NUMBER;
							break;
						case ConstantPoolGrammarLexer.ERROR:
						case ConstantPoolGrammarLexer.ERROR_HEX:
						case ConstantPoolGrammarLexer.ERROR_MESSAGE:
						case ConstantPoolGrammarLexer.ERROR_MESSAGE_END:
						case ConstantPoolGrammarLexer.ERROR_MESSAGE_START:
						default:
							val = COLOR_WRONG;
						}
						if (val != null) {
							hls.add(new HL(val, ishp, add + tok.getStartIndex(), add + tok.getStopIndex()));
						}
					}
				}
				
			});
			try {
				makeRebuildLook.wait();
			} catch (InterruptedException e) {
				throw new InternalError(e);
			}
		}
	}
	
	/**
	 * scans the text and prepares an rebuild
	 * 
	 * @return the prepared rebuild object
	 */
	private RebuildObj prepareRebuild() {
		RebuildObj ro = new RebuildObj();
		try {
			PrimitiveFileGrammarLexer lexer = new PrimitiveFileGrammarLexer(new ANTLRInputStream(new StringReader(ro.text)));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
			ro.parse = parser.parse(0L, true, new HashMap <>(PrimitiveAssembler.START_CONSTANTS));
		} catch (IOException | AssembleError e) {
			JOptionPane.showMessageDialog(this.frame, e.getClass().getName() + " " + e.getMessage(), "error: " + e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return ro;
	}
	
	private class RebuildObj {
		
		private ParseContext parse;
		private final String text = PrimAsmEditorWindow.this.textPane.getText().replaceAll("\r\n?|\n", "\n");
		
	}
	
}
