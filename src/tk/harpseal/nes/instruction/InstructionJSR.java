package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionJSR extends Instruction {

	protected InstructionJSR(CPU c, AddressingMode m) {
		super(c, m);
	}

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
			// Nobody knows what happens here!
			break;
		case 3:
			cpu.push((byte) (cpu.PC >> 8));
			break;
		case 4:
			cpu.push((byte) (cpu.PC & 0xFF));
			break;
		case 5:
			p |= (cpu.getByte(cpu.PC) << 8);
			cpu.jump((short) p);
		}
	}

}
