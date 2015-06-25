package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.repository.ForumRepository;
import com.usemodj.nodesoft.repository.search.ForumSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
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
 * REST controller for managing Forum.
 */
@RestController
@RequestMapping("/api")
public class ForumResource {

    private final Logger log = LoggerFactory.getLogger(ForumResource.class);

    @Inject
    private ForumRepository forumRepository;

    @Inject
    private ForumSearchRepository forumSearchRepository;

    /**
     * POST  /forums -> Create a new forum.
     */
    @RequestMapping(value = "/forums",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Forum forum) throws URISyntaxException {
        log.debug("REST request to save Forum : {}", forum);
        if (forum.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new forum cannot already have an ID").build();
        }
        forumRepository.save(forum);
        forumSearchRepository.save(forum);
        return ResponseEntity.created(new URI("/api/forums/" + forum.getId())).build();
    }

    /**
     * PUT  /forums -> Updates an existing forum.
     */
    @RequestMapping(value = "/forums",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Forum forum) throws URISyntaxException {
        log.debug("REST request to update Forum : {}", forum);
        if (forum.getId() == null) {
            return create(forum);
        }
        forumRepository.save(forum);
        forumSearchRepository.save(forum);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /forums -> get all the forums.
     */
    @RequestMapping(value = "/forums",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Forum>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Forum> page = forumRepository.findAll(PaginationUtil.generatePageRequest(offset, limit, Direction.ASC, "id"));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/forums", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /forums/:id -> get the "id" forum.
     */
    @RequestMapping(value = "/forums/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Forum> get(@PathVariable Long id) {
        log.debug("REST request to get Forum : {}", id);
        return Optional.ofNullable(forumRepository.findOne(id))
            .map(forum ->  new ResponseEntity<>(forum, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /forums/:id -> delete the "id" forum.
     */
    @RequestMapping(value = "/forums/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Forum : {}", id);
        
        forumRepository.deleteWithTopics(id);
        //forumSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/forums/:query -> search for the forum corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/forums/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Forum> search(@PathVariable String query) {
//        return StreamSupport
//            .stream(forumSearchRepository.search(queryString(query)).spliterator(), false)
//            .collect(Collectors.toList());
        return StreamSupport
                .stream(forumSearchRepository.search(QueryBuilders.fuzzyLikeThisQuery().likeText(query)).spliterator(), false)
                .collect(Collectors.toList());
    }
}
