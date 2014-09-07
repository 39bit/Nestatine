package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionRTS extends Instruction {
	protected InstructionRTS(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			break;
		case 1:
			cpu.getByte(cpu.PC + 1);
			break;
		case 2:
			cpu.S = (byte) (cpu.S + 1);
			break;
		case 3:
			p = cpu.getByte(cpu.S + 0x100);
			cpu.S = (byte) (cpu.S + 1);
			break;
		case 4:
			p |= (cpu.getByte(cpu.S + 0x100) << 8);
			break;
		case 5:
			p = (short) (p + 1);
			cpu.jump((short) p);
		}
	}

}
