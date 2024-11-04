package com.work.tool.rental.pos.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Order(

        @NotNull(message = "{validation.order.code.null}")
        String code,

        @NotNull(message = "{validation.order.daysCount.null}")
        @Min(value = 1, message = "{validation.order.daysCount.range}")
        Integer daysCount,

        @NotNull(message = "{validation.order.discount.null}")
        @Min(value = 0, message = "{validation.order.discount.range}")
        @Max(value = 100, message = "{validation.order.discount.range}")
        Integer discount,

        @NotNull(message = "{validation.order.checkoutDate.null}")
        LocalDate checkoutDate)
        
{}
