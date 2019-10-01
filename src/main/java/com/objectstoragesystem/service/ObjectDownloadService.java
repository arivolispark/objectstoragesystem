package com.objectstoragesystem.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectstoragesystem.entity.ObjectDownload;
import com.objectstoragesystem.repository.ObjectDownloadRepository;


@Service
public class ObjectDownloadService {
    @Autowired
    private ObjectDownloadRepository objectUploadRepository;

    public List<ObjectDownload> findAll() {
        List<ObjectDownload> objectUploadList = new ArrayList<ObjectDownload>();
        Iterable<ObjectDownload> iterable = objectUploadRepository.findAll();
        if (iterable != null) {
            iterable.forEach(objectUploadList::add);
        }
        return objectUploadList;
    }

    public ObjectDownload findById(Long id) {
        return objectUploadRepository.findOne(id);
    }

    public ObjectDownload save(ObjectDownload objectUpload) {
        return objectUploadRepository.save(objectUpload);
    }

    public void delete(ObjectDownload objectUpload) {
        objectUploadRepository.delete(objectUpload);
        return;
    }

    public List<ObjectDownload> findByFeed(String feed) {
        return objectUploadRepository.findByFeed(feed);
    }
}