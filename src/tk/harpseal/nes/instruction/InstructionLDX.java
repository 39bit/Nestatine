package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionLDX extends InstructionDocumentedRead {

	public InstructionLDX(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		cpu.setX(b, true);
	}

}
