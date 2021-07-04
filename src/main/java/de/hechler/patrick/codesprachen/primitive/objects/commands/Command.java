package de.hechler.patrick.codesprachen.primitive.objects.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;
import edu.emory.mathcs.backport.java.util.Arrays;

public abstract class Command {
	
	public final Commands art;
	public final List <Wert> params;
	
	
	
	@SuppressWarnings("unchecked")
	protected Command(Commands art, Wert... params) {
		this.art = art;
		this.params = Collections.unmodifiableList(Arrays.asList(params.clone()));
	}
	
	protected Command(Commands art, List <Wert> params) {
		this.art = art;
		this.params = Collections.unmodifiableList(new ArrayList <>(params));
	}
	
	
	
	public Wert param(int index) {
		return params.get(index);
	}
	
}
