package com.work.tool.rental.pos.repos;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import com.work.tool.rental.pos.models.Tool;

public interface ToolRepository extends Repository<Tool, String> {

    Optional<Tool> findById(String code);

}
