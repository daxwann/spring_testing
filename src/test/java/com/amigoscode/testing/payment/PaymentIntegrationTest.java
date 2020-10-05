package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {
  @Autowired
  private PaymentRepository paymentRepository;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void itShouldCreatePaymentSuccessfully() throws Exception {
    //Register
    UUID customerId = UUID.randomUUID();
    Customer customer = new Customer(customerId, "James", "+447123456789");
    CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
    ResultActions customerRegistrationResultActions = mockMvc.perform(put("/api/v1/customer-registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Objects.requireNonNull(stringifyJsonObject(customerRegistrationRequest))));
    customerRegistrationResultActions.andExpect(status().isOk());

    // Payment
    long paymentId = 1L;
    Payment payment = new Payment(
      paymentId,
      customerId,
      new BigDecimal("100.00"),
      Currency.GBP,
      "0x0x0x",
      "books"
    );
    PaymentRequest paymentRequest = new PaymentRequest(payment);
    ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Objects.requireNonNull(stringifyJsonObject(paymentRequest))));
    paymentResultActions.andExpect(status().isOk());

    assertThat(paymentRepository.findById(paymentId))
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));
  }

  private String stringifyJsonObject(Object object) throws JsonProcessingException {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonMappingException e) {
      fail("Failed to convert object to json");
      return null;
    }
  }
}
