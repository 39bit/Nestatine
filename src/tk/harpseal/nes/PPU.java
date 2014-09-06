package tk.harpseal.nes;

public class PPU {
	private NES nes;
	private byte[] mem = new byte[16384];
	private byte[] oam = new byte[256];
	private byte PPUCTRL = 0b00000000;
	private byte PPUMASK = 0b00000000;	
	private byte PPUSTAT = 0b00100000;	
	// Controls the latch of $2005 and $2006
	private boolean PM2005L = false;
	private short PPUSCRL = 0;
	private short OAMADDR = 0;
	// 88974 PPU cycles (PPU cycle is ran 3 times for every CPU cycle)
	private int powerup = 88974;
	public PPU(NES n) {
		powerup = 88974;
		nes = n;
		for (int i = 0; i < 256; i++) {
			oam[i] = 0;
		}
		for (int i = 0; i < 256; i++) {
			oam[i] = 0;
		}
	}
	public void reset() {
		PPUCTRL = 0b00000000;
		PPUMASK = 0b00000000;	
		PPUSTAT &= 0b10000000;	
		PPUSTAT |= 0b00100000;
		PPUSCRL = 0;
		for (int i = 0; i < 256; i++) {
			oam[i] = 0;
		}
	}
	public byte fetchData(int i) {
		// TODO
		// read of address $2000 + i
		return 0;
	}
	public void runCycle() {
		
	}
}