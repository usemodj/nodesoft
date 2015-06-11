package com.usemodj.nodesoft.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.usemodj.nodesoft.domain.User;

public class UserRepositoryImpl implements UserRepositoryCustom {
	  @PersistenceContext
	  private EntityManager em;

	@Override
	public List<User> usersList(int page, int perPage) {
		String select = "SELECT u FROM User u ";
		return em.createQuery(select)
				.setFirstResult((page - 1)* perPage)
				.setMaxResults(perPage)
				.getResultList();
	}

}
