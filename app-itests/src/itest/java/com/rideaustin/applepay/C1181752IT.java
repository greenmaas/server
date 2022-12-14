package com.rideaustin.applepay;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.money.Money;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.maps.model.LatLng;
import com.rideaustin.Constants;
import com.rideaustin.model.ride.ActiveDriver;
import com.rideaustin.test.setup.DefaultApplePaySetup;
import com.rideaustin.test.util.TestUtils;
import com.rideaustin.testrail.TestCases;

@Category(ApplePay.class)
public class C1181752IT extends AbstractApplePayTest<DefaultApplePaySetup> {

  @Test
  @TestCases("C1181752")
  public void test() throws Exception {
    LatLng pickupLocation = locationProvider.getCenter();
    ActiveDriver driver = setup.getActiveDriver();

    driverAction.goOnline(driver.getDriver().getEmail(), pickupLocation)
      .andExpect(status().isOk());

    Long ride = riderAction.requestRide(rider.getEmail(), pickupLocation, TestUtils.REGULAR, null, SAMPLE_TOKEN);

    awaitDispatch(driver, ride);

    driverAction.acceptRide(driver, ride)
      .andExpect(status().isOk());

    driverAction.cancelRide(driver.getDriver().getEmail(), ride)
      .andExpect(status().isOk());

    paymentService.processRidePayment(ride);

    Money expectedCancellationFee = Constants.ZERO_USD;

    assertEquals(expectedCancellationFee, stripeServiceMock.getApplePayCharged());
  }
}
