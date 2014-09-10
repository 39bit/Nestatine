package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBMI extends InstructionBranch {

	public InstructionBMI(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public boolean takeBranch() {
		return cpu.P.getBit(7);
	}

}
