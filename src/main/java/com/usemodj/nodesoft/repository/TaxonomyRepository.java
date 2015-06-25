package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Taxonomy;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Taxonomy entity.
 */
public interface TaxonomyRepository extends JpaRepository<Taxonomy,Long> {

}
