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
	private byte OAM_TEMP = 0;
	
	public boolean NMI_OCCURRED = false;
	public boolean NMI_OUTPUT = false;
	public boolean T_NMI = false;
	public boolean SPR_OVERFLOW = false;
	public boolean SPR0_HIT = false;
	
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
			if (SPR0_HIT) k |= 0b01000000;
			if (SPR_OVERFLOW) k |= 0b00100000;
			T_NMI = false;			// missing occasional VBL flag
			NMI_OCCURRED = false; 	// don't forget this!
			return k;
		}
		if (i == 4) { // should work during any blank
			if (NMI_OCCURRED || isForcedBlank()) {
				return oam_1[OAMADDR];
			} else {
				return OAM_TEMP;
			}
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
			if (PXCYCLE == 1) {
				NMI_OCCURRED = false;
				SPR0_HIT = false;
				SPR_OVERFLOW = false;
			}
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
				SCANLINE = -1;
				PXCYCLE = -1;
			}
		}
		PXCYCLE++;
	}
	private byte OAMnm(int n, int m) {
		return oam_1[(n*4)+m];
	}
	private int spriteoffset() {
		return ((PPUCTRL & 0b00100000) != 0) ? 15 : 7;
	}
	private int n = 0; // these two variables are
	private int m = 0; // used for sprite evaluation
	private byte c= 0; // cycle 65-256: sprite eval cycle
					   // cycle 257-320: selected sprite cycle
	private int s = 0; // free slot on secondary OAM, a.k.a.
					   // how many sprites have been found
	private void spriteEvaluation() {
		int SPCYCLE = PXCYCLE - 1;
		int SPLINE = SCANLINE + 1;
		int o = spriteoffset();
		if (SPCYCLE >= 0 && SPCYCLE < 64) {
			OAM_TEMP = (byte) 0xFF;
			oam_2[SPCYCLE >> 1] = (byte) 0xFF;
			n = m = s = 0;
		}
		if (SPCYCLE >= 64 && SPCYCLE < 256) {
			if (c == 0) { // 1
				int y = OAMnm(n, 0);
				OAM_TEMP = (byte) y;
				int yo = (SPLINE - y);
				if (yo <= o && yo >= 0) { // 1a
					for (int x = 0; x<3; x++)
						oam_2((s*4)+x, OAMnm(n,x));
					s++;
				}
				c = 1;
			} else if (c == 1) { // 2
				n++;
				if (n == 64) // 2a
					c = 3;
				else if (s < 8) // 2b
					c = 0;
				else if (s >= 8) // 2c
					c = 2;
			} else if (c == 2) { // 3
				int y = OAMnm(n, m);
				int yo = (SPLINE - y);
				OAM_TEMP = (byte) y;
				if (yo <= o && yo >= 0) { // 3a
					SPR_OVERFLOW = true;
					m++;
					if (m == 4) {
						m = 0;
						n++;
					}
				} else { // 3b
					n++;
					m++; // sprite overflow bug!
					if (n == 64) c = 3;
					if (m == 4) m = 0;
				}
			} else if (c == 3) { // 4
				OAM_TEMP = OAMnm(n,0);
				n++;
				if (n == 64) n = 0;
			}
		}
		if (SPCYCLE == 256) {
			c = 0;
			m = 0;
		}
		if (SPCYCLE >= 256 && SPCYCLE < 320) {
			OAM_TEMP = OAMnm(m,(c > 3 ? 3 : c));
			c++;
			if (SPCYCLE == 300) {
				if (s < 8) {
					oam_2((s*4),OAMnm(n,0));
				}
				for (int v = (s*4)+1; v < 64; v++) {
					oam_2(v, (byte) 0xFF);
				}
			}
			if (c == 8) {
				c = 0;
				m++;
			}
		}
	}
	private void oam_2(int i, byte j) {
		if (i < 64)
			oam_2[i] = j;
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
			OAMADDR = j;
		}
		
	}
	// Designed for DMA
	public void setOAMByte(int a, byte b) {
		oam_1[a & 0xFF] = b;
	}
}