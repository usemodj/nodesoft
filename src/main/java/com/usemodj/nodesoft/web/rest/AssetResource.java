package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Asset.
 */
@RestController
@RequestMapping("/api")
public class AssetResource {

    private final Logger log = LoggerFactory.getLogger(AssetResource.class);

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetSearchRepository assetSearchRepository;
    
    @Inject
    private Environment env;

    /**
     * POST  /assets -> Create a new asset.
     */
    @RequestMapping(value = "/assets",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Asset asset) throws URISyntaxException {
        log.debug("REST request to save Asset : {}", asset);
        if (asset.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new asset cannot already have an ID").build();
        }
        assetRepository.save(asset);
        assetSearchRepository.save(asset);
        return ResponseEntity.created(new URI("/api/assets/" + asset.getId())).build();
    }

    /**
     * PUT  /assets -> Updates an existing asset.
     */
    @RequestMapping(value = "/assets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Asset asset) throws URISyntaxException {
        log.debug("REST request to update Asset : {}", asset);
        if (asset.getId() == null) {
            return create(asset);
        }
        assetRepository.save(asset);
        assetSearchRepository.save(asset);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /assets -> get all the assets.
     */
    @RequestMapping(value = "/assets",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Asset>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Asset> page = assetRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/assets", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /assets/:id -> get the "id" asset.
     */
    @RequestMapping(value = "/assets/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> get(@PathVariable Long id) {
        log.debug("REST request to get Asset : {}", id);
        return Optional.ofNullable(assetRepository.findOne(id))
            .map(asset -> new ResponseEntity<>(
                asset,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /assets/:id -> delete the "id" asset.
     */
    @RequestMapping(value = "/assets/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Asset> delete(@PathVariable Long id) {
        log.debug("REST request to delete Asset : {}", id);
        String uploadPath = env.getProperty("uploadPath");
        return Optional.ofNullable(assetRepository.findOne(id))
                .map(asset -> {
                	File file = new File(uploadPath + asset.getFilePath());
                	file.delete();
                	log.info("file deleted: {}", file.getAbsolutePath());
                    assetRepository.delete(id);
                    assetSearchRepository.delete(id);
                	return new ResponseEntity<>(asset, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        
    }

    /**
     * SEARCH  /_search/assets/:query -> search for the asset corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/assets/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Asset> search(@PathVariable String query) {
        return StreamSupport
            .stream(assetSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
