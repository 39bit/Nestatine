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
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				p |= (cpu.getByte(cpu.PC + 2) << 8);
				cpu.jump((short) p);
				break;
			}
		}
		if (mode == AddressingMode.INDIRECT) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				p |= (cpu.getByte(cpu.PC + 2) << 8);
				break;
			case 3:
				t3 = cpu.getByte(p);
				break;
			case 4:
				t3 |= (cpu.getByte(p + 1) << 8);
				cpu.jump((short) t3);
				break;
			}
		}
	}

}
