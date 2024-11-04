package com.work.tool.rental.pos.controllers;

import com.work.tool.rental.pos.models.Order;
import com.work.tool.rental.pos.models.RentalAgreement;
import com.work.tool.rental.pos.services.CheckOutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring6.SpringTemplateEngine;

@RestController
public class CheckoutController {

    @Autowired
    private CheckOutService checkoutService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * This API endpoint allows you to place an order for tool rental and returns
     * {@link RentalAgreement} object with the charge details.
     */
    @PostMapping("/api/checkout")
    public RentalAgreement checkout(@Valid @RequestBody Order newOrder) {
        var agreement = checkoutService.checkout(newOrder);

        agreement.printToConsole(templateEngine);

        return agreement;
    }

}
