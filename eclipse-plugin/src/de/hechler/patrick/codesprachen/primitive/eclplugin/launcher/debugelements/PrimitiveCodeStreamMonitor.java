package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.IBinaryStreamListener;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IBinaryStreamMonitor;
import org.eclipse.debug.core.model.IStreamMonitor;

public class PrimitiveCodeStreamMonitor implements IStreamMonitor, IBinaryStreamMonitor, Runnable {

	private static final int BUFFER_MIN_GROW_SIZE = 1 << 10;
	private static final int BUFFER_MIN_GROW_SIZE_LOW_REVERT = BUFFER_MIN_GROW_SIZE - 1;

	private final InputStream in;
	private byte[] buffer;
	private int pos;
	private Set<IBinaryStreamListener> bsls;
	private Set<IStreamListener> sls;
	private boolean terminated;

	public PrimitiveCodeStreamMonitor(InputStream in) {
		this.in = in;
		this.buffer = new byte[0];
		this.pos = -1;
		this.bsls = new HashSet<>();
		this.sls = new HashSet<>();
		this.terminated = false;
	}

	public void setTerminated() {
		this.terminated = true;
	}

	@Override
	public void flushContents() {
		if (this.pos != -1) {
			this.pos = 0;
		}
	}

	@Override
	public void setBuffered(boolean buffer) {
		if (!buffer) {
			this.pos = -1;
		} else if (this.pos == -1) {
			this.pos = 0;
		}
	}

	@Override
	public boolean isBuffered() {
		return this.pos != -1;
	}

	@Override
	public void addBinaryListener(IBinaryStreamListener listener) {
		this.bsls.add(listener);
	}

	@Override
	public byte[] getData() {
		return Arrays.copyOf(this.buffer, this.pos);
	}

	@Override
	public void removeBinaryListener(IBinaryStreamListener listener) {
		this.bsls.remove(listener);
	}

	@Override
	public void addListener(IStreamListener listener) {
		this.sls.remove(listener);
	}

	@Override
	public String getContents() {
		if (pos == -1) {
			return "";
		} else {
			return new String(buffer, 0, pos, StandardCharsets.UTF_8);
		}
	}

	@Override
	public void removeListener(IStreamListener listener) {
		this.sls.remove(listener);
	}

	@Override
	public void run() {
		while (true) {
			try {
				int avl = in.available();
				if (avl == 0) {
					try {
						if (terminated) {
							return;
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				byte[] bytes;
				if (pos == -1) {
					bytes = new byte[avl];
					int read = in.read(bytes, 0, avl);
					if (avl > read) {
						bytes = Arrays.copyOf(bytes, read);
					}
				} else {
					if (this.buffer.length <= pos + avl) {
						int grow = avl;
						grow &= ~BUFFER_MIN_GROW_SIZE_LOW_REVERT;
						grow |= BUFFER_MIN_GROW_SIZE;
						this.buffer = Arrays.copyOf(this.buffer, this.buffer.length + grow);
					}
					int read = in.read(buffer, pos, avl);
					bytes = new byte[read];
					System.arraycopy(buffer, pos, bytes, 0, read);
					pos += read;
				}
				for (IBinaryStreamListener bsl : this.bsls) {
					bsl.streamAppended(bytes, this);
				}
				String text = new String(bytes, StandardCharsets.UTF_8);
				for (IStreamListener sl : this.sls) {
					sl.streamAppended(text, this);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

