package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionNOP extends Instruction {

	protected InstructionNOP(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		if (cycles == 0)
			cpu.increasePC();
		// This is a NOP/NOOP, what am I supposed to do?
	}

}
