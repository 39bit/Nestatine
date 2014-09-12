package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public class InstructionADC extends InstructionDocumentedRead {

	public InstructionADC(CPU c, AddressingMode m) {
		super(c, m);
	}

	@Override
	public void applyData(byte b) {
		t3 = cpu.getA() + b + cpu.P.getCarry();
		t1 = (byte) (t3 & 0xFF);
		cpu.P.AOR((byte) 0xFE, (byte) ((t3 > 255) ? 1 : 0));
		cpu.P.AOR((byte) 0xBF, (byte) (((uint_to_sint8(t3) - cpu.getA()) < 0) ? 0x40 : 0));
		cpu.setA(t1);
	}

	// Unsigned int to signed 8-bit integer
	private int uint_to_sint8(int t3) {
		int h = (t3 & 0xFF);
		if (h > 127) {
			return -(255 - (h - 1));
		}
		return h;
	}
}
