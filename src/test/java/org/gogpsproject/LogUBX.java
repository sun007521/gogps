/*
 * Copyright (c) 2010, Eugenio Realini, Mirko Reguzzoni, Cryms sagl - Switzerland. All Rights Reserved.
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
 *
 */
package org.gogpsproject;
import java.util.Vector;

import org.gogpsproject.parser.ublox.UBXSerialConnection;

/**
 * @author Eugenio Realini, Cryms.com
 *
 */
public class LogUBX {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{

			if(args.length<1){
				System.out.println("Usage example: goGPS_UBX_logger <COM10> <COM16>");
				
				UBXSerialConnection.getPortList();
				return;
			}
			
			for (int a = 0; a < args.length; a++) {
				UBXSerialConnection ubxSerialConn = new UBXSerialConnection(args[a], 9600);
				ubxSerialConn.init();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
