package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StripeService implements CardPaymentCharger {
  private final StripeApi stripeApi;
  private final static RequestOptions requestOptions = RequestOptions
      .builder()
      .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
      .build();

  public StripeService(StripeApi stripeApi) {
    this.stripeApi = stripeApi;
  }

  @Override
  public CardPaymentCharge chargeCard(
      String cardSource,
      BigDecimal amount,
      Currency currency,
      String description
  ) {
    Map<String, Object> params = new HashMap<>();
    params.put("amount", amount);
    params.put("currency", currency);
    params.put("source", cardSource);
    params.put("description", description);

    try {
      Charge charge = stripeApi.create(params, requestOptions);
      return new CardPaymentCharge(charge.getPaid());
    } catch (Exception e) {
      throw new IllegalStateException("Cannot make stripe charge", e);
    }
  }
}
