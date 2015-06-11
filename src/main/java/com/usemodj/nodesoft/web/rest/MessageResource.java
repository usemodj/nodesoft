package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.repository.MessageRepository;
import com.usemodj.nodesoft.repository.search.MessageSearchRepository;
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
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private MessageSearchRepository messageSearchRepository;

    /**
     * POST  /messages -> Create a new message.
     */
    @RequestMapping(value = "/messages",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to save Message : {}", message);
        if (message.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new message cannot already have an ID").build();
        }
        messageRepository.save(message);
        messageSearchRepository.save(message);
        return ResponseEntity.created(new URI("/api/messages/" + message.getId())).build();
    }

    /**
     * PUT  /messages -> Updates an existing message.
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to update Message : {}", message);
        if (message.getId() == null) {
            return create(message);
        }
        messageRepository.save(message);
        messageSearchRepository.save(message);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /messages -> get all the messages.
     */
    @RequestMapping(value = "/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Message>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Message> page = messageRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /messages/:id -> get the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> get(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        return Optional.ofNullable(messageRepository.findOne(id))
            .map(message -> new ResponseEntity<>(
                message,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /messages/:id -> delete the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageRepository.delete(id);
        messageSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/messages/:query -> search for the message corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/messages/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Message> search(@PathVariable String query) {
        return StreamSupport
            .stream(messageSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
