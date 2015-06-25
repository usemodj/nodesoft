package com.usemodj.nodesoft.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.OptionType;
import com.usemodj.nodesoft.domain.Product;
import com.usemodj.nodesoft.domain.Taxon;
import com.usemodj.nodesoft.domain.Variant;
import com.usemodj.nodesoft.domain.dto.ProductDTO;
import com.usemodj.nodesoft.repository.OptionTypeRepository;
import com.usemodj.nodesoft.repository.ProductRepository;
import com.usemodj.nodesoft.repository.TaxonRepository;
import com.usemodj.nodesoft.repository.VariantRepository;
import com.usemodj.nodesoft.repository.search.ProductSearchRepository;
import com.usemodj.nodesoft.repository.search.VariantSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Product.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    @Inject
    private ProductRepository productRepository;

    @Inject
    private ProductSearchRepository productSearchRepository;

    @Inject
    private VariantRepository variantRepository;

    @Inject
    private VariantSearchRepository variantSearchRepository;

    @Inject
    private OptionTypeRepository optionTypeRepository;
    
    @Inject
    private TaxonRepository taxonRepository;

    /**
     * POST  /products -> Create a new product.
     */
    @RequestMapping(value = "/products",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> create(@RequestBody Product product) throws URISyntaxException {
    	Variant variant = product.getVariant();
        log.debug("REST request to save Product : {}", product);
        log.debug("REST request to save Variant : {}", variant);
        
        Set optionTypes = new HashSet<OptionType>();
        for(Long id: product.getOptionTypeIds()){
            log.debug("REST request to save OptionType id : {}", id);
            Optional.ofNullable(optionTypeRepository.findOne(id))
        	.map(optionType -> optionTypes.add(optionType));
        }
        Set taxons = new HashSet<Taxon>();
        for(Long id: product.getTaxonIds()){
            log.debug("REST request to save Taxon id : {}", id);
        	Optional.ofNullable(taxonRepository.findOne(id))
        	.map(taxon -> taxons.add(taxon));
        }
        product.setOptionTypes(optionTypes);
        product.setTaxons(taxons);
        productRepository.save(product);
        productSearchRepository.save(product);
        
        variant.setIsMaster(true);
        variant.setProduct(product);
        variantRepository.save(variant);
        variantSearchRepository.save(variant);
        
        product.setVariant(variant);
        //return ResponseEntity.created(new URI("/api/products/" + product.getId())).build();
        return new ResponseEntity<>(
                product,
                HttpStatus.OK);
    }
    /**
     * POST  /products/clone -> Clone a product.
     */
    @RequestMapping(value = "/products/clone",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> createClone(@RequestBody Product product) throws URISyntaxException {
    	Variant variant = product.getVariant();
        log.debug("REST request to save Product : {}", product);
        log.debug("REST request to save Variant : {}", variant);

        Set optionTypes = new HashSet<OptionType>();
        for(Long id: product.getOptionTypeIds()){
        	Optional.ofNullable(optionTypeRepository.findOne(id))
        	.map(optionType -> optionTypes.add(optionType));
        }
        Set taxons = new HashSet<Taxon>();
        for(Long id: product.getTaxonIds()){
        	Optional.ofNullable(taxonRepository.findOne(id))
        	.map(taxon -> taxons.add(taxon));
        }
        product.setOptionTypes(optionTypes);
        product.setTaxons(taxons);
        product.setId(null);
        productRepository.save(product);
        productSearchRepository.save(product);
        variant.setId(null);
        variant.setIsMaster(true);
        variant.setProduct(product);
        variantRepository.save(variant);
        variantSearchRepository.save(variant);
        
        product.setVariant(variant);
        //return ResponseEntity.created(new URI("/api/products/" + product.getId())).build();
        return new ResponseEntity<>(
                product,
                HttpStatus.OK);
    }

    /**
     * PUT  /products -> Updates an existing product.
     */
    @RequestMapping(value = "/products",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Product product) throws URISyntaxException {
    	Variant variant = product.getVariant();
        log.debug("REST request to update Product : {}", product);
        log.debug("REST request to update Variant : {}", variant);
        if(product.getId() == null || variant.getId() == null){
        	log.error(" Product or Variant ID is null.");
        	//return ResponseEntity.badRequest().header("Failure", "Product or Variant does not have an ID").build();
        	return new ResponseEntity("Product or Variant does not have an ID", HttpStatus.BAD_REQUEST);
        }

        Set optionTypes = new HashSet<OptionType>();
        for(Long id: product.getOptionTypeIds()){
        	Optional.ofNullable(optionTypeRepository.findOne(id))
        	.map(optionType -> optionTypes.add(optionType));
        }
        Set taxons = new HashSet<Taxon>();
        for(Long id: product.getTaxonIds()){
        	Optional.ofNullable(taxonRepository.findOne(id))
        	.map(taxon -> taxons.add(taxon));
        }
        product.setOptionTypes(optionTypes);
        product.setTaxons(taxons);
        productRepository.save(product);
        productSearchRepository.save(product);
        
        variant.setProduct(product);
        variantRepository.save(variant);
        variantSearchRepository.save(variant);

        return ResponseEntity.ok().build();
    }

    /**
     * GET  /products -> get all the products.
     */
    @RequestMapping(value = "/products",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProductDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<ProductDTO> page = productRepository.findAllWithMasterVariant(PaginationUtil.generatePageRequest(offset, limit, Direction.DESC, "id"));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/products", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /products/:id -> get the "id" product.
     */
    @RequestMapping(value = "/products/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Product> get(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        return Optional.ofNullable(productRepository.getProductWithMasterVariant(id))
            .map(product -> new ResponseEntity<>(
                product,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /products/:id -> delete the "id" product.
     */
    @RequestMapping(value = "/products/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
//        productRepository.delete(id);
//        productSearchRepository.delete(id);
        Optional.ofNullable(productRepository.findOne(id))
         	.map(product -> {
         		product.setDeletedDate(DateTime.now());
         		productRepository.save(product);
         		return product;
         	});
    }

    /**
     * SEARCH  /_search/products/:query -> search for the product corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/products/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Product> search(@PathVariable String query) {
        return StreamSupport
            .stream(productSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
