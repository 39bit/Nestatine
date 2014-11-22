package tk.harpseal.nes;

public class PPU {
	private NES nes;
	private byte[] mem = new byte[8192];
	private byte[] oam_1 = new byte[256];
	private byte[] oam_2 = new byte[64];
	private byte PPUCTRL = 0b00000000;
	private byte PPUMASK = 0b00000000;	
	// Controls the latch of $2005 and $2006
	private boolean PM2005L = false;
	private byte PPUSCRL_X = 0;
	private byte PPUSCRL_Y = 0;
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
	private byte R2007_TEMP = 0;

	private boolean VBLANK = false;
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
		if (i == 5) {
			if (!PM2005L) return PPUSCRL_Y;
			else return PPUSCRL_X;
		}
		if (i == 6) {
			if (!PM2005L) return (byte) (VRAMADDR & 0xFF);
			else return (byte) ((VRAMADDR >> 8) & 0xFF);
		}
		if (i == 7) {
			byte r = R2007_TEMP;
			R2007_TEMP = readVRAM(VRAMADDR);
			if (VRAMADDR >= 0x3F00) r = R2007_TEMP;
			return r;
		}
		return 0;
	}
	private byte readVRAM(short v) {
		v = mirrorVRAM(v);
		if (v >= 0x2000) return mem[v-0x2000];
		
		// CHR ROM / RAM
		return 0;
	}
	private short mirrorVRAM(short v) {
		v = (short) (v & 0x3FFF);
		if (v >= 0x3F00) v = (short) (0x3F00 | (v & 0x1F));
		else if (v >= 0x3000) v -= 0x1000;
		if ((v >> 12 == 0x2) && nes.mirror == MirroringScheme.SINGLESCREEN)
			v = (short) (0x2000 | (v & 0x3FF));
		if ((v >> 12 == 0x2) && nes.mirror == MirroringScheme.HORIZONTAL)
			v = (short) (0x2000 | (v & 0xBFF));
		if ((v >> 12 == 0x2) && nes.mirror == MirroringScheme.VERTICAL)
			v = (short) (0x2000 | (v & 0x7FF));
		return v;
	}
	public void setVRAMAddress(short i) {
		VRAMADDR = (short) (i & 0x3FFF);
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
			if (PXCYCLE > 1 && PXCYCLE <= 256) {
				
			}
		}
		if (SCANLINE == 240) { //post-render
			// IDLE
		}
		if (SCANLINE > 240) { //vblank
			if ((SCANLINE == 240) && (PXCYCLE == 0)) {
				T_NMI = true;
				VBLANK = true;
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
				VBLANK = false;
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
		if (i == 0x2002) return;
		if (i == 0x2004) {
			if (isForcedBlank() | VBLANK) {
				oam_1[OAMADDR] = j;
				OAMADDR = (byte) ((OAMADDR + 1) & 0xFF);
			} else {
				// "For emulation purposes, it is probably best to completely ignore writes during rendering."
				// sorry but no
				OAMADDR = (byte) ((OAMADDR & 0x03) | ((((OAMADDR >> 2) + 1) & 0x3F) << 2));
			}
		}
		// TODO
	}
	// Designed for DMA
	public void setOAMByte(int a, byte b) {
		oam_1[a & 0xFF] = b;
	}
}