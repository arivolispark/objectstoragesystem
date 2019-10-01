package com.objectstoragesystem.repository;



import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.objectstoragesystem.entity.ObjectDownload;


@Repository
public interface ObjectDownloadRepository  extends CrudRepository<ObjectDownload, Long> {
    @Query("select c from ObjectDownload c where c.feed = :feed")
    public List<ObjectDownload> findByFeed(@Param("feed") String feed);
}
