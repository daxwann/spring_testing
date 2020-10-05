package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.Currency;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class StripeServiceTest {
  private StripeService underTest;

  @Mock
  private StripeApi stripeApi;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    underTest = new StripeService(stripeApi);
  }

  @Test
  void itShouldChargeCard() throws Exception {
    // Given
    String cardSource = "0x0x0x";
    BigDecimal amount = new BigDecimal("10.00");
    Currency currency = Currency.USD;
    String description = "book";

    // Successful charge
    Charge charge = new Charge();
    charge.setPaid(true);
    given(stripeApi.create(anyMap(), any())).willReturn(charge);

    // When
    CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);

    // Then
    ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
    ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
    then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

    // Assert on requestMap
    Map<String, Object> requestMap = mapArgumentCaptor.getValue();
    assertThat(requestMap.keySet()).hasSize(4);
    assertThat(requestMap.get("amount")).isEqualTo(amount);
    assertThat(requestMap.get("currency")).isEqualTo(currency);
    assertThat(requestMap.get("source")).isEqualTo(cardSource);
    assertThat(requestMap.get("description")).isEqualTo(description);

    // Assert on options
    RequestOptions options = optionsArgumentCaptor.getValue();
    assertThat(options).isNotNull();

    // Card is debited successfully
    assertThat(cardPaymentCharge).isNotNull();
    assertThat(cardPaymentCharge.isCardDebited()).isTrue();
  }
}
