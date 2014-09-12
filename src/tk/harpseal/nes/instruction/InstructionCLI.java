package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionCLI extends Instruction {

	public InstructionCLI(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.P.AND((byte) 0xFB);
		}
	}
}
