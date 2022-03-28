package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;

public class PscContentAssistProcessor implements IContentAssistProcessor {

	// @formatter:off
	private static final List<String> NO_CONST_POOL = Arrays.asList(new String[] {
			"~READ_SYM",
			"~IF", "~ELSEIF", "~ELSE", "~ENDIF",
			"MOV", "ADD", "SUB", "MUL", "DIV", "AND", "OR", "XOR", "NOT", "NEG",
			"LSH", "RLSH", "RASH", "DEC", "INC", "JMP", "JMPEQ", "JMPNE", "JMPGT",
			"JMPGE", "JMPLT", "JMPLE", "JMPCS", "JMPCC", "JMPZS", "JMPZC", "CALL",
			"CMP", "RET", "INT", "PUSH", "POP", "IRET", "SWAP", "LEA", "ADDC",
			"SUBC", "ADDFP", "SUBFP", "MULFP", "DIVFP", "NTFP", "FPTN",
			"--POS--",
			"IP", "SP", "STATUS", "INTP", "INTCNT",
			"X00", "X01", "X02", "X03", "X04", "X05", "X06", "X07", "X08", "X09", "X0A", "X0B", "X0C", "X0D", "X0E", "X0F", 
			"X10", "X11", "X12", "X13", "X14", "X15", "X16", "X17", "X18", "X19", "X1A", "X1B", "X1C", "X1D", "X1E", "X1F", 
			"X20", "X21", "X22", "X23", "X24", "X25", "X26", "X27", "X28", "X29", "X2A", "X2B", "X2C", "X2D", "X2E", "X2F", 
			"X30", "X31", "X32", "X33", "X34", "X35", "X36", "X37", "X38", "X39", "X3A", "X3B", "X3C", "X3D", "X3E", "X3F", 
			"X40", "X41", "X42", "X43", "X44", "X45", "X46", "X47", "X48", "X49", "X4A", "X4B", "X4C", "X4D", "X4E", "X4F", 
			"X50", "X51", "X52", "X53", "X54", "X55", "X56", "X57", "X58", "X59", "X5A", "X5B", "X5C", "X5D", "X5E", "X5F", 
			"X60", "X61", "X62", "X63", "X64", "X65", "X66", "X67", "X68", "X69", "X6A", "X6B", "X6C", "X6D", "X6E", "X6F", 
			"X70", "X71", "X72", "X73", "X74", "X75", "X76", "X77", "X78", "X79", "X7A", "X7B", "X7C", "X7D", "X7E", "X7F", 
			"X80", "X81", "X82", "X83", "X84", "X85", "X86", "X87", "X88", "X89", "X8A", "X8B", "X8C", "X8D", "X8E", "X8F", 
			"X90", "X91", "X92", "X93", "X94", "X95", "X96", "X97", "X98", "X99", "X9A", "X9B", "X9C", "X9D", "X9E", "X9F", 
			"XA0", "XA1", "XA2", "XA3", "XA4", "XA5", "XA6", "XA7", "XA8", "XA9", "XAA", "XAB", "XAC", "XAD", "XAE", "XAF", 
			"XB0", "XB1", "XB2", "XB3", "XB4", "XB5", "XB6", "XB7", "XB8", "XB9", "XBA", "XBB", "XBC", "XBD", "XBE", "XBF", 
			"XC0", "XC1", "XC2", "XC3", "XC4", "XC5", "XC6", "XC7", "XC8", "XC9", "XCA", "XCB", "XCC", "XCD", "XCE", "XCF", 
			"XD0", "XD1", "XD2", "XD3", "XD4", "XD5", "XD6", "XD7", "XD8", "XD9", "XDA", "XDB", "XDC", "XDD", "XDE", "XDF", 
			"XE0", "XE1", "XE2", "XE3", "XE4", "XE5", "XE6", "XE7", "XE8", "XE9", "XEA", "XEB", "XEC", "XED", "XEE", "XEF", 
			"XF0", "XF1", "XF2", "XF3", "XF4", "XF5", "XF6", "XF7", "XF8", "XF9", "XFA",
	});
	private static final List<String> IN_CONST_POOL = Arrays.asList(new String[] {
			"CHARS", 
	}); // @formatter:on

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		List<ICompletionProposal> result = new ArrayList<>();
		IDocument doc = viewer.getDocument();
		String text = doc.get().substring(0, offset);
		String last = getLastPart(text);
		TokenInfo inf = getTokenInfo(getDocVal(doc), offset);
		addCompletetions(offset, result, last, inf.ac.constants_.keySet(), inf.ac.labels_.keySet());
		addCompletetions(offset, result, last, inf.cpac == null ? NO_CONST_POOL : IN_CONST_POOL);
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	@SuppressWarnings("unchecked")
	private void addCompletetions(int offset, List<ICompletionProposal> result, String last, Iterable<?>... iterables) {
		for (Iterable<?> iter : iterables) {
			for (String complete : (Iterable<String>) iter) {
				if (complete.startsWith(last)) {
					result.add(new CompletionProposal(complete.substring(last.length()), offset, 0, complete.length() - last.length(), null, complete, null, null));
				}
			}
		}
	}

	/* @formatter:off
		// todo this is logic for .project file to complete on nature and project
		// references. Replace with your language logic!
		String _text = viewer.getDocument().get();
		String natureTag = "<nature>";
		String projectReferenceTag = "<project>";
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (_text.length() >= natureTag.length()
				&& _text.substring(offset - natureTag.length(), offset).equals(natureTag)) {
			IProjectNatureDescriptor[] natureDescriptors = workspace.getNatureDescriptors();
			ICompletionProposal[] proposals = new ICompletionProposal[natureDescriptors.length];
			for (int i = 0; i < natureDescriptors.length; i++) {
				IProjectNatureDescriptor descriptor = natureDescriptors[i];
				proposals[i] = new CompletionProposal(descriptor.getNatureId(), offset, 0,
						descriptor.getNatureId().length());
			}
			return proposals;
		}
		if (_text.length() >= projectReferenceTag.length()
				&& _text.substring(offset - projectReferenceTag.length(), offset).equals(projectReferenceTag)) {
			IProject[] projects = workspace.getRoot().getProjects();
			ICompletionProposal[] proposals = new ICompletionProposal[projects.length];
			for (int i = 0; i < projects.length; i++) {
				proposals[i] = new CompletionProposal(projects[i].getName(), offset, 0, projects[i].getName().length());
			}
			return proposals;
		}
		return new ICompletionProposal[0];
		@formatter:on */

	private String getLastPart(String text) {
		int[] lasts = new int[]{ //@formatter:off
			text.lastIndexOf(' '),
			text.lastIndexOf('\t'),
			text.lastIndexOf('\r'),
			text.lastIndexOf('\n'),
			text.lastIndexOf('#') - 1,
			text.lastIndexOf('@') - 1,
		}; //@formatter:on
		int lastest = -1;
		for (int i = 0; i < lasts.length; i++) {
			lastest = Math.max(lastest, lasts[i]);
		}
		return text.substring(lastest + 1);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[]{'@', '#'};
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}