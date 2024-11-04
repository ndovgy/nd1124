package com.work.tool.rental.pos.services;

import com.work.tool.rental.pos.exceptions.NoSuchToolCodeException;
import com.work.tool.rental.pos.models.Order;
import com.work.tool.rental.pos.models.RentalAgreement;
import com.work.tool.rental.pos.models.Tool;
import com.work.tool.rental.pos.models.ToolRentalRate;
import com.work.tool.rental.pos.repos.ToolRentalRateRepository;
import com.work.tool.rental.pos.repos.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;

@Service
public class CheckOutService {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private ToolRentalRateRepository toolRentalRateRepository;

    /**
     * Create flow with calculations based on requirements
     *
     * @param order
     * @return
     */
    public RentalAgreement checkout(Order order) {
        // throw a custom exception so that we can handle it separaretly in order to
        // make it more human readable, as per the requirements
        Tool tool = toolRepository.findById(order.code()).orElseThrow(() -> NoSuchToolCodeException
                .builder()
                .toolCode(order.code())
                .build());
        var toolRentalRate = toolRentalRateRepository.findById(tool.getType()).orElseThrow();

        // start at day after checkout
        LocalDate startDate = order.checkoutDate().plusDays(1);

        LocalDate dueDate = order.checkoutDate().plusDays(order.daysCount());
        int chargeableDays = calculateTotalChargeable(startDate, dueDate, order.daysCount(), toolRentalRate);
        BigDecimal preDiscountCharge = toolRentalRate.getDailyAmt().multiply(BigDecimal.valueOf(chargeableDays))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = preDiscountCharge.divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(order.discount())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalCharge = preDiscountCharge.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        RentalAgreement rentalAgreement = new RentalAgreement(
                tool.getCode(),
                tool.getType(),
                tool.getBrand(),
                order.daysCount(),
                order.checkoutDate(),
                dueDate,
                toolRentalRate.getDailyAmt(),
                chargeableDays,
                preDiscountCharge,
                order.discount(),
                discountAmount,
                finalCharge);
        return rentalAgreement;
    }

    /**
     * Returns count of chargeable days, from day after checkout through and
     * including due date, excluding “no charge” days as specified by the tool
     * type.
     */
    private int calculateTotalChargeable(LocalDate startDate, LocalDate dueDate, int chargableDaysCount,
            ToolRentalRate toolRentalRate) {
        // add one since datesUntil is exclusive of the end date
        var endDate = dueDate.plusDays(1);

        List<LocalDate> listOfDates = startDate.datesUntil(endDate).collect(Collectors.toList());
        for (LocalDate date : listOfDates) {

            if ((!toolRentalRate.isHolidayCharge() && isHoliday(date))) {
                chargableDaysCount--;
            } else {
                switch (date.getDayOfWeek()) {
                    case SATURDAY, SUNDAY -> {
                        if (!toolRentalRate.isWeekendCharge()) {
                            chargableDaysCount--;
                        }
                    }
                    default -> {
                        if (!toolRentalRate.isWeekdayCharge()) {
                            chargableDaysCount--;
                        }
                    }
                }
            }
        }

        return chargableDaysCount;
    }

    /**
     * Returns whether specified date falls within a configure holiday.
     */
    private boolean isHoliday(LocalDate date) {
        LocalDate fourthOfJuly = getFourthOfJulyAccordingRules(date);
        LocalDate laborDay = getLaborDay(date);

        return (fourthOfJuly.equals(date) || laborDay.equals(date));
    }

    /**
     * Return the day of the 4th of July jholiday, based on the following rules: if
     * falls on a weekend, then it is observed on the closest weekday.
     * For example, if it falls on a Saturday then use Friday, and if it falls on a
     * Sunday, then use the following Monday.
     */
    private LocalDate getFourthOfJulyAccordingRules(LocalDate date) {
        LocalDate holiday = LocalDate.of(date.getYear(), Month.JULY, 4);

        if (SATURDAY.equals(holiday.getDayOfWeek())) {
            holiday = holiday.minusDays(1);
        } else if (SUNDAY.equals(holiday.getDayOfWeek())) {
            holiday = holiday.plusDays(1);
        }

        return holiday;
    }

    /**
     * Return the date for the Labor dDay holida, whihc is the first Monday of
     * September
     */
    private LocalDate getLaborDay(LocalDate date) {
        LocalDate septemberDate = LocalDate.of(date.getYear(), Month.SEPTEMBER, 1);
        return septemberDate.with(TemporalAdjusters.firstInMonth(MONDAY));
    }

}
