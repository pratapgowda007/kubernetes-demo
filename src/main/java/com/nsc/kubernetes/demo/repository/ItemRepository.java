package com.nsc.kubernetes.demo.repository;

import com.nsc.kubernetes.demo.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ItemRepository extends CrudRepository<Item, String> {

    @Query(value = "SELECT CLINICAL_ITEM_ID FROM ITEM", nativeQuery = true)
    Set<String> findAllClinicalItemIds();
}
