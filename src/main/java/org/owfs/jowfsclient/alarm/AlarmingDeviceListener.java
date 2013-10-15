package org.owfs.jowfsclient.alarm;

import java.io.IOException;
import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.OwfsException;

/**
 *
 * @author Tom Kucharski
 */
public interface AlarmingDeviceListener {

	/**
	 * @return device name that is scan for alar
	 */
	String getDeviceName();

	/**
	 * Performs device initialization just before it is started scanning.
	 * Usually it contains clearing power flag, setting alarm mask and other activities that prepares this device to be scanned for alarming properly
	 * and not raising faked alarms
	 * @param client
	 * @throws IOException any exception thrown by Owfsclient
	 * @throws OwfsException any owfs exception thrown by Owfsclient
	 */
	void onInitialize(OwfsConnection client) throws IOException, OwfsException;

	/**
	 * Invoked when device is in alarming state.
	 * @param client connection to 1-wire device
	 * @throws IOException any io exception thrown by Owfsclient
	 * @throws OwfsException any owfs exception thrown by Owfsclient
	 */
	void onAlarm(OwfsConnection client) throws IOException, OwfsException;
}
