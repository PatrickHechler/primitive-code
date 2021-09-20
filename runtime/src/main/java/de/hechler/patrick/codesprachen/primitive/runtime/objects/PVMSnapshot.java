package de.hechler.patrick.codesprachen.primitive.runtime.objects;


public class PVMSnapshot {
	
	public long ax;
	public long bx;
	public long cx;
	public long dx;
	public long ip;
	public long sp;
	public long intp;
	public long status;
	
	public PVMSnapshot(long ax, long bx, long cx, long dx, long ip, long sp, long intp, long status) {
		this.ax = ax;
		this.bx = bx;
		this.cx = cx;
		this.dx = dx;
		this.ip = ip;
		this.sp = sp;
		this.intp = intp;
		this.status = status;
	}

	@Override
	public String toString() {
		return "PVMSnapshot [ax=" + ax + ", bx=" + bx + ", cx=" + cx + ", dx=" + dx + ", ip=" + ip + ", sp=" + sp + ", intp=" + intp + ", status=" + status + "]";
	}
	
}
