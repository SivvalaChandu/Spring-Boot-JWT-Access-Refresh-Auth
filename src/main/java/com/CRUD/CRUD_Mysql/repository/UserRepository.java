package com.CRUD.CRUD_Mysql.repository;

import com.CRUD.CRUD_Mysql.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
