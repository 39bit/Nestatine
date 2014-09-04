package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionASL extends Instruction {

	public InstructionASL(CPU c, AddressingMode m) {
		super(c, m);
	}
	private byte ASL(byte b) {
		byte v = (byte) (((b << 1) & 0xFF) | cpu.P.getCarry());
		cpu.P.setCarry((b & 0x80) != 0);
		return v;
	}
	@Override
	public void runCycle(int cycles) {
		if (mode == AddressingMode.IMPL_OR_A) {
			switch (cycles) {
			case 1:
				cpu.setA(ASL(cpu.getA()));
				break;
			case 0:
				cpu.getByte(cpu.PC + 1);
				break;
			}
		}
		// TODO rest of modes
	}

}
