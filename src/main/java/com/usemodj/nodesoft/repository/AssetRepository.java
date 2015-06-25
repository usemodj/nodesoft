package com.usemodj.nodesoft.repository;

import com.usemodj.nodesoft.domain.Asset;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Asset entity.
 */
public interface AssetRepository extends JpaRepository<Asset,Long>, AssetRepositoryCustom {

}
