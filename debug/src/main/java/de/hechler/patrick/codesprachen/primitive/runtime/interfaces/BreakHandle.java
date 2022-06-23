package de.hechler.patrick.codesprachen.primitive.runtime.interfaces;

import java.util.Iterator;
import java.util.Set;

public interface BreakHandle extends Set <Long> {
	
	@Override
	default boolean add(Long e) {
		if (e == null) {
			throw new NullPointerException("null add");
		}
		return add((long) e);
	}
	
	/**
	 * adds the breakpoint
	 * 
	 * @param newStop
	 *                the breakpoint to add
	 * @return <code>true</code> if the breakpoint was added and <code>false</code> when the breakpoint already exist
	 * @see #add(Long)
	 */
	boolean add(long newStop);
	
	@Override
	default boolean remove(Object o) {
		if (o == null) return false;
		else if (o instanceof Long) return remove((long) 0);
		else return false;
	}
	
	/**
	 * removes the breakpoint
	 * 
	 * @param formerStop
	 *                   the breakpoint to remove
	 * @return <code>true</code> if the breakpoint was removed and <code>false</code> when the breakpoint did not exist
	 * @see #remove(Object)
	 */
	boolean remove(long formerStop);
	
	@Override
	default boolean contains(Object o) {
		if (o == null) return false;
		else if (o instanceof Long) return contains((long) o);
		else return false;
	}
	
	/**
	 * returns <code>true</code> if the breakpoint exists and <code>false</code> if not
	 * 
	 * @param stop
	 *             the potentially breakpoint
	 * @return <code>true</code> if the breakpoint exists and <code>false</code> if not
	 */
	boolean contains(long stop);
	
	@Override
	int size();
	
	BreakIter iter();
	
	@Override
	default Iterator <Long> iterator() {
		return iter();
	}
	
	/**
	 * removes all breakpoints
	 */
	@Override
	void clear();
	
}
