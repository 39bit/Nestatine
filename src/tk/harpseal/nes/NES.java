package tk.harpseal.nes;

public class NES {
	public CPU cpu;
	public PPU ppu;
	public APU apu;
	public ROM game;
	// How many nanoseconds in between master clock cycles in NTSC & PAL
	private final double CPU_NTSC_NS = 55.873D;
	private final double CPU_PAL_NS = 60.147D;
	
	private final int PPU_NTSC_CD = 4;
	private final int CPU_NTSC_CD = 12;
	private final int PPU_PAL_CD = 5;
	private final int CPU_PAL_CD = 16;
	
	public NES(byte[] rom) {
		cpu = new CPU(this);
		ppu = new PPU(this);
		apu = new APU(this);
		game = ROMLoader.fromBytes(rom);
	}

	public byte fetchControllerInput(int i) {
		return 0;
	}
}
