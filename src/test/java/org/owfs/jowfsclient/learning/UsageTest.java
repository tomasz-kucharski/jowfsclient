package org.owfs.jowfsclient.learning;

import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.OwfsConnectionFactory;
import org.owfs.jowfsclient.TestNGGroups;
import org.testng.annotations.Test;

/**
 * @author Tom Kucharski
 * @since 15.10.13 23:29
 */
@Test(groups = TestNGGroups.LEARNING)
public class UsageTest {

	String hostname;
	int port;

	String deviceName;
	String devicePropertyName;
	String dynamicValue;

	public void showSimplestUsage() {
		OwfsConnectionFactory owfsConnectorFactory = new OwfsConnectionFactory(hostname, port);
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			connection.write(deviceName + "/" + devicePropertyName, dynamicValue);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void handleException(Exception e) {
	}
}
