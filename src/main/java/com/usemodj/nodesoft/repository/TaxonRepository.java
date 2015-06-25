package com.usemodj.nodesoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.Taxon;

/**
 * Spring Data JPA repository for the Taxon entity.
 */
public interface TaxonRepository extends JpaRepository<Taxon,Long>, TaxonRepositoryCustom {

	@Modifying
	@Transactional
	void deleteAllByTaxonomyId(Long id);

//	@Modifying
//	@Transactional
	@Query(value="INSERT INTO NS_TAXON (id, name, position, permalink, depth, parent_id, taxonomy_id)"+
			"VALUES(?1, ?2, ?3, ?4, ?5, ?6, ?7)", nativeQuery=true)
	void insertTaxon(Long id, String name, Integer position, String permalink, Integer depth, Long parentId, Long taxonomyId);

}
