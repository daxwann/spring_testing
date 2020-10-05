package com.amigoscode.testing.utils;

import com.amigoscode.testing.Utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

  private PhoneNumberValidator underTest;

  @BeforeEach
  void setUp() {
    underTest = new PhoneNumberValidator();
  }

  @ParameterizedTest
  @CsvSource({
      "+447123456789, true",
      "+4471234567890, false",
      "+44712345678, false",
      "+117123456789, false"
  })
  void itShouldValidatePhoneNumber(String phoneNumber, String expected) {
    //When
    boolean isValid = underTest.test(phoneNumber);

    //Then
    assertThat(isValid).isEqualTo(Boolean.valueOf(expected));
  }
}
