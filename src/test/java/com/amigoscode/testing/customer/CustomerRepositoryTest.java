package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import java.util.Optional;
import java.util.UUID;

@DataJpaTest(properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"})
public class CustomerRepositoryTest {
  private final CustomerRepository testRepository;

  @Autowired
  public CustomerRepositoryTest(CustomerRepository testRepository) {
    this.testRepository = testRepository;
  }

  @Test
  void itShouldSelectCustomerByPhoneNumber() {
    // test customer with complete data
    UUID id = UUID.randomUUID();
    String phoneNumber = "1111";
    String name = "Hansel";
    Customer customer = new Customer(id, name, phoneNumber);

    // saving to an embedded db
    testRepository.save(customer);

    // test query against saved entity
    Optional<Customer> optionalCustomer = testRepository.selectCustomerByPhoneNumber(phoneNumber);
    assertThat(optionalCustomer)
        .isPresent()
        .hasValueSatisfying(c -> {
          assertThat(c).isEqualToComparingFieldByField(customer);
        });
  }

  @Test
  void itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExist() {
    // test number not in DB
    String phoneNumber = "0000";

    Optional<Customer> optionalCustomer = testRepository.selectCustomerByPhoneNumber(phoneNumber);

    assertThat(optionalCustomer).isNotPresent();
  }

  @Test
  void itShouldSaveCustomer() {
    // Test customer with complete data
    UUID id = UUID.randomUUID();
    Customer customer = new Customer(id, "Bruce", "1111");

    // save customer
    testRepository.save(customer);

    // check if saved
    Optional<Customer> optionalCustomer = testRepository.findById(id);
    assertThat(optionalCustomer)
        .isPresent()
        .hasValueSatisfying(c -> {
          assertThat(c).isEqualToComparingFieldByField(customer);
        });
  }

  @Test
  void itShouldNotSaveCustomerWhenNameIsNull() {
    // test customer with no name
    UUID id = UUID.randomUUID();
    Customer customer = new Customer(id, null, "1111");

    assertThatThrownBy(() -> testRepository.save(customer))
        .hasMessageContaining("not-null property references a null or transient value")
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
    // test customer with no phone
    UUID id = UUID.randomUUID();
    Customer customer = new Customer(id, "Daisy", null);

    assertThatThrownBy(() -> testRepository.save(customer))
        .hasMessageContaining("not-null property references a null or transient value")
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
