package tk.harpseal.nes;

public class NES {
	// How many nanoseconds in between master clock cycles in NTSC & PAL
	private final double CLK_NTSC_NS = 55.873D;
	private final double CLK_PAL_NS = 60.147D;
	
	// Clock divider for NTSC/PAL CPU and PPU.
	private final int PPU_NTSC_CD = 4;
	private final int CPU_NTSC_CD = 12;
	private final int PPU_PAL_CD = 5;
	private final int CPU_PAL_CD = 16;
	
	private double CLK_NS;
	private int CPU_CD;
	private int PPU_CD;
	
	private int cpuclock;
	private int ppuclock; 
	
	public CPU cpu;
	public PPU ppu;
	public APU apu;
	public ROM game;
	
	public NES(byte[] rom, TVMode m) {
		cpu = new CPU(this);
		ppu = new PPU(this);
		apu = new APU(this);
		game = ROMLoader.fromBytes(rom);
		ppuclock = 0;
		cpuclock = 0;
		if (m == TVMode.PAL) {
			CLK_NS = CLK_PAL_NS;
			CPU_CD = CPU_PAL_CD;
			PPU_CD = PPU_PAL_CD;
		} else {
			CLK_NS = CLK_NTSC_NS;
			CPU_CD = CPU_NTSC_CD;
			PPU_CD = PPU_NTSC_CD;
		}
	}
	public void runCycle() {
		if (cpuclock == 0) {
			cpu.runCycle();
			cpuclock = CPU_CD;
		} else cpuclock--;
		if (ppuclock == 0) {
			ppu.runCycle();
			ppuclock = PPU_CD;
		} else ppuclock--;
	}
	public byte fetchControllerInput(int i) {
		return 0;
	}
}
