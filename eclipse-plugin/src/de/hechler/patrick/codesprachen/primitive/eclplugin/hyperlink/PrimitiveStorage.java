package de.hechler.patrick.codesprachen.primitive.eclplugin.hyperlink;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

public class PrimitiveStorage extends PlatformObject implements IStorage {

	private final String name;
	private final URL url;

	public PrimitiveStorage(String name, URL url) {
		this.name = name;
		this.url = url;
	}

	@Override
	public InputStream getContents() throws CoreException {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, getClass(), e.getMessage(), e));
		}
	}

	@Override
	public IPath getFullPath() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimitiveStorage other = (PrimitiveStorage) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
