package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBEQ extends InstructionBranch {

	public InstructionBEQ(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public boolean takeBranch() {
		return cpu.P.getBit(1);
	}

}
