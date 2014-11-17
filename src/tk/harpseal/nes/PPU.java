package tk.harpseal.nes;

public class PPU {
	private NES nes;
	private byte[] mem = new byte[16384];
	private byte[] oam_1 = new byte[256];
	private byte[] oam_2 = new byte[64];
	private byte PPUCTRL = 0b00000000;
	private byte PPUMASK = 0b00000000;	
	private byte PPUSTAT = 0b00100000;	
	// Controls the latch of $2005 and $2006
	private boolean PM2005L = false;
	private short PPUSCRL_X = 0;
	private short PPUSCRL_Y = 0;
	private short OAMADDR = 0;
	
	private short VRAMADDR = 0;
	private short VRAMADDR_T = 0;
	private short X_SCROLL = 0;

	private byte PPUCTRL_T = 0b00000000;
	private byte PPUMASK_T = 0b00000000;	
	private byte PPUSTAT_T = 0b00100000;	
	
	private short SCANLINE = 0;
	private short PXCYCLE = 0;
	private boolean ODD_FRAME = false;
	
	private short BG_SR1 = 0;
	private short BG_SR2 = 0;
	private byte BG_PR1 = 0;
	private byte BG_PR2 = 0;
	
	public boolean VBLANK = false;
	
	private byte[] SPR_BMP = new byte[8];
	private byte[] SPR_ATTR = new byte[8];
	private byte[] SPR_X = new byte[8];
	// 88974 PPU cycles (PPU cycle is ran 3 times for every CPU cycle)
	private int powerup = 88974;
	public PPU(NES n) {
		PXCYCLE = 0;
		ODD_FRAME = false;
		powerup = 88974;
		nes = n;
		for (int i = 0; i < 256; i++) {
			oam_1[i] = (byte) 255;
		}
	}
	public void reset() {
		PXCYCLE = 0;
		ODD_FRAME = false;
		PPUCTRL = 0b00000000;
		PPUMASK = 0b00000000;	
		PPUSTAT &= 0b10000000;	
		PPUSTAT |= 0b00100000;
		PPUSCRL_X = 0;
		for (int i = 0; i < 256; i++) {
			oam_1[i] = (byte) 255;
		}
	}
	public byte fetchData(int i) {
		// TODO
		// read of address $2000 + i
		
		return 0;
	}
	public void runCycle() {
		// Essentially just render pixel here.
		if (PXCYCLE == 341) {
			PXCYCLE = 0;
			SCANLINE++;
		}
		if (SCANLINE == -1) {
			PPUCTRL_T = PPUCTRL;
			PPUMASK_T = PPUMASK;
		}
		if (SCANLINE >= -1 && SCANLINE <= 239) { //render
			
		}
		if (SCANLINE == 240) { //post-render
			// IDLE
		}
		if (SCANLINE > 240) { //vblank
			if (SCANLINE == 240 && PXCYCLE == 1) {
				VBLANK = true;
				if ((PPUCTRL_T & 0b10000000) != 0)
					nes.cpu.S_NMI = true;
			}
			if ((SCANLINE == 261 && nes.tvmode == TVMode.NTSC)||(SCANLINE == 331 && nes.tvmode == TVMode.PAL)){
				SCANLINE = 0;
				PXCYCLE = -1;
				VBLANK = false;
			}
		}
		PXCYCLE++;
	}
	public void setData(int i, byte j) {
		
	}
	// Designed for DMA
	public void setOAMByte(int a, byte b) {
		oam_1[a & 0xFF] = b;
	}
}