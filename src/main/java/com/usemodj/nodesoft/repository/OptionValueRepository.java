package com.usemodj.nodesoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.OptionValue;

/**
 * Spring Data JPA repository for the OptionValue entity.
 */
public interface OptionValueRepository extends JpaRepository<OptionValue,Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM OptionValue optionValue WHERE optionValue.optionType.id = :id")
	//@Query(value = "DELETE FROM NS_OPTIONVALUE WHERE optiontype_id = :id", nativeQuery=true)
	void deleteAllByOptionTypeId(@Param("id") Long id);

}
