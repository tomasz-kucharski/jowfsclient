package org.owfs.jowfsclient.alarm;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.owfs.jowfsclient.OwfsException;
import org.owfs.jowfsclient.TestNGGroups;
import org.testng.annotations.Test;

/**
 * @author Tomasz Kucharski <tomasz.kucharski@decerto.pl>
 * @since 15.10.13 22:07
 */
@Test(groups = TestNGGroups.UNIT)
public class AlarmingDevicesScannerTest {

	public void shouldVerifyIfItNeedsToStart() throws IOException, OwfsException {
		//given
		AlarmingDevicesReader mock = mock(AlarmingDevicesReader.class);
		when(mock.isWorthToWork()).thenReturn(true);
		AlarmingDevicesScanner alarmingDevicesScanner = new AlarmingDevicesScanner(mock);
		AlarmingDeviceListener listenerMock = mock(AlarmingDeviceListener.class);

		//when
		alarmingDevicesScanner.addAlarmingDeviceHandler(listenerMock);

		//then
		verify(mock,times(1)).addAlarmingDeviceHandler(any(AlarmingDeviceListener.class));
		verify(mock,times(1)).isWorthToWork();
	}
}
