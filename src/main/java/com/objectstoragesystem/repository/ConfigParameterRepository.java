package com.objectstoragesystem.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.objectstoragesystem.entity.ConfigParameter;


@Repository
public interface ConfigParameterRepository extends CrudRepository<ConfigParameter, Long> {
    @Query("select c from ConfigParameter c where c.name = :name")
    public List<ConfigParameter> findByName(@Param("name") String name);
}