package com.objectstoragesystem.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.objectstoragesystem.entity.EncryptionKey;
import com.objectstoragesystem.repository.EncryptionKeyRepository;


@Service
public class EncryptionKeyService {
    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    public List<EncryptionKey> findAll() {
        List<EncryptionKey> encryptionKeyList = new ArrayList<EncryptionKey>();
        Iterable<EncryptionKey> iterable = encryptionKeyRepository.findAll();
        if (iterable != null) {
            iterable.forEach(encryptionKeyList::add);
        }
        return encryptionKeyList;
    }
	
    public EncryptionKey findById(Long id) {
        return encryptionKeyRepository.findOne(id);
    }

    public EncryptionKey save(EncryptionKey encryptionKey) {
        return encryptionKeyRepository.save(encryptionKey);
    }

    public void delete(EncryptionKey encryptionKey) {
        encryptionKeyRepository.delete(encryptionKey);
        return; 
    }

    public List<EncryptionKey> findByStackNameAndStatus(String stackName, String status) {
        return encryptionKeyRepository.findByStackNameAndStatus(stackName, status);
    }

    public List<EncryptionKey> findAllByQvCreatedTsDescending() {
        return encryptionKeyRepository.findAllByQvCreatedTsDescending();
    }

    public List<EncryptionKey> findLatest() {
        return encryptionKeyRepository.findPageableEncryptionKey(new PageRequest(0, 1));
    }
}
