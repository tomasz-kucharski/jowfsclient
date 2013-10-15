package org.owfs.jowfsclient;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.owfs.jowfsclient.alarm.AlarmingDevicesScanner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 * @since 14.04.13 14:18
 */
@Test(groups = TestNGGroups.UNIT)
public class OwfsConnectionFactoryTest {

	OwfsConnectionFactory factory;

	@BeforeMethod
	public void initialize() {
		factory = new OwfsConnectionFactory("fakeHostName", 0);
	}

	public void shouldCreateConnectionWithDefaultConfiguration() {
		//then
		OwfsConnectionConfig connectionConfig = factory.getConnectionConfig();
		assertEquals(connectionConfig.getPersistence(), Enums.OwPersistence.ON);
		assertEquals(connectionConfig.getBusReturn(), Enums.OwBusReturn.ON);
		assertEquals(connectionConfig.getTemperatureScale(), Enums.OwTemperatureScale.CELSIUS);
		assertEquals(connectionConfig.getOwDeviceDisplayFormat(), Enums.OwDeviceDisplayFormat.F_DOT_I);
	}

	public void shouldChangeConfig() {
		//given
		OwfsConnectionConfig xxx = new OwfsConnectionConfig("XXX", 0);

		//when
		factory.setConnectionConfig(xxx);

		//then
		assertEquals(factory.getConnectionConfig(), xxx);
	}

	public void shouldReturnNotNullSingleAlarmingScanner() {
		//when
		AlarmingDevicesScanner alarmingScanner1 = factory.getAlarmingScanner();
		AlarmingDevicesScanner alarmingScanner2 = factory.getAlarmingScanner();

		//then
		assertNotNull(alarmingScanner1);
		assertNotNull(alarmingScanner2);
		assertTrue(alarmingScanner1 == alarmingScanner2);
	}

	/**
	 * Tricky multihtreading test
	 * @throws InterruptedException
	 */
	public void shouldRunPeriodicJobs() throws InterruptedException {
		//given
		PeriodicJob periodicJob = spy(new PeriodicJob(100) {
			@Override
			public void run() {
			}
		});

		//when
		factory.addPeriodicJob(periodicJob);
		Thread.sleep(1000);

		//then
		verify(periodicJob,atLeast(2)).run();
		verify(periodicJob,atMost(15)).run();
	}



}
