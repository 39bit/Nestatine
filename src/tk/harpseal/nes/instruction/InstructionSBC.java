package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionSBC extends InstructionDocumentedRead {

	public InstructionSBC(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		t3 = cpu.getA() - b - cpu.P.invCarry();
		t1 = (byte) (t3 & 0xFF);
		cpu.P.AOR((byte) 0xFE, (byte) ((t3 < 0) ? 1 : 0));
		cpu.P.AOR((byte) 0xBF, (byte) (((t3 - cpu.getA()) < 0) ? 0x40 : 0));
		cpu.setA(t1);
	}
}
