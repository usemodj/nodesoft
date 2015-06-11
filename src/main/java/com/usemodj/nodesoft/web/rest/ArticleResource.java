package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Article;
import com.usemodj.nodesoft.repository.ArticleRepository;
import com.usemodj.nodesoft.repository.search.ArticleSearchRepository;
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
 * REST controller for managing Article.
 */
@RestController
@RequestMapping("/api")
public class ArticleResource {

    private final Logger log = LoggerFactory.getLogger(ArticleResource.class);

    @Inject
    private ArticleRepository articleRepository;

    @Inject
    private ArticleSearchRepository articleSearchRepository;

    /**
     * POST  /articles -> Create a new article.
     */
    @RequestMapping(value = "/articles",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Article article) throws URISyntaxException {
        log.debug("REST request to save Article : {}", article);
        if (article.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new article cannot already have an ID").build();
        }
        articleRepository.save(article);
        articleSearchRepository.save(article);
        return ResponseEntity.created(new URI("/api/articles/" + article.getId())).build();
    }

    /**
     * PUT  /articles -> Updates an existing article.
     */
    @RequestMapping(value = "/articles",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Article article) throws URISyntaxException {
        log.debug("REST request to update Article : {}", article);
        if (article.getId() == null) {
            return create(article);
        }
        articleRepository.save(article);
        articleSearchRepository.save(article);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /articles -> get all the articles.
     */
    @RequestMapping(value = "/articles",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Article>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Article> page = articleRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/articles", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /articles/:id -> get the "id" article.
     */
    @RequestMapping(value = "/articles/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Article> get(@PathVariable Long id) {
        log.debug("REST request to get Article : {}", id);
        return Optional.ofNullable(articleRepository.findOne(id))
            .map(article -> new ResponseEntity<>(
                article,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /articles/:id -> delete the "id" article.
     */
    @RequestMapping(value = "/articles/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Article : {}", id);
        articleRepository.delete(id);
        articleSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/articles/:query -> search for the article corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/articles/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Article> search(@PathVariable String query) {
        return StreamSupport
            .stream(articleSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
