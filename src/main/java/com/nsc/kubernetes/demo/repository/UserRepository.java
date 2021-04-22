package com.nsc.kubernetes.demo.repository;

import com.nsc.kubernetes.demo.model.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<AppUser, String> {

    @Query(value ="select u from USER u where u.userId IN (select userId from MY_USER) ", nativeQuery = true)
    List<AppUser> getUserBySubQueryExample();
}
