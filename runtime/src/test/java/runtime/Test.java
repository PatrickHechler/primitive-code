//package runtime;
//
//import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.PVM;
//import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMImpl;
//
//public class Test {
//	
//	public static void main(String[] args) throws InterruptedException {
//		PVM pvm = new PVMImpl(5, 2);
//		System.out.println("J-LOG: created pvm(5,2)");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		try {
//			System.out.println("J-LOG: pvm.get(7) -> " + pvm.get(7) + " <-");
//		} catch (IndexOutOfBoundsException ioobe) {
//			ioobe.printStackTrace();
//		}
//		try {
//			System.out.println("J-LOG: pvm.get(0x0160004130007105l) -> " + pvm.get(0x0160004130007105l) + " <-");
//		} catch (IndexOutOfBoundsException ioobe) {
//			ioobe.printStackTrace();
//		}
//		System.out.println("J-log: pvm.set(0,1) ->");
//		pvm.set(0, 1);
//		System.out.println("J-log: pvm.set(0,1) <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(1) -> " + pvm.get(1) + " <-");
//		System.out.println("J-LOG: pvm.get(2) -> " + pvm.get(2) + " <-");
//		System.out.println("J-log: pvm.set(2,681) ->");
//		pvm.set(2, 681);
//		System.out.println("J-log: pvm.set(2,681) <-");
//		System.out.println("J-LOG: pvm.get(0) -> " + pvm.get(0) + " <-");
//		System.out.println("J-LOG: pvm.get(1) -> " + pvm.get(1) + " <-");
//		System.out.println("J-LOG: pvm.get(2) -> " + pvm.get(2) + " <-");
//		System.out.println("J-LOG: pvm.getStackSize() -> " + pvm.getStackSize() + " <-");
//		System.out.println("J-LOG: pvm.getStackMaxSize() -> " + pvm.getStackMaxSize() + " <-");
//		System.out.println("J-log: pvm.push(2) ->");
//		pvm.push(2);
//		System.out.println("J-log: pvm.push(2) <-");
//		System.out.println("J-LOG: pvm.getStackMaxSize() -> " + pvm.getStackMaxSize() + " <-");
//		System.out.println("J-LOG: pvm.getStackSize() -> " + pvm.getStackSize() + " <-");
//		System.out.println("J-log: pvm.push(4) ->");
//		pvm.push(4);
//		System.out.println("J-log: pvm.push(4) <-");
//		System.out.println("J-LOG: pvm.getStackMaxSize() -> " + pvm.getStackMaxSize() + " <-");
//		System.out.println("J-LOG: pvm.getStackSize() -> " + pvm.getStackSize() + " <-");
//		System.out.println("J-LOG: pvm.pop() -> " + pvm.pop() + " <-");
//		System.out.println("J-LOG: pvm.getStackSize() -> " + pvm.getStackSize() + " <-");
//		System.out.println("J-LOG: pvm.pop() -> " + pvm.pop() + " <-");
//		System.out.println("J-LOG: pvm.getRegCount() -> " + pvm.getRegCount() + " <-");
//		System.out.println("J-LOG: pvm.getStackSize() -> " + pvm.getStackSize() + " <-");
//		System.out.println("J-LOG: pvm.getStackMaxSize() -> " + pvm.getStackMaxSize() + " <-");
//		String file = "./src/test/resources/test.pbc";
//		System.out.println("J-log: pvm.read('" + file + "') ->");
//		pvm.read(file);
//		System.out.println("J-log: pvm.read('" + file + "') <-");
//		System.out.println("J-LOG: pvm.execute() -> " + pvm.execute() + " <-");
//		file = "./src/test/resources/test-1.pbc";
//		System.out.println("J-log: pvm.read('" + file + "') ->");
//		pvm.read(file);
//		System.out.println("J-log: pvm.read('" + file + "') <-");
//		System.out.println("J-LOG: pvm.execute() -> " + pvm.execute() + " <-");
//		System.out.println("J-LOG: FINISH");
//	}
//	
//}
