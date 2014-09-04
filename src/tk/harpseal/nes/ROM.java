package tk.harpseal.nes;

import tk.harpseal.nes.mapper.Mapper;

public abstract class ROM {
	public Mapper mapper;
	public byte[] data;
	
	public ROM(byte mapper_id, byte[] d) {
		mapper = Mapper.fromId(mapper_id);
		data = d;
	}
	
	public abstract byte getByte(int addr);
	public abstract byte setByte(int addr);
	public abstract byte getRawByte(int addr);
	public abstract byte setRawByte(int addr);
	public abstract byte[] getHeader();
}
