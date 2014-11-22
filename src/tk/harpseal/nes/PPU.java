package tk.harpseal.nes;

public class PPU {
	private NES nes;
	private byte[] mem = new byte[16384];
	private byte[] oam_1 = new byte[256];
	private byte[] oam_2 = new byte[64];
	private byte PPUCTRL = 0b00000000;
	private byte PPUMASK = 0b00000000;	
	// Controls the latch of $2005 and $2006
	private boolean PM2005L = false;
	private short PPUSCRL_X = 0;
	private short PPUSCRL_Y = 0;
	private byte OAMADDR = 0;
	
	private short VRAMADDR = 0;
	private short VRAMADDR_T = 0;
	private short X_SCROLL = 0;
	
	private short SCANLINE = 0;
	private short PXCYCLE = 0;
	private boolean ODD_FRAME = false;
	
	private short BG_SR1 = 0;
	private short BG_SR2 = 0;
	private byte BG_PR1 = 0;
	private byte BG_PR2 = 0;
	
	private byte LAST_WRITE = 0;
	
	public boolean NMI_OCCURRED = false;
	public boolean NMI_OUTPUT = false;
	public boolean T_NMI = false;
	
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
		PPUSCRL_X = 0;
		for (int i = 0; i < 256; i++) {
			oam_1[i] = (byte) 255;
		}
	}
	public byte fetchData(int i) {
		// TODO
		// read of address $2000 + i
		if (i == 0) return PPUCTRL;
		if (i == 1) return PPUMASK;
		if (i == 3) return OAMADDR;
		if (i == 2) {
			PM2005L = false;
			byte k = (byte) (LAST_WRITE & 0b00011111);
			if (NMI_OCCURRED) k |= 0b10000000;
			T_NMI = false;			// missing occasional VBL flag
			NMI_OCCURRED = false; 	// don't forget this!
			return k;
		}
		if (i == 4) { // should work during any blank
			if (NMI_OCCURRED || isForcedBlank()) {
				return oam_1[OAMADDR];
			} else {
				if (PXCYCLE < 64) return (byte) 0xFF;
			}
			return 0;
		}
		return 0;
	}
	public void runCycle() {
		// Essentially just render pixel here.
		if (isForcedBlank()) return;
		if (PXCYCLE == 341) {
			PXCYCLE = 0;
			SCANLINE++;
		}
		if (SCANLINE == -1) {
			spriteEvaluation();
		}
		if (SCANLINE >= -1 && SCANLINE <= 239) { //render
			spriteEvaluation();
		}
		if (SCANLINE == 240) { //post-render
			// IDLE
		}
		if (SCANLINE > 240) { //vblank
			if ((SCANLINE == 240) && (PXCYCLE == 0)) {
				T_NMI = true;
			}
			if (SCANLINE == 240) {
				if (PXCYCLE == 1) {
					if (T_NMI) NMI_OCCURRED = true;
	
					if (NMI_OUTPUT && NMI_OCCURRED)
						nes.cpu.S_NMI = true;
				}
			}
			if ((SCANLINE == 261 && nes.tvmode == TVMode.NTSC)||(SCANLINE == 331 && nes.tvmode == TVMode.PAL)){
				SCANLINE = 0;
				PXCYCLE = -1;
				NMI_OCCURRED = false;
			}
		}
		PXCYCLE++;
	}
	private void spriteEvaluation() {
		if (PXCYCLE < 64) {
			oam_2[PXCYCLE >> 1] = (byte) 0xFF;
		}
		if (PXCYCLE >= 64 && PXCYCLE < 256) {
			// TODO
		}
		if (PXCYCLE >= 256 && PXCYCLE < 320) {
			
		}
	}
	private boolean isForcedBlank() {
		return ((PPUMASK & 0b00011000) == 0);
	}
	public void setData(int i, byte j, int ad) {
		LAST_WRITE = j;
		if ((powerup > 0) && (i == 0x2000 || i == 0x2001 || i == 0x2005 || i == 0x2006)) {
			// Ignore register write during powerup
			return;
		}
		if (i == 0x2000) {
			PPUCTRL = j;
			NMI_OUTPUT = (j & 0x80) == 0;
		}
		if (i == 0x2001)
			PPUMASK = j;
		if ((i == 0x2003) && nes.tvmode == TVMode.NTSC) { // let's simulate the 2C02
			byte h = (byte) ((ad >> 8) & 0xF8);
			byte g = (byte) (OAMADDR & 0xF8);
			for (int f = 0; f < 8; f++) {
				oam_1[h] = oam_1[g];
			}
		}
		
	}
	// Designed for DMA
	public void setOAMByte(int a, byte b) {
		oam_1[a & 0xFF] = b;
	}
}