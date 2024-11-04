package com.work.tool.rental.pos.models;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ToolRentalRate {

        @Id
        private String toolName;
        private BigDecimal dailyAmt;
        private boolean weekdayCharge;
        private boolean weekendCharge;
        private boolean holidayCharge;

}
