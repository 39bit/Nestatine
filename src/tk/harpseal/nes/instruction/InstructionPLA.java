package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionPLA extends Instruction {

	protected InstructionPLA(CPU c, AddressingMode m) {
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
			cpu.S = (byte) ((cpu.S + 1) & 0xFF);
			break;
		case 3:
			cpu.setA(cpu.getByte(0x100 + cpu.S));
		}
	}

}
