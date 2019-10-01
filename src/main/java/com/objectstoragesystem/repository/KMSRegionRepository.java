package com.objectstoragesystem.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.objectstoragesystem.entity.KMSRegion;


@Repository
public interface KMSRegionRepository extends CrudRepository<KMSRegion, Long> {
    @Query("select c from KMSRegion c where c.name = :name")
    public List<KMSRegion> findByName(@Param("name") String name);
}
