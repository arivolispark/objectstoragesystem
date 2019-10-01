package com.objectstoragesystem.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.objectstoragesystem.entity.EncryptionKey;


@Repository
public interface EncryptionKeyRepository extends CrudRepository<EncryptionKey, Long> {
    @Query("select c from EncryptionKey c where c.stackName = :stackName and c.status = :status")
    public List<EncryptionKey> findByStackNameAndStatus(@Param("stackName") String stackName, @Param("status") String status);

    @Query("select c from EncryptionKey c ORDER BY c.qvCreatedTs DESC")
    public List<EncryptionKey> findAllByQvCreatedTsDescending();

    @Query("select c from EncryptionKey c ORDER BY c.qvCreatedTs DESC")
    public List<EncryptionKey> findPageableEncryptionKey(Pageable pageable);
}
