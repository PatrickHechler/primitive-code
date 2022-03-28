package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.CpanythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;

public class PscReconcilerStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	@SuppressWarnings("unused")
	private IProgressMonitor monitor;
	private IDocument document;
	private ProjectionViewer projectionViewer;
	private List<Annotation> oldAnnotations = new ArrayList<>();
	private List<Position> oldPositions = new ArrayList<>();

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void setProjectionViewer(ProjectionViewer projectionViewer) {
		this.projectionViewer = projectionViewer;
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		doNotReconcile();
	}

	@Override
	public void reconcile(IRegion partition) {
		doNotReconcile();
	}

	@Override
	public void initialReconcile() {
		doNotReconcile();
	}

	//@formatter:off
	private void doNotReconcile() {}
	@SuppressWarnings("unused")
	private void doReconcile() {
	//@formatter:on
		List<Position> positions = getNewPositionsOfAnnotations();

		ProjectionAnnotationModel am = projectionViewer.getProjectionAnnotationModel();
		Set<Position> alreadyThere = new HashSet<>();
		{
			Set<Position> zw = new HashSet<>(positions);
			for (Iterator<Annotation> iter = am.getAnnotationIterator(); iter.hasNext();) {
				Annotation a = iter.next();
				Position pos = am.getPosition(a);
				if (!zw.contains(pos)) {
					iter.remove();
				} else {
					alreadyThere.add(pos);
				}
			}
		}

		for (Position position : positions) {
			Annotation annotation = new ProjectionAnnotation();
			if (!alreadyThere.contains(position)) {
				am.addAnnotation(annotation, position);
			}
			oldPositions.add(position);
			oldAnnotations.add(annotation);
		}
	}

	private List<Position> getNewPositionsOfAnnotations() {
		List<Position> positions = new ArrayList<>();
		DocumentValue val = getDocVal(document);
		for (ParseTree pt : val.context.children) {
			if (pt instanceof AnythingContext) {
				AnythingContext ac = (AnythingContext) pt;
				if (ac.CONSTANT_POOL != null) {
					int start = ac.start.getStartIndex();
					int stop = ac.stop.getStopIndex();
					positions.add(new Position(start, stop - start + 1));
					for (ParseTree cp__ac : ((ConstsContext) ac.zusatz).children) {
						if (cp__ac instanceof CpanythingContext) {
							CpanythingContext cp_ac = (CpanythingContext) cp__ac;
							for (ParseTree cp_pt : cp_ac.children) {
								if (cp_pt instanceof ConstantPoolGrammarParser.CommentContext) {
									ConstantPoolGrammarParser.CommentContext cp_c = (ConstantPoolGrammarParser.CommentContext) cp_pt;
									start = cp_c.start.getStartIndex();
									stop = cp_c.stop.getStopIndex();
									positions.add(new Position(start, stop - start + 1));
								}
							}
						}
					}
				} else {
					for (ParseTree cp_pt : ac.children) {
						if (cp_pt instanceof PrimitiveFileGrammarParser.CommentContext) {
							PrimitiveFileGrammarParser.CommentContext c = (PrimitiveFileGrammarParser.CommentContext) cp_pt;
							int start = c.start.getStartIndex();
							int stop = c.stop.getStopIndex();
							positions.add(new Position(start, stop - start + 1));
						}
					}
				}
			}
		}
		return positions;
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

}
