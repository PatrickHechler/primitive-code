package de.hechler.patrick.codesprachen.primitive.assemble.programs;

import static de.hechler.patrick.zeugs.check.Assert.assertEquals;
import static de.hechler.patrick.zeugs.check.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.End;
import de.hechler.patrick.zeugs.check.anotations.MethodParam;
import de.hechler.patrick.zeugs.check.anotations.Start;
import de.hechler.patrick.zeugs.check.exceptions.CheckerException;
import de.hechler.patrick.zeugs.pfs.FSProvider;
import de.hechler.patrick.zeugs.pfs.interfaces.FS;
import de.hechler.patrick.zeugs.pfs.interfaces.FSElement;
import de.hechler.patrick.zeugs.pfs.interfaces.File;
import de.hechler.patrick.zeugs.pfs.interfaces.WriteStream;
import de.hechler.patrick.zeugs.pfs.misc.ElementType;
import de.hechler.patrick.zeugs.pfs.opts.PatrFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.StreamOpenOptions;
import edu.emory.mathcs.backport.java.util.Arrays;

@CheckClass
@SuppressWarnings("static-method")
public class ProgramChecker {
	
	private static final byte[] EMPTY_BARR = new byte[0];
	
	private static final StreamOpenOptions CREATE_FILE_OPTS = new StreamOpenOptions(false, true, false,
			ElementType.file, false, true);
	
	private static final String HELLO_WORLD_PMF = "/hello-world";
	private static final String HELLO_WORLD_PFS = "./testout/hello-world.pfs";
	private static final String HELLO_WORLD_RES = "/de/hechler/patrick/codesprachen/primitive/assemble/programs/hello-world.psc";
	
	private FSProvider patrFsProv;
	
	@Start
	private void init() throws IOException {
		Files.createDirectories(Path.of("./testout/"));
	}
	
	@Start
	private void start(@MethodParam Method met) throws NoSuchProviderException {
		System.out.println("check now " + met.getName());
		patrFsProv = FSProvider.ofName(FSProvider.PATR_FS_PROVIDER_NAME);
	}
	
	@End
	private void end(@MethodParam Method met) {
		System.out.println("finished " + met.getName() + " check");
		patrFsProv.loadedFS().forEach(fs -> {
			try {
				fs.close();
			} catch (IOException e) {
				throw new IOError(e);
			}
		});
	}
	
	@Check
	private void checkHelloWorld() throws IOException, InterruptedException {
		try (FS fs = patrFsProv.loadFS(new PatrFSOptions(HELLO_WORLD_PFS, true, 4096L, 1024))) {
			System.out.println("opened fs, asm now");
			asm(fs, HELLO_WORLD_RES, HELLO_WORLD_PMF);
			System.out.println("finished asm, close now fs");
		}
		System.out.println("execute now the program");
		execute(HELLO_WORLD_PFS, HELLO_WORLD_PMF, 0, EMPTY_BARR, "hello world\n".getBytes(StandardCharsets.UTF_8),
				EMPTY_BARR);
	}
	
	private void execute(String pfsFile, String pmfFile, int exitCode, byte[] stdin, byte[] stdout, byte[] stderr)
			throws IOException, InterruptedException {
		Runtime r       = Runtime.getRuntime();
		String[] args = new String[] { "pvm", "--pfs=" + pfsFile, "--pmf=" + pmfFile };
		System.out.println("args: " + Arrays.toString(args));
		Process process = r.exec(args);
		System.out.println("started process pid: " + process.pid() + "   " + process);
		if (stdin.length > 0) { process.getOutputStream().write(stdin); }
		TwoBools b1 = new TwoBools(), b2 = new TwoBools();
		Thread.startVirtualThread(() -> check(process.getErrorStream(), stderr, b1));
		Thread.startVirtualThread(() -> check(process.getInputStream(), stdout, b2));
		assertEquals(exitCode, process.waitFor());
		checkResult(b1);
		checkResult(b2);
	}
	
	private void checkResult(TwoBools res) throws InterruptedException, CheckerException {
		while (!res.finish) {
			synchronized (res) {
				if (res.finish) { break; }
				res.wait(1000L);
			}
		}
		assertTrue(res.result);
	}
	
	private void check(InputStream stream, byte[] value, TwoBools b) {
		byte[] other = new byte[value.length];
		for (int i = 0; i < value.length;) {
			try {
				int reat = stream.read(value, i, other.length - i);
				if (reat == -1) { throw new IOException("reached EOF too early"); }
				i += reat;
			} catch (IOException e) {
				b.finish = true;
				b.result = false;
				synchronized (b) {
					b.notifyAll();
				}
				throw new IOError(e);
			}
		}
		b.finish = true;
		b.result = Arrays.equals(value, other);
		synchronized (b) {
			b.notifyAll();
		}
	}
	
	private static final class TwoBools {
		volatile boolean finish;
		volatile boolean result;
	}
	
	private void asm(FS fs, String res, String pmFile) throws IOException, AssembleError {
		try (InputStream in = getClass().getResourceAsStream(res)) {
			try (WriteStream stream = (WriteStream) fs.stream(pmFile, CREATE_FILE_OPTS)) {
				PrimitiveAssembler asm = newAsm(stream);
				System.out.println("assemble now " + pmFile);
				asm.assemble(Paths.get(res), in, StandardCharsets.UTF_8);
				System.out.println("assembled successful, flag now as executable");
				try (File file = fs.file(pmFile)) {
					file.flag(FSElement.FLAG_EXECUTABLE, 0);
				}
			}
		}
	}
	
	private static PrimitiveAssembler newAsm(WriteStream stream) {
		return new PrimitiveAssembler(new BufferedOutputStream(stream.asOutputStream()), null, new Path[0], false,
				true);
	}
	
}
