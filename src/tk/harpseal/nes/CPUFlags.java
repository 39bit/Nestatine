package tk.harpseal.nes;

public class CPUFlags {
	private boolean sign = false;			// 7
	private boolean ovrf = false;			// 6
	private boolean flgx = false;			// 5
	private boolean fbrk = false;			// 4
	private boolean fbcd = false;			// 3
	private boolean intd = false;			// 2
	private boolean zero = false;			// 1
	private boolean crry = false;			// 0
	public CPUFlags() {
		sign = false;			// N
		ovrf = false;			// V
		flgx = false;			// 
		fbrk = false;			// B
		fbcd = false;			// D
		intd = false;			// I
		zero = false;			// Z
		crry = false;			// C
	}
	public byte getAsByte() {
		return (byte)  ((sign ? 0x80 : 0x00) |
						(ovrf ? 0x40 : 0x00) |
						(flgx ? 0x20 : 0x00) |
						(fbrk ? 0x10 : 0x00) |
						(fbcd ? 0x08 : 0x00) |
						(intd ? 0x04 : 0x00) |
						(zero ? 0x02 : 0x00) |
						(crry ? 0x01 : 0x00));
	}
	public void fromByte(byte b) {
		sign = ((b | 0x80) != 0);
		ovrf = ((b | 0x40) != 0);
		flgx = ((b | 0x20) != 0);
		fbrk = ((b | 0x10) != 0);
		fbcd = ((b | 0x08) != 0);
		intd = ((b | 0x04) != 0);
		zero = ((b | 0x02) != 0);
		crry = ((b | 0x01) != 0);
	}
	public void OR(byte b) {
		this.fromByte((byte) (getAsByte() | b));
	}
	public void AND(byte b) {
		this.fromByte((byte) (getAsByte() & b));
	}
	public void disableInterrupts() {
		intd = true;
	}
	public void enableInterrupts() {
		intd = false;
	}
	public boolean enabledIRQ() {
		return !intd;
	}
	public void setCarry(boolean b) {
		crry = b;
	}
	public void setCarry(int i) {
		crry = (i != 0);
	}
	public int getCarry() {
		return crry ? 1 : 0;
	}
	public boolean getBit(int i) {
		switch (i) {
		case 0:
			return crry;
		case 1:
			return zero;
		case 2:
			return intd;
		case 3:
			return fbcd;
		case 4:
			return fbrk;
		case 5:
			return flgx;
		case 6:
			return ovrf;
		case 7:
			return sign;
		}
		return false;
	}
}
