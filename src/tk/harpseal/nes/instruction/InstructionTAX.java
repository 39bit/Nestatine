package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionTAX extends Instruction {

	public InstructionTAX(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.setX(cpu.getA(), true);
		}
	}
}
