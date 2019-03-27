package de.thkoeln.bibl.api.rfid.tag.data;

import java.util.zip.Checksum;

/**
 * Class implements the CRC-16 checksum used to verify the tag
 * data model. Reads in a sequence of bytes and prints out its
 * 16 bit cycle redundancy check (CRC-CCIIT 0xFFFF).
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class CRC16 implements Checksum {
	
	private int crc = 0xFFFF;
	private int poly = 0x1021;
	
	/**
	 * Initialize a new CRC16.
	 */
	public CRC16() {}
	
	/**
	 * Initialize a new CRC16 and updates the checksum
	 * with the passed data array.
	 * 
	 * @param data the data to build the checksum for
	 */
	public CRC16(byte[] data) {
		update(data, 0, data.length);
	}
	
	@Override
	public long getValue() {
		return crc;
	}

	@Override
	public void reset() {
		crc = 0xFFFF;
	}

	@Override	
	public void update(int b) {
		
		boolean xor;
		b = (b & 0xFF) << 8;
		
		for(int i=0; i < 8; i++)
		{
			xor = ((crc ^ b) & 0x8000) != 0;
			crc <<= 1;
			if (xor) crc ^= poly;
			b <<= 1;
		}
		crc &= 0xFFFF;
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for (int i = off; i < off + len; i++)
			update((int)b[i]);
	}
	
	@Override
	public String toString() {
		return Long.toString(crc);
	}
}