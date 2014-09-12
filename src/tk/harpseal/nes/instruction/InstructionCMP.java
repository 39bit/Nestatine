package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionCMP extends InstructionDocumentedRead {

	public InstructionCMP(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		t3 = cpu.getA() - b - cpu.P.invCarry();
		t1 = (byte) (t3 & 0xFF);
		cpu.flag(t1);
		cpu.P.AOR((byte) 0xFE, (byte) ((t3 < 0) ? 1 : 0));
	}
}
