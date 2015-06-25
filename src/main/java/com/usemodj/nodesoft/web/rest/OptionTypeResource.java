package com.usemodj.nodesoft.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import net.minidev.json.JSONValue;

import org.elasticsearch.action.search.SearchRequest;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.usemodj.nodesoft.repository.OptionTypeRepository;
import com.usemodj.nodesoft.repository.OptionValueRepository;
import com.usemodj.nodesoft.repository.search.OptionTypeSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing OptionType.
 */
@RestController
@RequestMapping("/api")
public class OptionTypeResource {

    private final Logger log = LoggerFactory.getLogger(OptionTypeResource.class);

    @Inject
    private OptionTypeRepository optionTypeRepository;

    @Inject
    private OptionTypeSearchRepository optionTypeSearchRepository;
    
    @Inject
    private OptionValueRepository optionValueRepository;

    /**
     * POST  /optionTypes -> Create a new optionType.
     */
    @RequestMapping(value = "/optionTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody OptionType optionType) throws URISyntaxException {
        log.debug("REST request to save OptionType : {}", optionType);
        if (optionType.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new optionType cannot already have an ID").build();
        }
        optionTypeRepository.save(optionType);
        optionTypeSearchRepository.save(optionType);
        return ResponseEntity.created(new URI("/api/optionTypes/" + optionType.getId())).build();
    }

    /**
     * PUT  /optionTypes -> Updates an existing optionType.
     */
    @RequestMapping(value = "/optionTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody OptionType optionType) throws URISyntaxException {
        log.debug("REST request to update OptionType : {}", optionType);
        if (optionType.getId() == null) {
            return create(optionType);
        }
        optionTypeRepository.save(optionType);
        optionTypeSearchRepository.save(optionType);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /optionTypes -> get all the optionTypes.
     */
    @RequestMapping(value = "/optionTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OptionType>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<OptionType> page = optionTypeRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/optionTypes", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /optionTypes/:id -> get the "id" optionType.
     */
    @RequestMapping(value = "/optionTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OptionType> get(@PathVariable Long id) {
        log.debug("REST request to get OptionType : {}", id);
        return Optional.ofNullable(optionTypeRepository.findOne(id))
            .map(optionType -> new ResponseEntity<>(
                optionType,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /optionTypes/:id -> delete the "id" optionType.
     */
    @RequestMapping(value = "/optionTypes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete OptionType : {}", id);
        optionValueRepository.deleteAllByOptionTypeId(id);
        optionTypeRepository.delete(id);
        optionTypeSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/optionTypes/:query -> search for the optionType corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/optionTypes/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OptionType> search(@PathVariable String query) {
        return StreamSupport
            .stream(optionTypeSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * GET  /optionTypes/position/:entry -> Update the position of optionTypes.
     * @throws JSONException 
     */
    @RequestMapping(value = "/optionTypes/position/{entry}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updatePosition(@PathVariable String entry) throws URISyntaxException {
        log.debug("REST request to update the position of OptionTypes : {}", entry);
        String[] ids = StringUtils.delimitedListToStringArray(entry, ",");
        int i = 1;
        for(String id : ids){
        	OptionType optionType = optionTypeRepository.findOne(Long.valueOf(id));
        	if(optionType == null) continue;
 			optionType.setPosition(i++);
			optionTypeRepository.save(optionType);
        }
        return ResponseEntity.created(new URI("/api/optionTypes/position")).build();
    }
    
    /**
     * POST  /optionTypes -> Update the position of optionTypes.
     * @throws JSONException 
     */
    @RequestMapping(value = "/optionTypes/changeValues",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> changeOptionValues(@RequestBody OptionType optionType) throws URISyntaxException {
        log.debug("REST request to change the OptionValues of OptionType : {}", optionType);
        
        optionValueRepository.deleteAllByOptionTypeId(optionType.getId());
        int i = 1;
        for(OptionValue optionValue: optionType.getOptionValues()){
        	optionValue.setId(null);;
        	optionValue.setPosition(i++);
			optionValueRepository.save(optionValue);
        }
        return ResponseEntity.created(new URI("/api/optionTypes/changeValues")).build();
    }

}
