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

package org.gogpsproject.parser.ublox;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.gogpsproject.StreamEventListener;
import org.gogpsproject.StreamEventProducer;
import org.gogpsproject.StreamResource;

@SuppressWarnings("restriction")
public class UBXSerialConnection  implements StreamResource, StreamEventProducer{
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean connected = false;

	private SerialPort serialPort;

	private UBXSerialReader ubxReader;
	private StreamEventListener streamEventListener;

	private String portName;
	private int speed;

	public UBXSerialConnection(String portName, int speed) {
		this.portName = portName;
		this.speed = speed;
	}

	@SuppressWarnings("unchecked")
	public static Vector<String> getPortList() {
		Enumeration<CommPortIdentifier> portList;
		Vector<String> portVect = new Vector<String>();
		portList = CommPortIdentifier.getPortIdentifiers();

		CommPortIdentifier portId;
		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portVect.add(portId.getName());
			}
		}
		System.out.println("Found the following ports:");
		for (int i = 0; i < portVect.size(); i++) {
			System.out.println(portVect.elementAt(i));
		}

		return portVect;
	}

	public boolean isConnected() {
		return connected;
	}


	/* (non-Javadoc)
	 * @see org.gogpsproject.StreamResource#init()
	 */
	@Override
	public void init() throws Exception {

		CommPortIdentifier portIdentifier;

//		boolean conn = false;
//		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				serialPort = (SerialPort) portIdentifier.open("Serial", 2000);
				serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				inputStream = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();

				ubxReader = new UBXSerialReader(inputStream,outputStream);
				ubxReader.setStreamEventListener(streamEventListener);
				ubxReader.start();

				connected = true;
				System.out.println("Connection on " + portName + " established");
				//conn = true;

			}
//		} catch (NoSuchPortException e) {
//			System.out.println("The connection could not be made");
//			e.printStackTrace();
//		} catch (PortInUseException e) {
//			System.out.println("The connection could not be made");
//			e.printStackTrace();
//		} catch (UnsupportedCommOperationException e) {
//			System.out.println("The connection could not be made");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("The connection could not be made");
//			e.printStackTrace();
//		}
//		return conn;

	}


	/* (non-Javadoc)
	 * @see org.gogpsproject.StreamResource#release(boolean, long)
	 */
	@Override
	public void release(boolean waitForThread, long timeoutMs)
			throws InterruptedException {

		if(ubxReader!=null){
			ubxReader.stop(waitForThread, timeoutMs);
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serialPort.close();


		connected = false;
		System.out.println("Connection disconnected");

	}


	/**
	 * @return the streamEventListener
	 */
	public StreamEventListener getStreamEventListener() {
		return streamEventListener;
	}


	/**
	 * @param streamEventListener the streamEventListener to set
	 */
	public void setStreamEventListener(StreamEventListener streamEventListener) {
		this.streamEventListener = streamEventListener;
		if(this.ubxReader!=null) this.ubxReader.setStreamEventListener(streamEventListener);
	}

}