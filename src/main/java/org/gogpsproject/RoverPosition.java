/*
 * Copyright (c) 2011, Lorenzo Patocchi. All Rights Reserved.
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
package org.gogpsproject;
/**
 * @author Lorenzo Patocchi cryms.com
 */

/**
 * @author Cryms.com
 *
 */
public class RoverPosition extends Coordinates {

	/* Position dilution of precision (PDOP) */
	private double pDop;

	/* Horizontal dilution of precision (HDOP) */
	private double hDop;

	/* Vertical dilution of precision (VDOP) */
	private double vDop;

	private int dopType = DOP_TYPE_NONE;
	public final static int DOP_TYPE_NONE = 0;
	public final static int DOP_TYPE_STANDALONE = 1;
	public final static int DOP_TYPE_KALMAN = 2;


	public RoverPosition(Coordinates c) {
		this(c,DOP_TYPE_NONE,0.0,0.0,0.0);
	}
	/**
	 *
	 */
	public RoverPosition(Coordinates c, int dopType, double pDop, double hDop, double vDop) {
		super();
		c.cloneInto(this);
		this.dopType = dopType;
		this.pDop = pDop;
		this.hDop = hDop;
		this.vDop = vDop;
	}


	/**
	 * @return the pDop
	 */
	public double getpDop() {
		return pDop;
	}


	/**
	 * @param pDop the pDop to set
	 */
	public void setpDop(double pDop) {
		this.pDop = pDop;
	}


	/**
	 * @return the hDop
	 */
	public double gethDop() {
		return hDop;
	}


	/**
	 * @param hDop the hDop to set
	 */
	public void sethDop(double hDop) {
		this.hDop = hDop;
	}


	/**
	 * @return the vDop
	 */
	public double getvDop() {
		return vDop;
	}


	/**
	 * @param vDop the vDop to set
	 */
	public void setvDop(double vDop) {
		this.vDop = vDop;
	}


	/**
	 * @return the dopType
	 */
	public int getDopType() {
		return dopType;
	}


	/**
	 * @param dopType the dopType to set
	 */
	public void setDopType(int dopType) {
		this.dopType = dopType;
	}

}
