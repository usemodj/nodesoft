package com.usemodj.nodesoft.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.OptionType;
import com.usemodj.nodesoft.domain.OptionValue;
import com.usemodj.nodesoft.domain.Variant;
import com.usemodj.nodesoft.repository.OptionValueRepository;
import com.usemodj.nodesoft.repository.ProductRepository;
import com.usemodj.nodesoft.repository.VariantRepository;
import com.usemodj.nodesoft.repository.search.VariantSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Variant.
 */
@RestController
@RequestMapping("/api")
public class VariantResource {

    private final Logger log = LoggerFactory.getLogger(VariantResource.class);

    @Inject
    private VariantRepository variantRepository;

    //@Inject
    //private VariantSearchRepository variantSearchRepository;

    @Inject
    private OptionValueRepository optionValueRepository;
    
    @Inject
    private ProductRepository productRepository;
    
    /**
     * POST  /variants -> Create a new variant.
     */
    @RequestMapping(value = "/variants",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Variant variant) throws URISyntaxException {
        log.debug("REST request to save Variant : {}", variant);
        log.debug("REST request to save Variant optionValueMap: {}", variant.getOptionValueMap());
        if (variant.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new variant cannot already have an ID").build();
        }
        Set optionValues = new HashSet<OptionValue>();
        Map<Long, Long> map = variant.getOptionValueMap();
        for(Long key: (Long[])map.keySet().toArray(new Long[0])){
        	log.debug(" OptionValueMap {}:{}", key, map.get(key));
        	Optional.ofNullable(optionValueRepository.findOne( map.get(key)))
        	.map(optionValue -> optionValues.add(optionValue));
        }
        variant.setOptionValues(optionValues);
        variant.setIsMaster(false);
        variant.setProduct(productRepository.findOne(variant.getProductId()));
        variantRepository.save(variant);
        //variantSearchRepository.save(variant);
        return ResponseEntity.created(new URI("/api/variants/" + variant.getId())).build();
    }

    /**
     * PUT  /variants -> Updates an existing variant.
     */
    @RequestMapping(value = "/variants",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Variant variant) throws URISyntaxException {
        log.debug("REST request to update Variant : {}", variant);
        log.debug("REST request to save Variant optionValueMap: {}", variant.getOptionValueMap());
        if (variant.getId() == null) {
            return create(variant);
        }
        
        Set optionValues = new HashSet<OptionValue>();
        Map<Long, Long> map = variant.getOptionValueMap();
        for(Long key: (Long[])map.keySet().toArray(new Long[0])){
        	log.debug(" OptionValueMap {}:{}", key, map.get(key));
        	Optional.ofNullable(optionValueRepository.findOne( map.get(key)))
        	.map(optionValue -> optionValues.add(optionValue));
        }
        variant.setOptionValues(optionValues);
        variantRepository.save(variant);
        //variantSearchRepository.save(variant);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /variants -> get all the variants.
     */
    @RequestMapping(value = "/variants",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Variant>> getAll(@RequestParam("productId") Long productId, @RequestParam("deleted") Boolean deleted) {
        List<Variant> variants = variantRepository.findAllByProductIdAndIsMasterIsFalse(productId, deleted);
        return new ResponseEntity<>(variants, HttpStatus.OK);
    }

    /**
     * GET  /variants/:id -> get the "id" variant.
     */
    @RequestMapping(value = "/variants/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Variant> get(@PathVariable Long id) {
        log.debug("REST request to get Variant : {}", id);
        return Optional.ofNullable(variantRepository.findOneWithEagerRelationships(id))
            .map(variant -> new ResponseEntity<>(
                variant,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /variants/:id -> delete the "id" variant.
     */
    @RequestMapping(value = "/variants/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Variant : {}", id);
        //variantRepository.delete(id);
        //variantSearchRepository.delete(id);
        Optional.ofNullable(variantRepository.findOne(id))
        .map(variant -> {
        	variant.setDeletedDate(DateTime.now());
        	variantRepository.save(variant);
        	return variant;
        });
    }

    /**
     * GET  /variants/position/:entry -> update the position of variants.
     */
    @RequestMapping(value = "/variants/position/{entry}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updatePosition(@PathVariable String entry) throws URISyntaxException {
        log.debug("REST request to update the position of Variants : {}", entry);
        String[] ids = StringUtils.delimitedListToStringArray(entry, ",");
        int i = 1;
        for(String id : ids){
        	Variant variant = variantRepository.findOne(Long.valueOf(id));
        	if(variant == null) continue;
        	variant.setPosition(i++);
        	variantRepository.save(variant);
        }
        return ResponseEntity.created(new URI("/api/variants/position")).build();
    }
    
    /**
     * GET  /variants/active/:id -> active the variant.
     */
    @RequestMapping(value = "/variants/active/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> active(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to active the Variant : {}", id);

        variantRepository.setActive(id);
        return ResponseEntity.created(new URI("/api/variants/active")).build();
    }

//    /**
//     * SEARCH  /_search/variants/:query -> search for the variant corresponding
//     * to the query.
//     */
//    @RequestMapping(value = "/_search/variants/{query}",
//        method = RequestMethod.GET,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public List<Variant> search(@PathVariable String query) {
//        return StreamSupport
//            .stream(variantSearchRepository.search(queryString(query)).spliterator(), false)
//            .collect(Collectors.toList());
//    }
}
