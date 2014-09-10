package tk.harpseal.nes;

import tk.harpseal.nes.instruction.Instruction;

public class CPU {
	// Cycles per instruction
	private final byte[] CPI = {7,6,1,8,3,3,5,5,3,2,2,2,4,4,6,6,3,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7,6,6,1,8,3,3,5,5,4,2,2,2,4,4,6,6,2,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7,6,6,1,8,3,3,5,5,3,2,2,2,3,4,6,6,3,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7,6,6,1,8,3,3,5,5,4,2,2,2,5,4,6,6,2,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7,2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,3,6,1,6,4,4,4,4,2,5,2,5,5,5,5,5,2,6,2,6,3,3,3,3,2,2,2,2,4,4,4,4,2,5,1,5,4,4,4,4,2,4,2,4,4,4,4,4,2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,3,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7,2,6,2,8,3,3,5,5,2,2,2,2,4,4,6,6,2,5,1,8,4,4,6,6,2,4,2,7,4,4,7,7};
	
	private final Instruction[] set = new Instruction[256];
	
	private byte[] mem = new byte[16416];
	
	public short PC = 0;
	private byte A = 0;
	private byte X = 0;
	private byte Y = 0;
	public byte S = 0;
	public CPUFlags P = new CPUFlags();
	
	private boolean APUlatch = true;
	
	// How many cycles left for this instruction
	private int cycles = 0;
	private int cycleid = 0;
	// Current instruction
	private byte instr = 0;
	
	// Force next instruction to be BRK? used for interrupts
	private boolean interrupting = false;
	// Interrupt is NMI instead of IRQ (jump from $FFFA not $FFFE)
	public boolean nmi = false;
	// Interrupt IRQ is BRK?
	public boolean brk = false;
	
	// Doing!
	
	public boolean S_NMI = false;
	public boolean S_RST = false;
	public boolean S_IRQ = false;
	
	private int powerupcycles = 10;
	
	private NES nes;
	
	public CPU(NES n) {
		nes = n;
		for (int i = 0; i < 256; i++) {
			set[i] = Instruction.getInstructionFromId(this, (byte) i);
		}
		for (int i = 0x0000; i < 0x8000; i++) {
			mem[i] = (byte) 0xFF;
		}
		P = new CPUFlags();
		powerupcycles = 10;
	}
	public byte getA() {
		return A;
	}
	public byte getX() {
		return X;
	}
	public byte getY() {
		return Y;
	}
	public void jump(short addr) {
		PC = addr;
	}
	public void setA(byte b) {
		A = b;
		flag(A);
	}
	public void setA(int i) {
		setA((byte) (i & 0xFF));
	}
	public void setX(byte b) {
		X = b;
	}
	public void setY(byte b) {
		Y = b;
	}
	public void flag(byte a2) {
		P.AND((byte) 0b01111101);
		if (a2 == 0) P.OR((byte) 0x02); 
		if ((a2 < 0) || (a2 >= 128)) P.OR((byte) 0x80); 
	}
	public void compare(byte a1, byte a2) {
		P.AND((byte) 0b01111100);
		byte b = (byte) ((byte) a1 + a2);
		if (b == 0) P.OR((byte) 0x02); 
		if (b < 0) P.OR((byte) 0x80); 
		if (Math.abs(a2) > Math.abs(a1)) P.OR((byte) 0x01); 
	}
	
	public void runCycle() {
		if (powerupcycles > 0) {
			switch (powerupcycles) {
			case 10:
				S = 0x00;
				break;
			case 7:
				getByte(0x100 + S);
				S = (byte) (S - 1);
				break;
			case 6:
				getByte(0x100 + S);
				S = (byte) (S - 1);
				break;
			case 5:
				getByte(0x100 + S);
				S = (byte) (S - 1);
				break;
			case 4:
				PC = getByte(0xFFFC);
				break;
			case 3:
				PC |= (getByte(0xFFFD) << 8);
				// Then we wait
			}
			powerupcycles--;
			return;
		}
		runInstructionCycle();
		if (APUlatch) nes.apu.runCycle();
		APUlatch = !APUlatch;
		if (cycles == 0) {
			checkInterrupts();
			runInstructionAtPC();
			runInstructionCycle();
		} else {
			cycles--;
		}
	}
	public void runInstructionAtPC() {
		byte i = mem[PC];
		brk = (i == 0);
		if (interrupting) {
			i = 0;
			interrupting = false;
		}
		runInstruction(i);
	}
	private void checkInterrupts() {
		if (S_RST) { reset(); return; }
		if (S_NMI) { callNMI(); return; }
		if (S_IRQ && P.enabledIRQ()) { callIRQ(); return; }
	}
	public void reset() {
		S_NMI = false;
		S_IRQ = false;
		setByte(0x4015, (byte) 0);
		jump(getShort(0xFFFC));
		powerupcycles = 10;
	}
	public byte getByte(int i) {
		i = i & 0xFFFF;
		if (i >= 0x800 && i <= 0x1FFF) {
			while (i >= 0x800) {
				i -= 0x800;
			}
		}
		if (i >= 0x2000 && i <= 0x3FFF) {
			i = 0x2000 + ((i - 0x2000) % 8);
		}
		if (i >= 0x2000 && i <= 0x2007) {
			return nes.ppu.fetchData(i - 0x2000);
		}
		if (i >= 0x4016 && i <= 0x4017) {
			return nes.fetchControllerInput(i - 0x4016);
		}
		return mem[i];
	}
	public void setByte(int i, byte j) {
		if (i >= 0x800 && i <= 0x1FFF) {
			while (i >= 0x800) {
				i -= 0x800;
			}
		}
		if (i >= 0x2000 && i <= 0x3FFF) {
			i = 0x2000 + ((i - 0x2000) % 8);
		}
		mem[i] = j;
	}
	public void increasePC() {
		PC = (short) (PC + 1);
	}
	public void addCycle() {
		cycles++;
	}
	private void callNMI() {
		nmi = true;
		brk = false;
		interrupting = true;
	}
	private void callIRQ() {
		nmi = false;
		brk = false;
		interrupting = true;
	}
	public short getShort(int addr) {
		return (short) (getByte(addr) | (getByte(addr + 1) << 8));
	}
	/*private byte ASL(byte b) {
		byte v = (byte) (((b << 1) & 0xFF) | P.getCarry());
		P.setCarry((b & 0x80) != 0);
		return v;
	}
	private byte LSR(byte b) {
		byte v = (byte) (((b >> 1) & 0xFF) | (P.getCarry() << 7));
		P.setCarry((b & 0x01) != 0);
		return v;
	}*/
	public void push(byte d) {
		setByte(0x100 + S, d);
		S = (byte) ((S - 1) & 0xFF);
	}
	private void runInstruction(byte i) {
		instr = i;
		cycleid = 0;
		cycles = CPI[i] - 1;
	}
	private void runInstructionCycle() {
		// 6502 bugs/quirks are INTENTIONALLY included
		// - RMW instructions write twice: original then modified
		// - JMP indirect bug present
		set[instr].runCycle(cycleid++);
	}
}
