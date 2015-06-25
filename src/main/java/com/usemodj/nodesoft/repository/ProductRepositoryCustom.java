package com.usemodj.nodesoft.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.usemodj.nodesoft.domain.Product;
import com.usemodj.nodesoft.domain.dto.ProductDTO;

public interface ProductRepositoryCustom {
	Product getProductWithMasterVariant(Long productId);
	
    Page<ProductDTO> findAllWithMasterVariant(Pageable pageable);

}
