package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.OptionType;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OptionType entity.
 */
public interface OptionTypeRepository extends JpaRepository<OptionType,Long> {

}
