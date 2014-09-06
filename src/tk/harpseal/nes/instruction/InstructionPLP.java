package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionPLP extends Instruction {

	protected InstructionPLP(CPU c, AddressingMode m) {
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
			cpu.S = (byte) ((cpu.S + 1) & 0xFF);
			break;
		case 3:
			cpu.P.fromByte(cpu.getByte(0x100 + cpu.S));
		}
	}

}
