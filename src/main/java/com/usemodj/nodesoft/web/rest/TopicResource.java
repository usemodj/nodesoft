package com.usemodj.nodesoft.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
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
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.domain.User;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.ForumRepository;
import com.usemodj.nodesoft.repository.PostRepository;
import com.usemodj.nodesoft.repository.TopicRepository;
import com.usemodj.nodesoft.repository.search.PostSearchRepository;
import com.usemodj.nodesoft.repository.search.TopicSearchRepository;
import com.usemodj.nodesoft.service.UserService;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Topic.
 */
@RestController
@RequestMapping("/api")
public class TopicResource {

    private final Logger log = LoggerFactory.getLogger(TopicResource.class);

    @Inject
    private Environment env;
    
    @Inject
    private TopicRepository topicRepository;

    @Inject
    private TopicSearchRepository topicSearchRepository;

    @Inject 
    private UserService userService;
    @Inject
    private ForumRepository forumRepository;
    @Inject
    private PostRepository postRepository;
    @Inject
    private PostSearchRepository postSearchRepository;
    
    @Inject
    private AssetRepository assetRepository;
    
    /**
     * POST  /topics -> Create a new topic.
     */
    @RequestMapping(value = "/topics/upload",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> createTopic(@RequestParam("topic") String topicStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Topic : {}", topicStr);
        Object obj = JSONValue.parse( topicStr);
    	JSONObject jTopic = (JSONObject)obj;
    	
    	if (jTopic.get("id") != null) {
            return ResponseEntity.badRequest().header("Failure", "A new topic cannot already have an ID").build();
        }
    	Long forumId = null;
		try {
			forumId = Long.valueOf((String)jTopic.get("forumId"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Forum forum = forumRepository.findOne(forumId);
    	
    	Topic topic = new Topic((String)jTopic.get("name"), (Boolean)jTopic.get("locked"), (Boolean)jTopic.get("sticky"),
    			user, forum);
        topicRepository.save(topic);
        topicSearchRepository.save(topic);
        JSONObject jPost = (JSONObject)jTopic.get("post");
        log.debug(" post json parse: {}", jPost);
    	Post post = new Post((String)jTopic.get("name"), (String)jPost.get("content"), true,
    			user, forum, topic);
    	postRepository.save(post);
        postSearchRepository.save(post);
        Integer topicCount = forum.getTopicCount();
        if(topicCount == null) topicCount = 0;
        Integer postCount = forum.getPostCount();
        if(postCount == null) postCount = 0;
        forum.setTopicCount( topicCount + 1);
        forum.setPostCount( postCount + 1);
        forum.setLastModifiedDate(DateTime.now());
        forum.setLastTopic(topic);
        forumRepository.save( forum);

        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "post/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(post.getId(), "Post", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "post/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        return ResponseEntity.created(new URI("/api/topics/" + topic.getId())).build();
    }

    /**
     * POST  /topics -> Create a new topic.
     */
    @RequestMapping(value = "/topics",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Topic topic) throws URISyntaxException {
        log.debug("REST request to save Topic : {}", topic);
        if (topic.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new topic cannot already have an ID").build();
        }
        topicRepository.save(topic);
        topicSearchRepository.save(topic);
        return ResponseEntity.created(new URI("/api/topics/" + topic.getId())).build();
    }

    /**
     * POST  /topics -> Create a new topic.
     */
    @RequestMapping(value = "/topics/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateTopic(@RequestParam("topic") String topicStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Topic : {}", topicStr);
        Object obj = JSONValue.parse( topicStr);
    	JSONObject jTopic = (JSONObject)obj;
    	
    	Long topicId = null;
    	try {
    		topicId = Long.valueOf((Integer)jTopic.get("id"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	if(topicId == null)
			return createTopic(topicStr, files, request);

    	
    	Long forumId = null;
		try {
			forumId = Long.valueOf((String)jTopic.get("forumId"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Optional.ofNullable((Topic)topicRepository.findOne(topicId))
				.map(topic -> {
					topic.setName((String)jTopic.get("name"));
					topic.setLocked((Boolean)jTopic.get("locked"));
					topic.setSticky((Boolean)jTopic.get("sticky"));
					topic.setUser(user);
			        topicRepository.save(topic);
			        topicSearchRepository.save(topic);
			        return topic;
				});
    	
		JSONObject jPost = (JSONObject)jTopic.get("post");
		Long postId = Long.valueOf((Integer)jPost.get("id"));
		Optional.ofNullable((Post)postRepository.findOne(postId))
    			.map(post -> {
    				post.setName((String)jTopic.get("name"));
    				post.setContent((String)jPost.get("content"));
    				post.setUser(user);
    		    	postRepository.save(post);
    		        postSearchRepository.save(post);
    		        return post;
    			});

        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "post/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(postId, "Post", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "post/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        //return ResponseEntity.created(new URI("/api/topics/" + topicId)).build();
        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /topics -> Updates an existing topic.
     */
    @RequestMapping(value = "/topics",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Topic topic) throws URISyntaxException {
        log.debug("REST request to update Topic : {}", topic);
        if (topic.getId() == null) {
            return create(topic);
        }
        topicRepository.save(topic);
        topicSearchRepository.save(topic);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /topics -> get all the topics.
     */
    @RequestMapping(value = "/topics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Topic>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Topic> page = topicRepository.findAll(PaginationUtil.generatePageRequest(offset, limit, Direction.DESC, "id"));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/topics", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * GET  /topics -> get all the forum topics.
     */
    @RequestMapping(value = "/forum/topics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<String, List<Topic>>> getForumTopics(@RequestParam("forum_id") Long forumId, @RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
    	List<Topic> stickyTopics = topicRepository.findAllByForumIdAndSticky(forumId);
    	
        Page<Topic> page = topicRepository.findTopicsByForumId(forumId, PaginationUtil.generatePageRequest(offset, limit, Direction.DESC, "id"));
        Map<String, List<Topic>> content = new HashMap<String, List<Topic>>();
        content.put("topics", page.getContent());
        content.put("stickyTopics", stickyTopics);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/forum/topics", offset, limit, "forumId="+ forumId);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    /**
     * GET  /topics/:id -> get the "id" topic.
     */
    @RequestMapping(value = "/topics/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Topic> get(@PathVariable Long id) {
        log.debug("REST request to get Topic : {}", id);
        
        return Optional.ofNullable(topicRepository.findOneWithPosts(id))
            .map(topic -> {
            	int views = topic.getViews().intValue();
            	topic.setViews(views + 1);
            	topicRepository.save(topic);
            	
            	return new ResponseEntity<>(topic,HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /topics/:id/root -> get the "id" topic.
     */
    @RequestMapping(value = "/topics/{id}/root",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Topic> getRoot(@PathVariable Long id) {
        log.debug("REST request to get Topic : {}", id);
        
        return Optional.ofNullable((Topic)topicRepository.findOneWithRootPost(id))
            .map(topic -> new ResponseEntity<>(
                topic,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /topics/:id -> delete the "id" topic.
     */
    @RequestMapping(value = "/topics/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Topic : {}", id);
        topicRepository.deleteWithPosts(id);
        //topicRepository.delete(id);
        //topicSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/topics/:query -> search for the topic corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/topics/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Topic> search(@PathVariable String query) {
//        return StreamSupport
//            .stream(topicSearchRepository.search(queryString(query)).spliterator(), false)
//            .collect(Collectors.toList());
        return StreamSupport
                .stream(topicSearchRepository.search(QueryBuilders.fuzzyLikeThisQuery().likeText(query)).spliterator(), false)
                .collect(Collectors.toList());
    }
    
    /**
     * POST  /topics/reply -> Create a new post.
     */
    @RequestMapping(value = "/topics/post",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> createPost(@RequestParam("post") String postStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Post : {}", postStr);
        Object obj = JSONValue.parse( postStr);
    	JSONObject jPost = (JSONObject)obj;
    	
    	if (jPost.get("id") != null) {
            return ResponseEntity.badRequest().header("Failure", "A new post cannot already have an ID").build();
        }
    	Long topicId = null;
		try {
			topicId = Long.valueOf((String)jPost.get("topicId"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Long postId = (Long) Optional.ofNullable((Topic)topicRepository.findOne(topicId))
			.map(topic -> {
		        Forum forum = topic.getForum();
		    	Post post = new Post((String)jPost.get("name"), (String)jPost.get("content"), false,
		    			user, forum, topic);
		    	postRepository.save(post);
		        postSearchRepository.save(post);
		        
		        Integer replies = topic.getReplies();
		        if(replies == null) replies = 0;
		        topic.setReplies(replies + 1);
		        topic.setLastPost(post);
		        topicRepository.save(topic);
		        
		        Integer postCount = forum.getPostCount();
		        if(postCount == null) postCount = 0;
		        forum.setPostCount( postCount + 1);
		        //forum.setLastTopicId(topic.getId());
		        //forum.setLastPostId(post.getId());
		        forum.setLastModifiedDate(DateTime.now());
		        forum.setLastTopic(topic);
		        forumRepository.save(forum);
		        
		        return post.getId();
			})
			.get();
    	
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "post/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(postId, "Post", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "post/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        return ResponseEntity.created(new URI("/api/topics/" + topicId)).build();
    }

    /**
     * POST  /topics/editPost -> Edit a post.
     */
    @RequestMapping(value = "/topics/editPost",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> editPost(@RequestParam("post") String postStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Post : {}", postStr);
        Object obj = JSONValue.parse( postStr);
    	JSONObject jPost = (JSONObject)obj;
    	
    	if (jPost.get("id") == null) {
            return createPost(postStr, files, request);
        }
    	Long postId = null;
		try {
			postId = Long.valueOf((Integer)jPost.get("id"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		postId = (Long) Optional.ofNullable((Post)postRepository.findOne(postId))
			.map(post -> {
		    	post.setName((String)jPost.get("name"));
		    	post.setContent((String)jPost.get("content"));
		    	post.setUser(user);
		    	postRepository.save(post);
		        postSearchRepository.save(post);
		        
		        return post.getId();
			})
			.get();
    	
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "post/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(postId, "Post", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "post/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        return ResponseEntity.ok().build();
    }

}
