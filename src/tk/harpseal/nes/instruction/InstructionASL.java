package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionASL extends Instruction {

	public InstructionASL(CPU c, AddressingMode m) {
		super(c, m);
	}
	public static byte ASL(CPU c, byte b, boolean rotate) {
		byte v = (byte) (((b << 1) & 0xFF) | (rotate ? c.P.getCarry() : 0));
		c.P.setCarry((b & 0x80) != 0);
		return v;
	}
	@Override
	public void runCycle(int cycles) {
		if (mode == AddressingMode.IMPL_OR_A) {
			switch (cycles) {
			case 0:
				break;
			case 1:
				cpu.setA(ASL(cpu,cpu.getA(),false));
				cpu.getByte(cpu.PC + 1);
				break;
			}
		}
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
				t1 = ASL(cpu,t1,false);
				break;
			case 4:
				cpu.setByte(p, t1);
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
				t1 = ASL(cpu,t1,false);
				break;
			case 5:
				cpu.setByte(p, t1);
			}
		}
	}

}
