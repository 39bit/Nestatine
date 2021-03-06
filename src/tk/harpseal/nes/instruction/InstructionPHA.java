package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionPHA extends Instruction {

	protected InstructionPHA(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			cpu.getByte(cpu.PC + 1);
			break;
		case 2:
			cpu.push(cpu.getA());
		}
	}

}
