package tk.harpseal.nes;

public class NES {
	public CPU cpu;
	public PPU ppu;
	public ROM game;
	
	public NES(byte[] rom) {
		cpu = new CPU(this);
		ppu = new PPU(this);
		game = ROMLoader.fromBytes(rom);
	}

	public byte fetchControllerInput(int i) {
		return 0;
	}
}
