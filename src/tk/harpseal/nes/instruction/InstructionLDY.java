package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionLDY extends InstructionDocumentedRead {

	public InstructionLDY(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		cpu.setY(b, true);
	}

}
