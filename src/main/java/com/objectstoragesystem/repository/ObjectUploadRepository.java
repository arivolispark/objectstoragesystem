package com.objectstoragesystem.repository;



import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.objectstoragesystem.entity.ObjectUpload;


@Repository
public interface ObjectUploadRepository  extends PagingAndSortingRepository<ObjectUpload, Long> {
    @Query("select c from ObjectUpload c where c.s3Key like %:fileName%")
    public Page<ObjectUpload> findByFileName(@Param("fileName") String fileName, Pageable pageable);

    @Query("select c from ObjectUpload c where c.status = status")
    public Page<ObjectUpload> findByStatus(@Param("status") String status, Pageable pageable);
}