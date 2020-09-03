package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

public class CustomerRegistrationServiceTest {
  @Mock
  private CustomerRepository customerRepository;

  @Captor
  private ArgumentCaptor<Customer> customerArgumentCaptor;

  private CustomerRegistrationService testService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    testService = new CustomerRegistrationService(customerRepository);
  }

  @Test
  void itShouldSaveNewCustomer() {
    // test customer
    String phone = "7777";
    Customer customer = new Customer(UUID.randomUUID(), "Marian", phone);

    // request
    CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

    // DB doesn't have customer with this phone number
    given(customerRepository.selectCustomerByPhoneNumber(phone)).willReturn(Optional.empty());

    // register customer
    testService.registerNewCustomer(customerRegistrationRequest);

    // check if DB is saving the same customer
    then(customerRepository).should().save(customerArgumentCaptor.capture());
    Customer capturedCustomerArg = customerArgumentCaptor.getValue();
    assertThat(capturedCustomerArg).isEqualTo(customer);
  }

  @Test
  void itShouldSaveNewCustomerWhenIdIsNull() {
    // test customer
    String phoneNumber = "0007";
    Customer customer = new Customer(null, "Maryam", phoneNumber);

    // request
    CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

    // customer with phone number not in DB
    given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

    // register customer
    testService.registerNewCustomer(request);

    // service should pass customer into customerRepository
    then(customerRepository).should().save(customerArgumentCaptor.capture());
    Customer customerArgument = customerArgumentCaptor.getValue();
    assertThat(customerArgument)
        .isEqualToIgnoringGivenFields(customer, "id");
    assertThat(customerArgument).isNotNull();
  }

  @Test
  void itShouldNotSaveCustomerWhenCustomerExists() {
    // test customer
    String phoneNumber = "0008";
    Customer customer = new Customer(UUID.randomUUID(), "Giannis", phoneNumber);

    // request
    CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

    // Given that DB returns that customer
    given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

    // When we register this customer
    testService.registerNewCustomer(request);

    // Then the customer is not passed to the repository for saving
    then(customerRepository).should(never()).save(any());
  }

  @Test
  void itShouldThrowWhenPhoneNumberIsTaken() {
    // test customer
    String phoneNumber = "0008";
    Customer customer = new Customer(UUID.randomUUID(), "Harden", phoneNumber);
    Customer anotherCustomer = new Customer(UUID.randomUUID(), "Westbrook", phoneNumber);

    // request
    CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

    // DB will return another customer with the same phone number
    given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
        .willReturn(Optional.of(anotherCustomer));

    // service throws error
    assertThatThrownBy(() -> testService.registerNewCustomer(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("phone number %s is taken", phoneNumber));

    // service does not pass customer to repository for saving
    then(customerRepository).should(never()).save(any());
  }
}
