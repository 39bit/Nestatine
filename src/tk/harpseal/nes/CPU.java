package tk.harpseal.nes;

public class CPU {
	// Bytes per instruction
	private final byte[] BPI = {1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,3,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,2,2,2,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3};
	// Cycles per instruction
	private final byte[] CPI = {1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,3,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,2,2,2,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3,1,2,1,2,2,2,2,2,1,2,1,2,3,3,3,3,2,2,1,2,2,2,2,2,1,3,1,3,3,3,3,3};
	// How many nanoseconds in between cycles in NTSC & PAL
	private final double CPU_NTSC_NS = 55.873D;
	private final double CPU_PAL_NS = 60.147D;
	
	private byte[] mem = new byte[16416];
	
	private int PC = 0;
	private byte A = 0;
	private byte X = 0;
	private byte Y = 0;
	private byte S = (byte) 0xFD;
	private CPUFlags P = new CPUFlags();
	
	private int P_NMI = 0;
	private int P_RST = 0;
	private int P_IRQ = 0;
	
	// How many cycles left for this instruction
	private int cycles = 0;
	
	public boolean S_NMI = false;
	public boolean S_RST = false;
	public boolean S_IRQ = false;
	
	private NES nes;
	
	public CPU(NES n) {
		nes = n;
		for (int i = 0x0000; i < 0x8000; i++) {
			mem[i] = (byte) 0xFF;
		}
		P = new CPUFlags();
		
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
	private void flag(byte a2) {
		P.AND((byte) 0b01111101);
		if (a2 == 0) P.OR((byte) 0x02); 
		if (a2 < 0) P.OR((byte) 0x80); 
	}
	private void compare(byte a1, byte a2) {
		P.AND((byte) 0b01111100);
		byte b = (byte) ((byte) a1 + a2);
		if (b == 0) P.OR((byte) 0x02); 
		if (b < 0) P.OR((byte) 0x80); 
		if (Math.abs(a2) > Math.abs(a1)) P.OR((byte) 0x01); 
	}
	
	public void runCycle() {
		if (cycles == 0) {
			runInstructionAtPC();
		} else if (cycles == 1) {
			checkInterrupts();
		} else {
			cycles--;
		}
	}
	public void runInstructionAtPC() {
		byte i = mem[PC];
		int p = 0;
		if (BPI[i] > 1)
			p = mem[PC + 1];
		if (BPI[i] > 2)
			p = p | (mem[PC + 2] << 8);
		runInstruction(i,p);
		PC += BPI[i];
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
	}
	private byte getByte(int i) {
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
		if (i >= 0x4000 && i <= 0x4015) {
			updateAPU();
		}
		if (i >= 0x4016 && i <= 0x4017) {
			return nes.fetchControllerInput(i - 0x4016);
		}
		return mem[i];
	}
	private void setByte(int i, byte j) {
		if (i >= 0x800 && i <= 0x1FFF) {
			while (i >= 0x800) {
				i -= 0x800;
			}
		}
		if (i >= 0x2000 && i <= 0x3FFF) {
			i = 0x2000 + ((i - 0x2000) % 8);
		}
		mem[i] = j;
		if (i >= 0x2000 && i <= 0x2007) {
			nes.ppu.updatePPU();
		}
		if (i >= 0x4000 && i <= 0x4015) {
			updateAPU();
		}
	}
	private void updateAPU() {
		// TODO
	}
	private void pushInterruptData() {
		// TODO
	}
	private void callNMI() {
		pushInterruptData();
		PC = getShort(0xFFFA);
	}
	private void callIRQ() {
		pushInterruptData();
		PC = getShort(0xFFFE);
	}
	private int indX(int zp) {
		return getByte(getShort(zp + X));
	}
	private int indY(int zp) {
		return getByte((getShort(zp) + Y));
	}
	private short getShort(int addr) {
		return (short) (getByte(addr) | (getByte(addr + 1) << 8));
	}
	private byte ASL(byte b) {
		byte v = (byte) (((b << 1) & 0xFF) | P.getCarry());
		P.setCarry((b & 0x80) != 0);
		return v;
	}
	private byte LSR(byte b) {
		byte v = (byte) (((b >> 1) & 0xFF) | (P.getCarry() << 7));
		P.setCarry((b & 0x01) != 0);
		return v;
	}
	private void push(byte d) {
		setByte(0x100 + S, d);
		S = (byte) ((S - 1) & 0xFF);
	}
	private byte pull() {
		S = (byte) ((S + 1) & 0xFF);
		return getByte(0x100 + S);
	}
	private void runInstruction(byte i, int p) {
		switch (i) {
		case 0x00: // BRK
			// TODO	
			break;
		case 0x01: // ORA X,ind
			setA(A | getByte(indX(p)));
			break;
		case 0x05: // ORA zpg
			setA(A | getByte(p));
			break;
		case 0x06: // ASL zpg
			byte B = getByte(p);
			// RMW instructions write twice: original then modified
			setByte(p, B); 
			setByte(p, ASL(B));
			break;
		case 0x08: // PHP impl
			push(P.getAsByte());
			break;
		case 0x09: // ORA #
			setA(A | p);
			break;
		case 0x0A: // ASL A
			setA(ASL(A));
			break;
		// Below this line only undocumented opcodes
		}
		cycles = CPI[i] - 1;
	}
}
