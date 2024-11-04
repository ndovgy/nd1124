package com.work.tool.rental.pos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.tool.rental.pos.models.Order;
import com.work.tool.rental.pos.models.RentalAgreement;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import net.javacrumbs.jsonunit.core.Option;

import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ToolRentalPosControllerTest {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static final String HOST = "http://localhost:";
    private static final String CHECKOUT_API_URL = "/api/checkout";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testInvalidToolCode() {
        var order = new Order(
                ("FOO"),
                1,
                0,
                LocalDate.parse("01/01/2024", dateFormat));

        HttpEntity<Order> request = new HttpEntity<Order>(order, getBasicAuthorizationHttpHeaders());

        var response = this.restTemplate.postForEntity(getCheckoutApiUrl(), request, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        var expectedResponse = """
                    {
                        "message": "Cound not find tool by code 'FOO'"
                    }
                """;

        assertThatJson(response.getBody()).when(IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
    void testCheckout(String toolCode, String days, String discountPercent, String date, String expectedStatus,
            String expectedErrorsJson, String expectedToolType, String expectedToolBrand, String expectedDueDate,
            String expectedDailyRentalCharge, String expectedChargeDays, String expectedPreDiscountCharge,
            String expectedDiscountAmount, String expectedFinalCharge) throws Exception {
        var expectedHttpStatus = Integer.valueOf(expectedStatus);

        Order order = new Order(
                toolCode,
                (days != null ? Integer.parseInt(days) : null),
                (discountPercent != null ? Integer.parseInt(discountPercent) : null),
                (date != null ? LocalDate.parse(date, dateFormat) : null));

        HttpEntity<Order> request = new HttpEntity<>(order, getBasicAuthorizationHttpHeaders());

        var response = this.restTemplate.postForEntity(getCheckoutApiUrl(), request, Map.class);
        assertThat(response.getStatusCode().value()).isEqualTo(expectedHttpStatus);

        if (expectedHttpStatus != OK.value()) {
            assertError(expectedErrorsJson, response);
        } else {
            var expectedAgreement = new RentalAgreement(
                    toolCode,
                    expectedToolType,
                    expectedToolBrand,
                    (Integer.parseInt(days)),
                    LocalDate.parse(date, dateFormat),
                    (LocalDate.parse(expectedDueDate, dateFormat)),
                    (new BigDecimal(expectedDailyRentalCharge)),
                    (Integer.parseInt(expectedChargeDays)),
                    (new BigDecimal(expectedPreDiscountCharge)),
                    (Integer.parseInt(discountPercent)),
                    (new BigDecimal(expectedDiscountAmount)),
                    (new BigDecimal(expectedFinalCharge)));

            assertThatJson(response.getBody()).when(IGNORING_ARRAY_ORDER)
                    .isEqualTo(objectMapper.writeValueAsString(expectedAgreement));
        }
    }

    private String getCheckoutApiUrl() {
        return HOST + port + CHECKOUT_API_URL;
    }

    private HttpHeaders getBasicAuthorizationHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:user".getBytes()));
        return headers;
    }

    private void assertError(String expectedErrorsJson, ResponseEntity<Map> response) {
        var expectedResponse = """
                    {
                        "message": "Your request was invalid due to the following errors",
                        "validation_errors": %s
                    }
                """.formatted(expectedErrorsJson);

        assertThatJson(response.getBody()).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expectedResponse);
    }

}
