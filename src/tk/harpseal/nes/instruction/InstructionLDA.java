package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionLDA extends InstructionDocumentedRead {

	public InstructionLDA(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		cpu.setA(b);
	}

}
