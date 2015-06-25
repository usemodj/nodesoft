package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Taxon;
import com.usemodj.nodesoft.repository.TaxonRepository;
import com.usemodj.nodesoft.repository.search.TaxonSearchRepository;
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
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Taxon.
 */
@RestController
@RequestMapping("/api")
public class TaxonResource {

    private final Logger log = LoggerFactory.getLogger(TaxonResource.class);

    @Inject
    private TaxonRepository taxonRepository;

    @Inject
    private TaxonSearchRepository taxonSearchRepository;

    /**
     * POST  /taxons -> Create a new taxon.
     */
    @RequestMapping(value = "/taxons",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Taxon taxon) throws URISyntaxException {
        log.debug("REST request to save Taxon : {}", taxon);
        if (taxon.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new taxon cannot already have an ID").build();
        }
        taxonRepository.save(taxon);
        taxonSearchRepository.save(taxon);
        return ResponseEntity.created(new URI("/api/taxons/" + taxon.getId())).build();
    }

    /**
     * PUT  /taxons -> Updates an existing taxon.
     */
    @RequestMapping(value = "/taxons",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Taxon taxon) throws URISyntaxException {
        log.debug("REST request to update Taxon : {}", taxon);
        if (taxon.getId() == null) {
            return create(taxon);
        }
        taxonRepository.save(taxon);
        taxonSearchRepository.save(taxon);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /taxons -> get all the taxons.
     */
    @RequestMapping(value = "/taxons",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Taxon>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Taxon> page = taxonRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taxons", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /taxons/:id -> get the "id" taxon.
     */
    @RequestMapping(value = "/taxons/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Taxon> get(@PathVariable Long id) {
        log.debug("REST request to get Taxon : {}", id);
        return Optional.ofNullable(taxonRepository.findOne(id))
            .map(taxon -> new ResponseEntity<>(
                taxon,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /taxons/:id -> delete the "id" taxon.
     */
    @RequestMapping(value = "/taxons/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Taxon : {}", id);
        taxonRepository.delete(id);
        taxonSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/taxons/:query -> search for the taxon corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/taxons/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Taxon> search(@PathVariable String query) {
        return StreamSupport
            .stream(taxonSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
