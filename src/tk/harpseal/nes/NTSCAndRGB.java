package tk.harpseal.nes;

public class NTSCAndRGB { 
	// this class acts as a bridge between PPU palette values
	// and the NESCanvas

	public NTSCAndRGB() {
		
	}
	public final double PI = 3.1415926535D;
	// http://forums.nesdev.com/viewtopic.php?t=8209
	public byte[] ntsc_to_rgb(int pixel, int PPUMASK) {
		int color = (pixel & 0x0F);
		if ((PPUMASK & 1) != 0) color = 0;
		int level = color < 0x0E ? (pixel>>4) & 3 : 1;
		final float black = .518F, white = 1.962F, attenuation = .746F;
		final float levels[] = {.350f,.518f,.962f,1.550f,1.094f,1.506f,1.962f,1.962f};
		final float lh[] = {levels[level+4*i2b(color==0x0)],levels[level+4*i2b(color<0xD)]};
		float y = 0.f, gamma = 1.8f, i = y, q = y;
		for (int p = 0; p < 12; p++) {
			float spot = lh[i2b(wave(p,color))];
			if (((PPUMASK & 0x20) != 0 && wave(p,12)) ||
					((PPUMASK & 0x40) != 0 && wave(p,4)) ||
					((PPUMASK & 0x80) != 0 && wave(p,8))) spot *= attenuation;
				
			float v = (spot-black) / (white-black) / 12f;
			y += v;
			i += v * Math.cos(PI * p / 6.0);
			q += v * Math.sin(PI * p / 6.0);
		}
		byte[] rgb = {(byte) clamp(255 * gammafix(y + 0.946882f*i + 0.623557f*q, gamma)),(byte) clamp(255 * gammafix(y + -0.274788f*i + -0.635691f*q,gamma)),(byte) clamp(255 * gammafix(y + -1.108545f*i +  1.709007f*q,gamma))};
		return rgb;
	}
	// this isn't actually really PAL yet, only difference is red/green switch
	public byte[] pal_to_rgb(int pixel, int PPUMASK) {
		int color = (pixel & 0x0F);
		if ((PPUMASK & 1) != 0) color = 0;
		int level = color < 0x0E ? (pixel>>4) & 3 : 1;
		final float black = .518F, white = 1.962F, attenuation = .746F;
		final float levels[] = {.350f,.518f,.962f,1.550f,1.094f,1.506f,1.962f,1.962f};
		final float lh[] = {levels[level+4*i2b(color==0x0)],levels[level+4*i2b(color<0xD)]};
		float y = 0.f, gamma = 1.8f, i = y, q = y;
		for (int p = 0; p < 12; p++) {
			float spot = lh[i2b(wave(p,color))];
			if (((PPUMASK & 0x40) != 0 && wave(p,12)) ||
					((PPUMASK & 0x20) != 0 && wave(p,4)) ||
					((PPUMASK & 0x80) != 0 && wave(p,8))) spot *= attenuation;
				
			float v = (spot-black) / (white-black) / 12f;
			y += v;
			i += v * Math.cos(PI * p / 6.0);
			q += v * Math.sin(PI * p / 6.0);
		}
		byte[] rgb = {(byte) clamp(255 * gammafix(y + 0.946882f*i + 0.623557f*q, gamma)),(byte) clamp(255 * gammafix(y + -0.274788f*i + -0.635691f*q,gamma)),(byte) clamp(255 * gammafix(y + -1.108545f*i +  1.709007f*q,gamma))};
		return rgb;
	}
	private boolean wave(int x, int y) {
		return ((x+y+8)%12)<6;
	}
	private double gammafix(float f, float gamma) {
		return f < 0.f ? 0.f : Math.pow(f, 2.2f / gamma);
	}
	private int clamp(double a) {
		return ((int) a < 0 ? 0 : ((int) a > 255 ? 255 : (int) a));
	}
	private int i2b(boolean b) {
		return b ? 1 : 0;
	}

}
