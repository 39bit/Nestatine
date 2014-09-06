package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionINC extends Instruction {

	public InstructionINC(CPU c, AddressingMode m) {
		super(c, m);
	}
	public static byte OP(CPU c, byte b) {
		return (byte) ((b + 1) & 0xFF);
	}
	@Override
	public void runCycle(int cycles) {
		if (mode == AddressingMode.ZEROPAGE) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				t1 = cpu.getByte(p);
				break;
			case 3:
				cpu.setByte(p, t1);
				t1 = OP(cpu,t1);
				break;
			case 4:
				cpu.setByte(p, t1);
				cpu.flag(t1);
			}
		}
		if (mode == AddressingMode.ZEROPAGE_X) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				p = (p + cpu.getX()) & 0xFF;
				break;
			case 3:
				t1 = cpu.getByte(p);
				break;
			case 4:
				cpu.setByte(p, t1);
				t1 = OP(cpu,t1);
				break;
			case 5:
				cpu.setByte(p, t1);
				cpu.flag(t1);
			}
		}
		if (mode == AddressingMode.ABSOLUTE) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				p |= cpu.getByte(cpu.PC + 2) << 8;
				break;
			case 3:
				t1 = cpu.getByte(p);
				break;
			case 4:
				cpu.setByte(p, t1);
				t1 = OP(cpu,t1);
				break;
			case 5:
				cpu.setByte(p, t1);
				cpu.flag(t1);
			}
		}
		if (mode == AddressingMode.ABSOLUTE_X) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				p = cpu.getByte(cpu.PC + 1);
				break;
			case 2:
				p |= cpu.getByte(cpu.PC + 2) << 8;
				t3 = (p & 0xFF) + cpu.getX();
				break;
			case 3:
				t1 = cpu.getByte(p);
				t2 = (byte) ((p >> 8) & 0xFF);
				t2 = (byte) ((t2 + (t3 >> 8)) & 0xFF);
				p = (t2 << 8) | (t3 & 0xFF);
				break;
			case 4:
				t1 = cpu.getByte(p);
				break;
			case 5:
				cpu.setByte(p, t1);
				t1 = OP(cpu,t1);
				break;
			case 6:
				cpu.setByte(p, t1);
				cpu.flag(t1);
			}
		}
	}
}
