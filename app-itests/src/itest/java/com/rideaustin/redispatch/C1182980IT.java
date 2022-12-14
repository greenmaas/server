package com.rideaustin.redispatch;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.rideaustin.model.enums.RideStatus;
import com.rideaustin.model.ride.ActiveDriver;
import com.rideaustin.testrail.TestCases;

/**
 * https://testrail.devfactory.com/index.php?/cases/view/1182980
 */
@Category(Redispatch.class)
public class C1182980IT extends Abstract4DriversRedispatchTest {

  @Test
  @TestCases("C1182980")
  public void test() throws Exception {
    doTestRedispatch(null);
  }

  @Override
  protected void cancelAndRedispatch(ActiveDriver firstDriver, ActiveDriver secondDriver, Long ride) throws Exception {
    driverAction.cancelRide(firstDriver.getDriver().getEmail(), ride)
      .andExpect(status().isOk());

    //assert that rider received no notifications
    assertFalse("Rider should not be notified about cancellation", notificationFacade.getDataMap().keySet().contains(RideStatus.DRIVER_CANCELLED.name()));

    acceptRide(secondDriver, ride);
    driverAction.cancelRide(secondDriver.getDriver().getEmail(), ride)
      .andExpect(status().isOk());

    acceptRide(thirdDriver, ride);

    driverAction.cancelRide(thirdDriver.getDriver().getEmail(), ride)
      .andExpect(status().isOk());

    acceptRedispatched(fourthDriver, ride);
  }

}
