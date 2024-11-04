package com.work.tool.rental.pos.repos;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.work.tool.rental.pos.models.ToolRentalRate;

public interface ToolRentalRateRepository extends Repository<ToolRentalRate, String> {

    Optional<ToolRentalRate> findById(String toolName);

}
