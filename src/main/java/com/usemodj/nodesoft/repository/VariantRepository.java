package com.usemodj.nodesoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.Variant;

/**
 * Spring Data JPA repository for the Variant entity.
 */
public interface VariantRepository extends JpaRepository<Variant,Long>, VariantRepositoryCustom {

    @Query("select variant from Variant variant left join fetch variant.optionValues where variant.id =:id")
    Variant findOneWithEagerRelationships(@Param("id") Long id);
    
    @Query("SELECT variant FROM Variant variant WHERE variant.product.id = ?1 AND variant.isMaster = true")
	Variant findByProductIdAndIsMasterIsTrue(Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE Variant variant SET variant.deletedDate = null WHERE variant.id = :id")
	void setActive(@Param("id") Long id);
}
