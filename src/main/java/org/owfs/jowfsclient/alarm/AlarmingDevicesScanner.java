package org.owfs.jowfsclient.alarm;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.owfs.jowfsclient.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tom Kucharski
 */
public class AlarmingDevicesScanner {

	private static final Logger log = LoggerFactory.getLogger(AlarmingDevicesScanner.class);

	private static final int THREAD_POOL_SIZE = 1;

	private AlarmingDevicesReader reader;

	private int periodInterval;

	private int initialDelay;

	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	public AlarmingDevicesScanner(AlarmingDevicesReader reader) {
		this.reader = reader;
	}

	public void setPeriodInterval(int periodInterval) {
		this.periodInterval = periodInterval;
	}

	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	public synchronized void addAlarmingDeviceHandler(AlarmingDeviceListener commander) throws IOException, OwfsException {
		reader.addAlarmingDeviceHandler(commander);
		verifyIfShouldBeStartedOrStopped();
	}

	public synchronized void removeAlarmingDeviceHandler(String deviceName) {
		reader.removeAlarmingDeviceHandler(deviceName);
		verifyIfShouldBeStartedOrStopped();
	}

	public boolean isAlarmingDeviceOnList(String deviceName) {
		return reader.isAlarmingDeviceHandlerInstalled(deviceName);
	}

	private void verifyIfShouldBeStartedOrStopped() {
		if (!reader.isWorthToWork()) {
			shutdown();
		} else if (scheduledThreadPoolExecutor == null) {
			init();
		}
	}

	void init() {
		log.debug("AlarmingDeviceScanner initialization");
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);
		scheduledThreadPoolExecutor.setThreadFactory(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = Executors.defaultThreadFactory().newThread(r);
				thread.setName("jowsfsclient-alarm-" + thread.getName());
				return thread;
			}
		});
		scheduledThreadPoolExecutor.setMaximumPoolSize(THREAD_POOL_SIZE);
		scheduledThreadPoolExecutor.scheduleAtFixedRate(reader, initialDelay, periodInterval, TimeUnit.MILLISECONDS);
	}

	void shutdown() {
		log.debug("AlarmingDeviceScanner shutdown");
		scheduledThreadPoolExecutor.shutdown();
		scheduledThreadPoolExecutor = null;
	}
}
