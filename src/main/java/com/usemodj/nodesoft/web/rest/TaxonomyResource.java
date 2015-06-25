package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.OptionType;
import com.usemodj.nodesoft.domain.Taxon;
import com.usemodj.nodesoft.domain.Taxonomy;
import com.usemodj.nodesoft.repository.TaxonRepository;
import com.usemodj.nodesoft.repository.TaxonomyRepository;
import com.usemodj.nodesoft.repository.search.TaxonomySearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Taxonomy.
 */
@RestController
@RequestMapping("/api")
public class TaxonomyResource {

    private final Logger log = LoggerFactory.getLogger(TaxonomyResource.class);

    @Inject
    private TaxonomyRepository taxonomyRepository;

    @Inject
    private TaxonomySearchRepository taxonomySearchRepository;

    @Inject
    private TaxonRepository taxonRepository;
    
    /**
     * POST  /taxonomys -> Create a new taxonomy.
     */
    @RequestMapping(value = "/taxonomys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Taxonomy taxonomy) throws URISyntaxException {
        log.debug("REST request to save Taxonomy : {}", taxonomy);
        if (taxonomy.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new taxonomy cannot already have an ID").build();
        }
        taxonomyRepository.save(taxonomy);
        taxonomySearchRepository.save(taxonomy);
        
        Taxon taxon = new Taxon(taxonomy.getName(), taxonomy);
        taxonRepository.save(taxon);
        return ResponseEntity.created(new URI("/api/taxonomys/" + taxonomy.getId())).build();
    }

    /**
     * PUT  /taxonomys -> Updates an existing taxonomy.
     */
    @RequestMapping(value = "/taxonomys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Taxonomy taxonomy) throws URISyntaxException {
        log.debug("REST request to update Taxonomy : {}", taxonomy);
        if (taxonomy.getId() == null) {
            return create(taxonomy);
        }
        taxonomyRepository.save(taxonomy);
        taxonomySearchRepository.save(taxonomy);
        
        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /taxonomys -> Updates an existing taxonomy.
     */
    @RequestMapping(value = "/taxonomys/updateTaxons",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateTaxons(@RequestBody Taxonomy taxonomy) throws URISyntaxException {
        log.debug("REST request to update Taxonomy : {}", taxonomy);
        
        taxonRepository.deleteAllByTaxonomyId(taxonomy.getId());
        if(taxonomy.getTaxons() != null && !taxonomy.getTaxons().isEmpty()){
        	Taxon taxon = taxonomy.getTaxons().toArray(new Taxon[0])[0];

        	saveChildTaxons(taxon, null, 1, taxonomyRepository.findOne(taxonomy.getId()));
        }
        return ResponseEntity.ok().build();
    }

    Integer saveChildTaxons(Taxon taxon, Taxon parent, Integer position, Taxonomy taxonomy){
    	if(taxon == null) return position;
//    	if(parent !=null) {
//    		parent.addChild(taxon);
//    	}
    	taxon.setPosition(position);
    	taxon.setTaxonomy(taxonomy);
    	taxon.setParent(parent);
    	taxonRepository.create(taxon);
    	log.debug(" Taxon : {}, parent: {}", taxon, taxon.getParent());
    	for(Taxon child: taxon.getChildren()){
    		position = saveChildTaxons(child, taxon, ++position, taxonomy);
    		
    	}
    	return position;
    }
    
    /**
     * GET  /taxonomys -> get all the taxonomys.
     */
    @RequestMapping(value = "/taxonomys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Taxonomy>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Taxonomy> page = taxonomyRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taxonomys", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /taxonomys/:id -> get the "id" taxonomy.
     */
    @RequestMapping(value = "/taxonomys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Taxonomy> get(@PathVariable Long id) {
        log.debug("REST request to get Taxonomy : {}", id);
        return Optional.ofNullable(taxonomyRepository.findOne(id))
            .map(taxonomy -> new ResponseEntity<>(
                taxonomy,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /taxonomys/:id -> delete the "id" taxonomy.
     */
    @RequestMapping(value = "/taxonomys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Taxonomy : {}", id);
        taxonomyRepository.delete(id);
        taxonomySearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/taxonomys/:query -> search for the taxonomy corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/taxonomys/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Taxonomy> search(@PathVariable String query) {
        return StreamSupport
            .stream(taxonomySearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * GET  /taxonomys/position/:entry -> Update the position of Taxonomys.
     * @throws JSONException 
     */
    @RequestMapping(value = "/taxonomys/position/{entry}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updatePosition(@PathVariable String entry) throws URISyntaxException {
        log.debug("REST request to update the position of taxonomys : {}", entry);
        String[] ids = StringUtils.delimitedListToStringArray(entry, ",");
        int i = 1;
        for(String id : ids){
        	Taxonomy taxonomy = taxonomyRepository.findOne(Long.valueOf(id));
        	if(taxonomy == null) continue;
        	taxonomy.setPosition(i++);
 			taxonomyRepository.save(taxonomy);
        }
        return ResponseEntity.created(new URI("/api/taxonomys/position")).build();
    }

}
