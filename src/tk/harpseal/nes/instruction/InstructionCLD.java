package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionCLD extends Instruction {

	public InstructionCLD(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.P.AND((byte) 0xF7);
		}
	}
}
