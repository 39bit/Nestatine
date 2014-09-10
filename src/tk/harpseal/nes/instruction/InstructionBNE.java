package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBNE extends InstructionBranch {

	public InstructionBNE(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public boolean takeBranch() {
		return !cpu.P.getBit(1);
	}

}
