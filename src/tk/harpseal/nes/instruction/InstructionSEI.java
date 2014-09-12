package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionSEI extends Instruction {

	public InstructionSEI(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.P.OR((byte) 0x04);
		}
	}
}
