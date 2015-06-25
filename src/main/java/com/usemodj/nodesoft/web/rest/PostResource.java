package com.usemodj.nodesoft.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.validation.Valid;

import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.ForumRepository;
import com.usemodj.nodesoft.repository.PostRepository;
import com.usemodj.nodesoft.repository.TopicRepository;
import com.usemodj.nodesoft.repository.search.PostSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Post.
 */
@RestController
@RequestMapping("/api")
public class PostResource {

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    @Inject
    private PostRepository postRepository;
    @Inject
    private TopicRepository topicRepository;
    @Inject
    private ForumRepository forumRepository;
    @Inject
    private AssetRepository assetRepository;

    @Inject
    private PostSearchRepository postSearchRepository;

    /**
     * POST  /posts -> Create a new post.
     */
    @RequestMapping(value = "/posts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to save Post : {}", post);
        if (post.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new post cannot already have an ID").build();
        }
        postRepository.save(post);
        postSearchRepository.save(post);
        return ResponseEntity.created(new URI("/api/posts/" + post.getId())).build();
    }

    /**
     * PUT  /posts -> Updates an existing post.
     */
    @RequestMapping(value = "/posts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to update Post : {}", post);
        if (post.getId() == null) {
            return create(post);
        }
        postRepository.save(post);
        postSearchRepository.save(post);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /posts -> get all the posts.
     */
    @RequestMapping(value = "/posts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Post>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Post> page = postRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/posts", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /posts/:id -> get the "id" post.
     */
    @RequestMapping(value = "/posts/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Post> get(@PathVariable Long id) {
        log.debug("REST request to get Post : {}", id);
        return Optional.ofNullable(postRepository.findOne(id))
            .map(post -> new ResponseEntity<>(
                post,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /posts/:id -> delete the "id" post.
     */
    @RequestMapping(value = "/posts/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Post : {}", id);
        Optional.ofNullable((Post)postRepository.findOne(id))
         	.map(post -> {
         		Topic topic = post.getTopic();
         		if( post.equals( topic.getLastPost())){
         			topic.setLastPost(null);
         		}
         		int replies = Integer.max(topic.getReplies() - 1, 0);
         		topic.setReplies(replies);
     			topicRepository.save(topic);
         		Forum forum  = topic.getForum();
         		int postCount = Integer.max(forum.getPostCount() - 1, 0);
         		forum.setPostCount(postCount);
         		forumRepository.save(forum);
         		
                postRepository.deleteWithAssets(id);
                                
                assetRepository.deleteWithFile(id, "Post");
                
                return post;
         	});
        
    }

    /**
     * SEARCH  /_search/posts/:query -> search for the post corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/posts/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Post> search(@PathVariable String query) {
        return StreamSupport
            .stream(postSearchRepository.search(QueryBuilders.fuzzyLikeThisQuery().likeText(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
