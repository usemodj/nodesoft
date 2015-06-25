package com.usemodj.nodesoft.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.SqlResultSetMapping;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.usemodj.nodesoft.domain.Product;
import com.usemodj.nodesoft.domain.dto.ProductDTO;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	
	@Inject
	private ProductRepository productRepository;
	
	@Inject
	private VariantRepository variantRepository;
	
    static String selectProducts = "SELECT p.*, v.sku, v.price FROM NS_PRODUCT p LEFT JOIN NS_VARIANT v ON v.product_id = p.id AND v.is_master = true";

	@Override
	public Product getProductWithMasterVariant(Long productId) {
		return Optional.ofNullable(productRepository.findOne(productId))
			.map(product ->{
				product.setVariant(variantRepository.findByProductIdAndIsMasterIsTrue(productId));
				return product;
			})
			.get();
	}

	@Override
	public Page<ProductDTO> findAllWithMasterVariant(Pageable pageable) {
		List<ProductDTO> list = new ArrayList<ProductDTO>();
		long total = productRepository.count();
		int offset = pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();
		List<String> directions = new ArrayList<String>();
		for(Order order: sort){
			String property = order.getProperty();
			String direction = order.isAscending()? property+" ASC": property+" DESC";
			directions.add(direction);
		}
		
		String query = "SELECT sp.id AS id, sp.name AS name, sp.sku AS sku, sp.price AS price, sp.available_date AS available_date, sp.deleted_date AS deleted_date FROM ( \n"+ selectProducts + "\n) sp ";
		if(directions.size() > 0)
			query += "\n ORDER BY "+ StringUtils.join(directions, ", ");
		query += "\n LIMIT "+ offset + ", "+ pageSize;
		log.debug( query);
		
		
		list.addAll( Optional.ofNullable(em.createNativeQuery(query, "ProductDTOMapping").getResultList())
			.map(products -> products)
			.orElse(Collections.emptyList())
			);
			
		Page<ProductDTO> page = new PageImpl<ProductDTO>(list, pageable, total);
		
		return page;
	}

}
