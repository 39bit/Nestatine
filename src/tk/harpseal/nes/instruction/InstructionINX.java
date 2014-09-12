package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionINX extends Instruction {

	public InstructionINX(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.setX((byte) ((cpu.getX() + 1) & 0xFF), true);
		}
	}
}
