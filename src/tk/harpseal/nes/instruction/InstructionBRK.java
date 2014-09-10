package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionBRK extends Instruction {
	public int offset = 0xFFFE;
	protected InstructionBRK(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void runCycle(int cycles) {
		switch (cycles) {
		case 0:
			if (cpu.nmi)
				offset = 0xFFFA;
			cpu.increasePC();
			break;
		case 1:
			cpu.getByte(cpu.PC + 1);
			cpu.PC++;
			cpu.increasePC();
			break;
		case 2:
			cpu.push((byte) (cpu.PC >> 8));
			if (cpu.brk)
				cpu.P.OR((byte) 0x10);
			break;
		case 3:
			cpu.push((byte) (cpu.PC & 0xFF));
			break;
		case 4:
			cpu.push(cpu.P.getAsByte());
			break;
		case 5:
			p = cpu.getByte(offset);
			break;
		case 6:
			p |= (cpu.getByte(offset + 1) << 8);
			cpu.jump((short) p);
		}
	}

}
