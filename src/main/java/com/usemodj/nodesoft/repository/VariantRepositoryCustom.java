package com.usemodj.nodesoft.repository;

import java.util.List;

import com.usemodj.nodesoft.domain.Variant;

public interface VariantRepositoryCustom {
	List<Variant> findAllByProductIdAndIsMasterIsFalse(Long productId, Boolean deleted);

}
