package com.usemodj.nodesoft.web.rest;

import org.elasticsearch.index.query.QueryBuilders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Article;
import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.User;
import com.usemodj.nodesoft.repository.ArticleRepository;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.search.ArticleSearchRepository;
import com.usemodj.nodesoft.service.UserService;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

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

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private UserService userService;
    
    @Inject
    private Environment env;

    /**
     * POST  /articles -> Create a new article.
     */
    @RequestMapping(value = "/upload/article",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> createArticle( @RequestParam("article") String articleStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Article : {}", articleStr);
    	
        Object obj = JSONValue.parse( articleStr);
    	JSONObject jsonObj = (JSONObject)obj;
    	
        //log.debug(" article subject: {}, content:{} ", jsonObj.get("subject"), jsonObj.get("content"));
    	Article article = new Article((String)jsonObj.get("subject"), (String)jsonObj.get("summary"), 
    			(String)jsonObj.get("content"), (String)jsonObj.get("imgUrl"), user);

        articleRepository.save(article);
        articleSearchRepository.save(article);
        
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "article/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(article.getId(), "Article", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "article/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return ResponseEntity.created(new URI("/api/articles/" + article.getId())).build();
    }

    /**
     * POST  /articles -> Create a new article.
     */
    @RequestMapping(value = "/articles/update_article",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> updateArticle( @RequestParam("article") String articleStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        log.debug("REST request to save Article : {}", articleStr);
    	
        Object obj = JSONValue.parse( articleStr);
    	JSONObject jsonObj = (JSONObject)obj;
    	
        //log.debug(" article subject: {}, content:{} ", jsonObj.get("subject"), jsonObj.get("content"));
    	Long articleId = Optional.ofNullable(articleRepository.findOne(Long.valueOf((Integer)jsonObj.get("id"))))
    			.map(article -> {
    				article.setSubject((String)jsonObj.get("subject"));
    				article.setSummary((String)jsonObj.get("summary"));
    				article.setContent((String)jsonObj.get("content"));
    				article.setImgUrl((String)jsonObj.get("imgUrl"));
    				article.setUser(user);
    		        articleRepository.save(article);
    		        articleSearchRepository.save(article);
    		        return article.getId();
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
				File dir = new File(uploadPath + "article/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(articleId, "Article", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "article/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return ResponseEntity.created(new URI("/api/articles/" + articleId)).build();
    }

    /**
     * POST  /articles -> Create a new article.
     */
    @RequestMapping(value = "/articles",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
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
        Page<Article> page = articleRepository.findAll(PaginationUtil.generatePageRequest(offset, limit, Direction.DESC, "id"));
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
        return Optional.ofNullable(articleRepository.findOneWithAssets(id))
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
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
    	String uploadPath = env.getProperty("uploadPath");
    	
        log.debug("REST request to delete Article : {}", id);
        return Optional.ofNullable(articleRepository.findOneWithAssets( id))
        		.map(article -> {
        			for(Asset asset: article.getAssets()){
        				File file = new File(uploadPath + asset.getFilePath());
        				file.delete();
        				log.info(" file deleted {}", file.getAbsolutePath());
        				assetRepository.delete(asset);
        			}
        	        articleRepository.delete(id);
        	        articleSearchRepository.delete(id);
        			return new ResponseEntity<>( article, HttpStatus.OK);
        		})
        		.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        
        
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
            .stream(articleSearchRepository.search(QueryBuilders.fuzzyLikeThisQuery().likeText(query)).spliterator(), false)
            .collect(Collectors.toList());
//        return StreamSupport
//                .stream(articleSearchRepository.search(QueryBuilders.multiMatchQuery(query, "subject","summary", "content")).spliterator(), false)
//                .collect(Collectors.toList());
//        return StreamSupport
//                .stream(articleSearchRepository.search(QueryBuilders.moreLikeThisQuery("subject","summary", "article.content").likeText(query).minTermFreq(1).maxQueryTerms(12)).spliterator(), false)
//                .collect(Collectors.toList());
    }
}
