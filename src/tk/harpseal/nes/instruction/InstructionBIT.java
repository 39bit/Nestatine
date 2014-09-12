package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBIT extends InstructionDocumentedRead {

	public InstructionBIT(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		byte v = (byte) (cpu.getA() & b);
		if (v == 0) cpu.P.isZero(); else cpu.P.notZero();
		cpu.P.AND((byte) 0x3F);
		cpu.P.OR((byte) (0xC0 & b)); 
	}

}
