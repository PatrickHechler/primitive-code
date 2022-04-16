package de.hechler.patrick.codesprachen.primitive.eclplugin.hyperlink;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class PrimitiveEditorInput extends PlatformObject implements IStorageEditorInput {

	private final IStorage storage;
	private final String name;
	private final IPersistableElement persistable;
	private final String toolTipText;

	public PrimitiveEditorInput(IStorage storage, String name, IPersistableElement persistable, String toolTipText) {
		this.storage = storage;
		this.name = name;
		this.persistable = persistable;
		this.toolTipText = toolTipText;
	}

	@Override
	public IStorage getStorage() {
		return storage;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		return persistable;
	}

	@Override
	public String getToolTipText() {
		return toolTipText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((persistable == null) ? 0 : persistable.hashCode());
		result = prime * result + ((storage == null) ? 0 : storage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		PrimitiveEditorInput other = (PrimitiveEditorInput) obj;
		if (persistable == null) {
			if (other.persistable != null) {
				return false;
			}
		} else if (!persistable.equals(other.persistable)) {
			return false;
		}
		if (storage == null) {
			if (other.storage != null) {
				return false;
			}
		} else if (!storage.equals(other.storage)) {
			return false;
		}
		return true;
	}

}
