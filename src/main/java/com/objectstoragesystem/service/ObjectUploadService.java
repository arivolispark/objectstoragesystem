package com.objectstoragesystem.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectstoragesystem.entity.ObjectUpload;
import com.objectstoragesystem.repository.ObjectUploadRepository;


@Service
public class ObjectUploadService {
    @Autowired
    private ObjectUploadRepository objectUploadRepository;

    public List<ObjectUpload> findAll() {
        List<ObjectUpload> objectUploadList = new ArrayList<ObjectUpload>();
        Iterable<ObjectUpload> iterable = objectUploadRepository.findAll();
        if (iterable != null) {
            iterable.forEach(objectUploadList::add);
        }
        return objectUploadList;
    }

    public ObjectUpload findById(Long id) {
        return objectUploadRepository.findOne(id);
    }

    public ObjectUpload save(ObjectUpload objectUpload) {
        return objectUploadRepository.save(objectUpload);
    }

    public void delete(ObjectUpload objectUpload) {
        objectUploadRepository.delete(objectUpload);
        return;
    }

    public Page<ObjectUpload> findByFileName(String objectName, Pageable pageable) {
        return objectUploadRepository.findByFileName(objectName, pageable);
    }
}