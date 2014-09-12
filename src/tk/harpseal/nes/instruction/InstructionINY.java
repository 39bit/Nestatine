package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionINY extends Instruction {

	public InstructionINY(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.setY((byte) ((cpu.getY() + 1) & 0xFF), true);
		}
	}
}
