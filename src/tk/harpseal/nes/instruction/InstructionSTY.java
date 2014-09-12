package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionSTY extends Instruction {

	public InstructionSTY(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		if (mode == AddressingMode.ZEROPAGE) {
			switch (cycles) {
			case 0:
				cpu.increasePC();
				break;
			case 1:
				p = cpu.getByte(cpu.PC);
				cpu.increasePC();
				break;
			case 2:
				cpu.setByte(p, cpu.getY());
			}
		}
		if (mode == AddressingMode.ZEROPAGE_X) {
			switch (cycles) {
			case 0:
				cpu.increasePC();
				break;
			case 1:
				p = cpu.getByte(cpu.PC);
				cpu.increasePC();
				break;
			case 2:
				cpu.getByte(p);
				p = ((p + cpu.getX()) & 0xFF);
				break;
			case 3:
				cpu.setByte(p, cpu.getY());
			}
		}
		if (mode == AddressingMode.ABSOLUTE) {
			switch (cycles) {
			case 0:
				cpu.increasePC();
				break;
			case 1:
				p = cpu.getByte(cpu.PC);
				cpu.increasePC();
				break;
			case 2:
				p |= (cpu.getByte(cpu.PC) << 8);
				cpu.increasePC();
				break;
			case 3:
				cpu.setByte(p, cpu.getY());
			}
		}
	}
}
