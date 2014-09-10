package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBPL extends InstructionBranch {

	public InstructionBPL(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public boolean takeBranch() {
		return !cpu.P.getBit(7);
	}

}
