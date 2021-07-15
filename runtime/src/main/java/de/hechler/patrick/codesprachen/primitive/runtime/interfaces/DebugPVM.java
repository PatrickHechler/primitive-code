package de.hechler.patrick.codesprachen.primitive.runtime.interfaces;

public interface DebugPVM extends PVM {

	void setStackMaxSize(long maxSize) throws OutOfMemoryError, IndexOutOfBoundsException;

	void setRegCount(long count) throws OutOfMemoryError, IndexOutOfBoundsException;

	void step();

	void stepOut();

	void jump(long target) throws IndexOutOfBoundsException;

}