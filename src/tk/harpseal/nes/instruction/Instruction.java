package tk.harpseal.nes.instruction;

import tk.harpseal.nes.CPU;

public abstract class Instruction {
	public CPU cpu;							// CPU
	public AddressingMode mode;				// addressing mode
	public int p = 0;						// parameter (fetched by instruction)
	protected Instruction(CPU c, AddressingMode m) {
		cpu = c;
		mode = m;
		p = 0;
	}
	public abstract void runCycle(int cycles);
	
	public static Instruction getInstructionFromId(CPU cpu, byte i) {
		return null;
	}
}
