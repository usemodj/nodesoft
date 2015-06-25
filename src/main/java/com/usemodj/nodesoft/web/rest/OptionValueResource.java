package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.OptionValue;
import com.usemodj.nodesoft.repository.OptionValueRepository;
import com.usemodj.nodesoft.repository.search.OptionValueSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 * REST controller for managing OptionValue.
 */
@RestController
@RequestMapping("/api")
public class OptionValueResource {

    private final Logger log = LoggerFactory.getLogger(OptionValueResource.class);

    @Inject
    private OptionValueRepository optionValueRepository;

    @Inject
    private OptionValueSearchRepository optionValueSearchRepository;

    /**
     * POST  /optionValues -> Create a new optionValue.
     */
    @RequestMapping(value = "/optionValues",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody OptionValue optionValue) throws URISyntaxException {
        log.debug("REST request to save OptionValue : {}", optionValue);
        if (optionValue.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new optionValue cannot already have an ID").build();
        }
        optionValueRepository.save(optionValue);
        optionValueSearchRepository.save(optionValue);
        return ResponseEntity.created(new URI("/api/optionValues/" + optionValue.getId())).build();
    }

    /**
     * PUT  /optionValues -> Updates an existing optionValue.
     */
    @RequestMapping(value = "/optionValues",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody OptionValue optionValue) throws URISyntaxException {
        log.debug("REST request to update OptionValue : {}", optionValue);
        if (optionValue.getId() == null) {
            return create(optionValue);
        }
        optionValueRepository.save(optionValue);
        optionValueSearchRepository.save(optionValue);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /optionValues -> get all the optionValues.
     */
    @RequestMapping(value = "/optionValues",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OptionValue>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<OptionValue> page = optionValueRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/optionValues", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /optionValues/:id -> get the "id" optionValue.
     */
    @RequestMapping(value = "/optionValues/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OptionValue> get(@PathVariable Long id) {
        log.debug("REST request to get OptionValue : {}", id);
        return Optional.ofNullable(optionValueRepository.findOne(id))
            .map(optionValue -> new ResponseEntity<>(
                optionValue,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /optionValues/:id -> delete the "id" optionValue.
     */
    @RequestMapping(value = "/optionValues/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete OptionValue : {}", id);
        optionValueRepository.delete(id);
        optionValueSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/optionValues/:query -> search for the optionValue corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/optionValues/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OptionValue> search(@PathVariable String query) {
        return StreamSupport
            .stream(optionValueSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
