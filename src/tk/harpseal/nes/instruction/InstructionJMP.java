package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionJMP extends Instruction {

	protected InstructionJMP(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
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
				cpu.jump((short) p);
				break;
			}
		}
		if (mode == AddressingMode.INDIRECT) {
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
				t3 = cpu.getByte(p);
				break;
			case 4:
				t3 |= (cpu.getByte(IND_BUG(p)) << 8);
				cpu.jump((short) t3);
				break;
			}
		}
	}

	private int IND_BUG(int p) {
		// emulate the indirect JMP bug
		byte f = (byte) (p >> 8);
		byte g = (byte) ((p & 0xFF) + 1);
		return (f << 8) | g;
	}

}
