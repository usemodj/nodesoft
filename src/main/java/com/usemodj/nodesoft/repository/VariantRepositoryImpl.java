package com.usemodj.nodesoft.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.Query;

import com.usemodj.nodesoft.domain.Variant;

public class VariantRepositoryImpl implements VariantRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(VariantRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	
	@Inject
	private ProductRepository productRepository;
	
	@Inject
	private VariantRepository variantRepository;

    static String selectVariants = "SELECT va.id, va.sku, va.price, va.cost_price, va.position, va.product_id, va.deleted_date, r.options \n" +
            " FROM NS_VARIANT va LEFT JOIN \n" +
            " (SELECT o.id, o.product_id, GROUP_CONCAT(o.options) AS options \n" +
            "  FROM (SELECT v.id, v.product_id, concat(t.presentation,':', o.presentation) AS options \n" +
            "       FROM NS_VARIANT v, NS_VARIANT_OPTIONVALUE vo, NS_OPTIONVALUE o, NS_OPTIONTYPE t \n" +
            "       WHERE v.id = vo.variants_id and vo.optionvalues_id = o.id and o.optiontype_id = t.id) o \n" +
            "  GROUP BY o.id) r \n" +
            " ON va.id = r.id WHERE va.is_master = false AND va.product_id = :productId \n";
	@Override
	public List<Variant> findAllByProductIdAndIsMasterIsFalse(Long productId,
			Boolean deleted) {
		String nullable = (deleted == true)? " IS NOT NULL ": " IS NULL ";
		String query =  selectVariants + " AND va.deleted_date " + nullable + " ORDER BY position, id DESC";
		
		return Optional.ofNullable(em.createNativeQuery(query, "VariantDTOMapping")
				.setParameter("productId", productId)
				.getResultList())
			.map(products -> products)
			.orElse(Collections.emptyList());
	}

}
