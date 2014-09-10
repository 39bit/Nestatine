package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBVC extends InstructionBranch {

	public InstructionBVC(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public boolean takeBranch() {
		return !cpu.P.getBit(6);
	}

}
