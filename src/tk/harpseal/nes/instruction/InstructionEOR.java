package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionEOR extends InstructionDocumentedRead {

	public InstructionEOR(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		cpu.setA(cpu.getA() ^ b);
	}

}
