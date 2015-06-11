package com.usemodj.nodesoft.repository;

import java.util.List;

import com.usemodj.nodesoft.domain.User;

public interface UserRepositoryCustom {
	List<User> usersList(int page, int perPage);
}
