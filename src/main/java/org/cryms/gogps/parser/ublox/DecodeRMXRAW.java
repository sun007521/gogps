/*
 * Copyright (c) 2010, Cryms.com . All Rights Reserved.
 *
 * This file is part of goGPS Project (goGPS).
 *
 * goGPS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * goGPS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with goGPS.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.cryms.gogps.parser.ublox;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.cryms.gogps.util.Bits;
import org.cryms.gogps.util.UnsignedOperation;
import org.gogpsproject.ObservationSet;
import org.gogpsproject.Observations;
import org.gogpsproject.Time;


public class DecodeRMXRAW {
	//private boolean[] bits;
	InputStream in;
	
	int[] fdata;
	int[] fbits;
	boolean end = true;

	// public gpsDecode(boolean[] _bits){
	// bits=_bits;
	// }
	public DecodeRMXRAW(InputStream _in) {
		in = _in;
	}

	public Observations decode() throws IOException,UBXException {
		// parse little Endian data

		int[] length = new int[2];
		int[] data;

		length[1] = in.read();
		length[0] = in.read();
		
		int CH_A = 0;
		int CH_B = 0;
		CH_A += 0x02;CH_B += CH_A;

		CH_A += 0x10;CH_B += CH_A;
		CH_A += length[1];CH_B += CH_A;
		CH_A += length[0];CH_B += CH_A;
		
		int len = length[0]*256+length[1];
		//System.out.println(" %%%%%%%%%% Length : " + len+" "+(length[0]*255+length[1]));
		data = new int[8];
		//int[] datatmp = new int[len];
		//System.out.print("\n Header ");
		for (int i = 0; i < 8; i++) {
			data[i] = in.read();
			CH_A += data[i];CH_B += CH_A;
			//System.out.print("0x" + Integer.toHexString(data[i]) + " ");
		}
		//System.out.println();
		boolean[] bits = new boolean[8 * 4];
		int indice = 0;
		for (int j = 3; j >= 0; j--) {
			boolean[] temp1 = Bits.intToBits(data[j], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
		}
		int tow = Bits.bitsTwoComplement(bits);
		//System.out.println("Gps TOW " + tow + " ms");
		
		bits = new boolean[8 * 2];
		indice = 0;
		for (int j = 5; j >= 4; j--) {
			boolean[] temp1 = Bits.intToBits(data[j], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
		}
		int week = Bits.bitsTwoComplement(bits);
		//System.out.println("Week :  " + week );
		
		bits = new boolean[8];
		indice = 0;
		boolean[] temp1 = Bits.intToBits(data[6], 8);
		for (int i = 0; i < 8; i++) {
			bits[indice] = temp1[i];
			indice++;
		}
		
		int numSV = Bits.bitsToUInt(bits);
		//System.out.println("NumSV :  " + numSV + " S ");

		bits = new boolean[8];
		indice = 0;
		temp1 = Bits.intToBits(data[7], 8);
		for (int i = 0; i < 8; i++) {
			bits[indice] = temp1[i];
			indice++;
		}

		//System.out.println("Res :  " + Bits.bitsToUInt(bits) + "  ");

		data = new int[len - 8];

		for (int i = 0; i < len - 8; i++) {
			data[i] = in.read();
			CH_A += data[i];CH_B += CH_A;
			//System.out.print("0x" + Integer.toHexString(data[i]) + " ");
		}
		//System.out.println();
		
		long gmtTS = getGMTTS(tow, week);
		Observations o = new Observations(new Time(gmtTS),0);

		//System.out.println(tow+"  "+o.getRefTime().getGpsTime());
		
		
		for (int k = 0; k < (len - 8) / 24; k++) {
			
			ObservationSet os = new ObservationSet();
			
			
			int offset = k * 24;
			bits = new boolean[8 * 8]; // R8
			indice = 0;
			for (int j = offset + 7; j >= 0 + offset; j--) {
				temp1 = Bits.intToBits(data[j], 8);
				for (int i = 0; i < 8; i++) {
					bits[indice] = temp1[i];
					indice++;
				}
			}
			os.setPhase(ObservationSet.L1, UnsignedOperation.toDouble(Bits.tobytes(bits)));
//			System.out.print(k+"\tPhase: "
//					+ os.getPhase(ObservationSet.L1) + "  ");
			bits = new boolean[8 * 8]; // R8
			indice = 0;
			for (int j = offset + 7 + 8; j >= 8 + offset; j--) {
				temp1 = Bits.intToBits(data[j], 8);
				for (int i = 0; i < 8; i++) {
					bits[indice] = temp1[i];
					indice++;
				}
			}
			os.setCodeC(ObservationSet.L1, UnsignedOperation.toDouble(Bits.tobytes(bits)));
//			System.out.print(" Code: "
//					+ os.getCodeC(ObservationSet.L1) + "  ");
			bits = new boolean[8 * 4]; // R8
			indice = 0;
			for (int j = offset + 7 + 8 + 4; j >= 8 + 8 + offset; j--) {
				temp1 = Bits.intToBits(data[j], 8);
				for (int i = 0; i < 8; i++) {
					bits[indice] = temp1[i];
					indice++;
				}
			}
			os.setDoppler(ObservationSet.L1, UnsignedOperation.toFloat(Bits.tobytes(bits)));
//			System.out.print(" Doppler: "
//					+ os.getDoppler(ObservationSet.L1) + "  ");
			bits = new boolean[8];
			indice = 0;
			temp1 = Bits.intToBits(data[offset + 7 + 8 + 4 + 1], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
			os.setSatID(Bits.bitsToUInt(bits));
//			System.out.print (" SatID: "
//					+ os.getSatID() + "  ");
			
			
			bits = new boolean[8];
			indice = 0;
			temp1 = Bits.intToBits(data[offset + 7 + 8 + 4 + 1 + 1], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
//			System.out.print("Nav Measurements Quality Ind.: "
//					+ Bits.bitsTwoComplement(bits) + "  ");
//			System.out.print(" QI: "
//					+ Bits.bitsToUInt(bits) + "  ");
			bits = new boolean[8];
			indice = 0;
			temp1 = Bits.intToBits(data[offset + 7 + 8 + 4 + 1 + 1 + 1], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
			
			os.setSignalStrength(ObservationSet.L1, Bits.bitsTwoComplement(bits));
//			System.out.print(" SNR: " // Signal strength C/No. (dbHz)
//					+ os.getSignalStrength(ObservationSet.L1) + "  ");
			bits = new boolean[8];
			indice = 0;
			temp1 = Bits.intToBits(data[offset + 7 + 8 + 4 + 1 + 1 + 1 + 1], 8);
			for (int i = 0; i < 8; i++) {
				bits[indice] = temp1[i];
				indice++;
			}
//			System.out.println(" Lock: "//Loss of lock indicator (RINEX definition)
//					+ Bits.bitsToUInt(bits) + "  ");
			int total = offset + 7 + 8 + 4 + 1 + 1 + 1 + 1;
			//System.out.println("Offset " + total);

			o.setGps(k, os);
		}
		// / Checksum
		
		
		CH_A = CH_A & 0xFF;
		CH_B = CH_B & 0xFF;
		if(CH_A != in.read() && CH_B!=in.read())
			throw new UBXException("Wrong message checksum");
//		System.out.println("CH_A cal " + Integer.toHexString(CH_A)
//				+ " CH_K packetto " + Integer.toHexString(in.read()));
//		System.out.println("CH_B cal " + Integer.toHexString(CH_B)
//				+ " CH_K packetto " + Integer.toHexString(in.read()));
		
		return o;
	}
	
	private long getGMTTS(int tow, int week) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.set(Calendar.YEAR, 1980);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DAY_OF_MONTH, 6);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		c.add(Calendar.WEEK_OF_YEAR, week);
		c.add(Calendar.MILLISECOND, (int)(Math.round((double)tow/1000.0)*1000));
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss.SSS");
		//System.out.println(sdf.format(c.getTime()));
		
		return c.getTimeInMillis();
	}
}