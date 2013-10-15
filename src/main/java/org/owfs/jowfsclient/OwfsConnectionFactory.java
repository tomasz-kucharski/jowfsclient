/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package org.owfs.jowfsclient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.owfs.jowfsclient.alarm.AlarmingDevicesReader;
import org.owfs.jowfsclient.alarm.AlarmingDevicesScanner;
import org.owfs.jowfsclient.internal.OwfsConnectionImpl;
import org.owfs.jowfsclient.internal.OwfsConnectionThreadSafeProxy;
import org.owfs.jowfsclient.internal.regularfs.OwfsConnectionRegularFs;

/**
 * This is a factory client for {@link OwfsConnection}s.
 * Here's an example of how {@code OwfsConnectionFactory} and {@link OwfsConnection} can
 * be used:
 * <pre>
 *  OwfsConnectionFactory owfsConnectorFactory = new OwfsConnectionFactory(hostname, port);
 *  try {
 *      OwfsConnection connection = owfsConnectorFactory.createNewConnection();
 *      connection.write(deviceName + "/" + devicePropertyName, dynamicValue);
 *  } catch (Exception e) {
 *      handleException(e);
 *  }
 * </pre>
 *
 * @author Patrik Akerfeldt
 * @author Tom Kucharski
 */

public class OwfsConnectionFactory {

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private OwfsConnectionConfig connectionConfig;

	private AlarmingDevicesScanner alarmingScanner;

	/**
	 * Creates factory with default configuration
	 *
	 * @param hostName   hostname for owserver
	 * @param portNumber portname for owserver, usually 4304
	 */
	public OwfsConnectionFactory(String hostName, int portNumber) {
		connectionConfig = new OwfsConnectionConfig(hostName, portNumber);
		connectionConfig.setDeviceDisplayFormat(Enums.OwDeviceDisplayFormat.F_DOT_I);
		connectionConfig.setTemperatureScale(Enums.OwTemperatureScale.CELSIUS);
		connectionConfig.setPersistence(Enums.OwPersistence.ON);
		connectionConfig.setBusReturn(Enums.OwBusReturn.ON);
	}

	/**
	 * Creates a new {@link OwfsConnection} instance.
	 *
	 * @return a new {@link OwfsConnection} instance.
	 */
	public static OwfsConnection newOwfsClient(OwfsConnectionConfig config) {
		return new OwfsConnectionImpl(config);
	}

	/**
	 * Thread safe {@link OwfsConnection}
	 *
	 * @return
	 */
	public static OwfsConnection newOwfsClientThreadSafe(OwfsConnectionConfig config) {
		return new OwfsConnectionThreadSafeProxy().decorate(newOwfsClient(config));
	}

	/**
	 * Creates a new {@link OwfsConnection} instance.
	 *
	 * @param rootPath A file path to the root directory of the 1-wire file system, e.g. <strong>&quot;/mnt/1wire&quot;</strong> or
	 *                 <strong>&quot;/var/1wire/simulated-fs&quot;</strong>
	 * @return a new {@link OwfsConnection} instance.
	 */
	public static OwfsConnection newOwfsClient(String rootPath) {
		return new OwfsConnectionRegularFs(rootPath);
	}

	/**
	 * Creates new connection based on configuration provided
	 *
	 * @return
	 */
	public OwfsConnection createNewConnection() {
		return new OwfsConnectionImpl(connectionConfig);
	}

	public OwfsConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}

	public void setConnectionConfig(OwfsConnectionConfig connectionConfig) {
		this.connectionConfig = connectionConfig;
		if (alarmingScanner != null) {
			alarmingScanner.setPeriodInterval(connectionConfig.getAlarmingInterval());
		}
	}

	/**
	 * Returns alarming scanner related to {@link OwfsConnectionFactory}. If not
	 *
	 * @return alarming scanner
	 */
	public AlarmingDevicesScanner getAlarmingScanner() {
		if (alarmingScanner == null) {
			alarmingScanner = new AlarmingDevicesScanner(new AlarmingDevicesReader(this));
			alarmingScanner.setPeriodInterval(connectionConfig.getAlarmingInterval());
		}
		return alarmingScanner;
	}

	public void addPeriodicJob(PeriodicJob job) {
		job.setOwfsConnectionFactory(this);
		ScheduledFuture scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
				job,
				0,
				job.getIntervalInMiliseconds(),
				TimeUnit.MILLISECONDS
		);
		job.setScheduledFuture(scheduledFuture);
	}
}
