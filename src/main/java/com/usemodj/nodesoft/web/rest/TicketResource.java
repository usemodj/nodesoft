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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

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
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.domain.Ticket;
import com.usemodj.nodesoft.domain.User;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.MessageRepository;
import com.usemodj.nodesoft.repository.TicketRepository;
import com.usemodj.nodesoft.repository.search.TicketSearchRepository;
import com.usemodj.nodesoft.service.UserService;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Ticket.
 */
@RestController
@RequestMapping("/api")
public class TicketResource {

    private final Logger log = LoggerFactory.getLogger(TicketResource.class);

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private TicketSearchRepository ticketSearchRepository;
    
    @Inject
    private MessageRepository messageRepository;
    
    @Inject
    private AssetRepository assetRepository;

    @Inject
    private UserService userService;
    
    @Inject
    private Environment env;


    /**
     * POST  /upload_tickets -> Create a new ticket.
     */

    @RequestMapping(value = "/upload/ticket",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> createTicket(@RequestParam("ticket") String ticketStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();

    	log.debug(" ticket: {} ", ticketStr);
    	Object obj = JSONValue.parse( ticketStr);
    	JSONObject jsonObj = (JSONObject)obj;
    	
        log.debug(" ticket subject: {}, content:{} ", jsonObj.get("subject"), jsonObj.get("content"));
        
        Ticket ticket  = new Ticket((String)jsonObj.get("subject"), (String)jsonObj.get("status"), 0, 0, user);
        ticketRepository.save(ticket);
        ticketSearchRepository.save(ticket);
        
        //TODO: get session user object
        //log.debug("RemoteUser : {}", request.getRemoteUser());
        //log.debug("UserPrincipal : {}", request.getUserPrincipal());
        Message message = new Message( (String)jsonObj.get("content"), true, request.getRemoteAddr(), user, ticket);
        messageRepository.save( message);
        
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "message/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(message.getId(), "Message", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "message/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
        return ResponseEntity.created(new URI("/api/tickets/" + ticket.getId())).build();
    }

    /**
     * POST  /reply message -> Create a new message.
     */

    @RequestMapping(value = "/ticket/reply",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> replyTicket(@RequestParam("message") String messageStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
    	log.debug(" message: {} ", messageStr);
    	Object obj = JSONValue.parse( messageStr);
    	JSONObject jsonObj = (JSONObject)obj;
    	
        log.debug(" ticket id: {}, content:{} ", jsonObj.get("ticket_id"), jsonObj.get("content"));
        Long ticketId = Long.valueOf((String)jsonObj.get("ticket_id"));
        Ticket ticket  = ticketRepository.findOne(ticketId);
        
        Message message = new Message( (String)jsonObj.get("content"), false, request.getRemoteAddr(), user, ticket);
        messageRepository.save( message);

        ticket.setStatus((String)jsonObj.get("status"));
        ticket.setReplies(ticket.getReplies() + 1);
        ticket.setLastReplier(user);
        ticket.setLastReply(message);
        ticket.setLastModifiedDate(DateTime.now());
        ticketRepository.save(ticket);
        //ticketSearchRepository.save(ticket);
        
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();

        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug("getName:{}, getOriginalFilename: {}", files[i].getName(), files[i].getOriginalFilename());
    		
    		try {
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "message/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(message.getId(), "Message", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "message/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
     
        return ResponseEntity.created(new URI("/api/tickets/" + ticket.getId())).build();
    }

    /**
     * POST  update_message -> Edit a message
     */

    @RequestMapping(value = "/ticket/update_message",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateMessage(@RequestParam("message") String messageStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
    	log.debug(" message: {} ", messageStr);
    	Object obj = JSONValue.parse( messageStr);
    	JSONObject jsonObj = (JSONObject)obj;
    	
        log.debug(" ticket id: {}, message id: {}, content:{} ", jsonObj.get("ticket_id"), jsonObj.get("id"), jsonObj.get("content"));
        Long ticketId = Long.valueOf((String)jsonObj.get("ticket_id"));
        Long messageId = Long.valueOf((Integer)jsonObj.get("id"));
        
        Message message = messageRepository.findOne(messageId);
        
        message.setContent((String)jsonObj.get("content"));
        message.setIpaddress(request.getRemoteAddr());
        message.setUser(user);
        message.setLastModifiedDate( DateTime.now());
        messageRepository.save( message);

        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();

        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug("getName:{}, getOriginalFilename: {}", files[i].getName(), files[i].getOriginalFilename());
    		
    		try {
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + "message/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(message.getId(), "Message", files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), "message/"+ now.getTime() +"/"+files[i].getOriginalFilename(), files[i].getOriginalFilename(), Integer.valueOf(i));
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
     
        return ResponseEntity.created(new URI("/api/tickets/" + ticketId)).build();
    }

    /**
     * POST  /tickets -> Create a new ticket.
     */
    @RequestMapping(value = "/tickets",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Ticket ticket) throws URISyntaxException {
        log.debug("REST request to save Ticket : {}", ticket);
        if (ticket.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new ticket cannot already have an ID").build();
        }
        ticketRepository.save(ticket);
        ticketSearchRepository.save(ticket);
        return ResponseEntity.created(new URI("/api/tickets/" + ticket.getId())).build();
    }

    /**
     * PUT  /tickets -> Updates an existing ticket.
     */
    @RequestMapping(value = "/tickets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Ticket ticket) throws URISyntaxException {
        log.debug("REST request to update Ticket : {}", ticket);
        if (ticket.getId() == null) {
            return create(ticket);
        }
        ticketRepository.save(ticket);
        ticketSearchRepository.save(ticket);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /tickets -> get all the tickets.
     */
    @RequestMapping(value = "/tickets",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ticket>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Ticket> page = ticketRepository.findAll(PaginationUtil.generatePageRequest(offset, limit, Direction.DESC, "id"));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tickets", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tickets/:id -> get the "id" ticket.
     */
    @RequestMapping(value = "/tickets/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ticket> get(@PathVariable Long id) {
        log.debug("REST request to get Ticket : {}", id);
        return Optional.ofNullable(ticketRepository.findOne(id))
                .map(ticket -> {
                	//Optional.ofNullable(messageRepository.findAllByTicketIdOrderByIdDesc(ticket.getId()))
                 	Optional.ofNullable(messageRepository.fileAllWithAssetsQuery(ticket.getId()))
                 	  .map(messages -> {
                		ticket.setMessages(new HashSet(messages)); 
                		return ticket;
                		});
                	
            		return new ResponseEntity<>(ticket, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tickets/:id -> delete the "id" ticket.
     */
    @RequestMapping(value = "/tickets/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Ticket : {}", id);
        ticketRepository.delete(id);
        ticketSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/tickets/:query -> search for the ticket corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/tickets/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ticket> search(@PathVariable String query) {
        return StreamSupport
            .stream(ticketSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
