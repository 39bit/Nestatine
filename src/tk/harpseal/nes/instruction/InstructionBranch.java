package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public abstract class InstructionBranch extends Instruction {

	public InstructionBranch(CPU c, AddressingMode m) {
		super(c, m);
	}
	public abstract boolean takeBranch();

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			cpu.increasePC();
			break;
		case 1:
			p = cpu.getByte(cpu.PC);
			cpu.increasePC();
			break;
		case 2:
			cpu.getByte(cpu.PC);
			if (takeBranch()) {
				cpu.addCycle();
				int h = p;
				if (p >= 128)
					h = -((255 - p) + 1);
				t3 = (cpu.PC + h) & 0xFFFF;
				cpu.jump((short) ((cpu.PC & 0xFF00) | (t3 & 0x00FF)));
			} else {
				cpu.increasePC();
			}
			break;
		case 3:
			cpu.getByte(cpu.PC);
			if ((t3 >> 8) != (cpu.PC >> 8)) {
				cpu.addCycle();
				cpu.jump((short) t3);
			} else {
				cpu.increasePC();
			}
			break;
		case 4:
			cpu.getByte(cpu.PC);
			cpu.increasePC();
			break;
		}
	}

}
