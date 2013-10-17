package org.owfs.jowfsclient;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Tomasz Kucharski <tomasz.kucharski@decerto.pl>
 * @since 15.10.13 23:49
 */
public abstract class PeriodicJob implements Runnable {

	private OwfsConnectionFactory owfsConnectionFactory;

	private ScheduledFuture scheduledFuture;

	private final int intervalInMiliseconds;

	protected PeriodicJob(int intervalInMiliseconds) {
		this.intervalInMiliseconds = intervalInMiliseconds;
	}

	public OwfsConnectionFactory getOwfsConnectionFactory() {
		return owfsConnectionFactory;
	}

	void setOwfsConnectionFactory(OwfsConnectionFactory owfsConnectionFactory) {
		this.owfsConnectionFactory = owfsConnectionFactory;
	}

	public ScheduledFuture getScheduledFuture() {
		return scheduledFuture;
	}

	void setScheduledFuture(ScheduledFuture scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	private OwfsConnection getConnection() {
		return owfsConnectionFactory.createNewConnection();
	}

	public int getIntervalInMiliseconds() {
		return intervalInMiliseconds;
	}

	public void run() {
		run(getConnection());
	}

	public abstract void run(OwfsConnection connection);

	public void cancel() {
		scheduledFuture.cancel(false);
	}
}
