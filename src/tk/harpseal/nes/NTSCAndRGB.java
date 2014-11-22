package tk.harpseal.nes;

public class NTSCAndRGB { 
	// this class acts as a bridge between PPU palette values
	// and the NESCanvas

	public NTSCAndRGB() {
		
	}
	// http://forums.nesdev.com/viewtopic.php?t=8209
	public byte[] ntsc_to_rgb(int pal_entry, int PPUMASK) {
		int pixel = pal_entry | ((PPUMASK & 0xE0) << 1);
		int color = (pixel & 0x0F);
		int level = color < 0x0E ? (pixel>>4) & 3 : 1;
		final float black = .518F, white = 1.962F, attenuation = .746F;
		final float levels[] = {.350f,.518f,.962f,1.550f,1.094f,1.506f,1.962f,1.962f};
		final float lh[] = {levels[level+4*i2b(color==0x0)],levels[level+4*i2b(color<0xD)]};
		float y = 0.f, gamma = 1.8f, i = y, q = y;
		for (int p = 0; p < 12; p++) {
			float spot = lh[i2b(wave(p,color))];
			if (((pixel & 0x40) != 0 && wave(p,12)) ||
					((pixel & 0x80) != 0 && wave(p,4)) ||
					((pixel & 0x100) != 0 && wave(p,8))) spot *= attenuation;
				
			float v = (spot-black) / (white-black) / 12f;
		}
	}
	private boolean wave(int x, int y) {
		return ((x+y+8)%12)<6;
	}
	private double gammafix(float f, float gamma) {
		return f < 0.f ? 0.f : Math.pow(f, 2.2f / gamma);
	}
	private int clamp(int a) {
		return (a < 0 ? 0 : (a > 255 ? 255 : a));
	}
	private int i2b(boolean b) {
		return b ? 1 : 0;
	}

}
