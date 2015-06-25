package com.usemodj.nodesoft.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.domain.Variant;
import com.usemodj.nodesoft.domain.dto.AssetDTO;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;
import com.usemodj.nodesoft.web.rest.util.PaginationUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
    @RequestMapping(value = "/assets/upload",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestParam("asset") String assetStr, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save Asset : {}", assetStr);
    	JSONObject jAsset = (JSONObject)JSONValue.parse( assetStr);
    	
    	Long viewableId = Long.valueOf((String)jAsset.get("viewableId"));
    	String viewableType = (String)jAsset.get("viewableType");
    	String alt = (String)jAsset.get("alt");
    	Long assetId = null;
		try {
			assetId = Long.valueOf((Integer)jAsset.get("id"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	if ( assetId != null) {
    		assetRepository.deleteWithFile(assetId);
    	}
    	
        String uploadPath = env.getProperty("uploadPath");
    	Date now = new Date();
        // uploaded file saving
    	log.debug(" files size: {}", files.length);
    	for(int i =0; i < files.length; i++){
    		log.debug(" getOriginalFilename: {}", files[i].getOriginalFilename());
    		
    		try {
    			
				byte[] bytes = files[i].getBytes();
				File dir = new File(uploadPath + viewableType.toLowerCase() + "/"+ now.getTime());
				if(!dir.exists()) dir.mkdirs();
				
				//Create the file on server
				File serverFile = new File(dir.getAbsolutePath()+ File.separator + files[i].getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				log.info("Server File Location: {}", serverFile.getAbsolutePath());
				
				Asset asset = new Asset(viewableId, viewableType, files[i].getContentType(), String.valueOf(files[i].getSize()),
										files[i].getOriginalFilename(), viewableType.toLowerCase() + "/"+ now.getTime() +"/"+files[i].getOriginalFilename(), ((alt != null)? alt: files[i].getOriginalFilename()), i);
				assetRepository.save( asset);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

        return ResponseEntity.created(new URI("/api/product/" + viewableId + "/asset")).build();
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
//        if (asset.getId() == null) {
//            return create(asset);
//        }
        assetRepository.save(asset);
        assetSearchRepository.save(asset);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /assets -> get all the assets of the product.
     */
    @RequestMapping(value = "/assets",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AssetDTO>> getAll(@RequestParam("productId") Long productId, @RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
    	List<AssetDTO> assets = assetRepository.findAllProductAssets(productId);
        return new ResponseEntity<>( assets, HttpStatus.OK);
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
    
    /**
     * GET  /variants/position/:entry -> update the position of variants.
     */
    @RequestMapping(value = "/assets/position/{entry}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updatePosition(@PathVariable String entry) throws URISyntaxException {
        log.debug("REST request to update the position of Variants : {}", entry);
        String[] ids = StringUtils.delimitedListToStringArray(entry, ",");
        int i = 1;
        for(String id : ids){
        	Asset asset = assetRepository.findOne(Long.valueOf(id));
        	if(asset == null) continue;
        	asset.setPosition(i++);
        	assetRepository.save(asset);
        }
        return ResponseEntity.created(new URI("/api/assets/position")).build();
    }

}
