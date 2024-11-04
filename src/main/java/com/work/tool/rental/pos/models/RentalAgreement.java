package com.work.tool.rental.pos.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

public record RentalAgreement(
        String code,
        String type,
        String brand,
        int rentalDays,
        LocalDate checkoutDate,
        LocalDate dueDate,
        BigDecimal dailyRentalCharge,
        int chargeDays,
        BigDecimal preDiscountCharge,
        int discount,
        BigDecimal discountAmount,
        BigDecimal finalCharge
) {

  /**
   * Prints the rental agreement to the console, using the formatting defined by the requirements,
   * by running it through a Thymeleaf template.
   */
    public void printToConsole(SpringTemplateEngine templateEngine) {
        var context = new Context();
        context.setVariable("agreement", this);

        var result = templateEngine.process("rental-agreement.txt", context);
        System.out.println(result);
    }

}



