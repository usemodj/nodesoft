package com.usemodj.nodesoft.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.usemodj.nodesoft.domain.Taxon;
import com.usemodj.nodesoft.domain.User;
import com.usemodj.nodesoft.service.UserService;

public class TaxonRepositoryImpl implements TaxonRepositoryCustom {
	private final Logger log = LoggerFactory.getLogger(TaxonRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	Environment env;
	
	@Inject
	private TaxonRepository taxonRepository;
	
	@Inject
	private UserService userService;

	static String insertQuery ="INSERT INTO NS_TAXON (id, name, position, permalink, depth, parent_id, taxonomy_id, created_by, created_date)"+
								"VALUES(:id, :name, :position, :permalink, :depth, :parentId, :taxonomyId, :createdBy, now())";

	@Override
	@Transactional
	public void create(Taxon taxon) {
		User user = userService.getUserWithAuthorities();
		em.createNativeQuery(insertQuery)
		.setParameter("id", taxon.getId())
		.setParameter("name", taxon.getName())
		.setParameter("position", taxon.getPosition())
		.setParameter("permalink", taxon.getPermalink())
		.setParameter("depth", taxon.getDepth())
		.setParameter("parentId", taxon.getParentId())
		.setParameter("taxonomyId", taxon.getTaxonomy().getId())
		.setParameter("createdBy", user.getEmail())
		.executeUpdate();
	}

}
