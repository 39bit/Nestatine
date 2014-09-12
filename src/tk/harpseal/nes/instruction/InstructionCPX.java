package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionCPX extends InstructionDocumentedRead {

	public InstructionCPX(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		t3 = cpu.getX() - b - cpu.P.invCarry();
		t1 = (byte) (t3 & 0xFF);
		cpu.flag(t1);
		cpu.P.AOR((byte) 0xFE, (byte) ((t3 < 0) ? 1 : 0));
	}
}
