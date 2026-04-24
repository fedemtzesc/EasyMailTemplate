package com.fdxsoft.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fdxsoft.entities.UserEntity;
import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{
	Optional<UserEntity> findByUsername(String username);
	
}
