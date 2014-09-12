package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionCPY extends InstructionDocumentedRead {

	public InstructionCPY(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		t3 = cpu.getY() - b - cpu.P.invCarry();
		t1 = (byte) (t3 & 0xFF);
		cpu.flag(t1);
		cpu.P.AOR((byte) 0xFE, (byte) ((t3 < 0) ? 1 : 0));
	}
}
