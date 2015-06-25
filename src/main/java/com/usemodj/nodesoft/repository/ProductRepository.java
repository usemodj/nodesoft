package com.usemodj.nodesoft.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.usemodj.nodesoft.domain.Product;

/**
 * Spring Data JPA repository for the Product entity.
 */
public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom {

    @Query("select product from Product product left join fetch product.optionTypes left join fetch product.taxons where product.id =:id")
    Product findOneWithEagerRelationships(@Param("id") Long id);

}
