package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public abstract class Instruction {
	public CPU cpu;							// CPU
	public AddressingMode mode;				// addressing mode
	public int p = 0;						// parameter (fetched by instruction)
	public byte t1 = 0;						// temp 1
	public byte t2 = 0;						// temp 2
	public byte t3 = 0;						// temp 3
	protected Instruction(CPU c, AddressingMode m) {
		cpu = c;
		mode = m;
		p = 0;
	}
	public abstract void runCycle(int cycles);
	
	public static Instruction getInstructionFromId(CPU cpu, byte i) {
		return null;
	}
	public int indX(int zp) {
		return cpu.getByte(cpu.getShort(zp + cpu.getX()));
	}
	public int indY(int zp) {
		return cpu.getByte((cpu.getShort(zp) + cpu.getY()));
	}
}
