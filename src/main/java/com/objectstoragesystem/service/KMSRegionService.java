package com.objectstoragesystem.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectstoragesystem.entity.KMSRegion;
import com.objectstoragesystem.repository.KMSRegionRepository;


@Service
public class KMSRegionService {
    @Autowired
    private KMSRegionRepository kmsRegionRepository;

    public List<KMSRegion> findAll() {
        List<KMSRegion> kmsRegionList = new ArrayList<KMSRegion>();
        Iterable<KMSRegion> iterable = kmsRegionRepository.findAll();
        if (iterable != null) {
            iterable.forEach(kmsRegionList::add);
        }
        return kmsRegionList;
    }
	
    public KMSRegion findById(Long id) {
        return kmsRegionRepository.findOne(id);
    }

    public KMSRegion save(KMSRegion kmsRegion) {
        return kmsRegionRepository.save(kmsRegion);
    }

    public void delete(KMSRegion kmsRegion) {
        kmsRegionRepository.delete(kmsRegion);
        return; 
    }

    public List<KMSRegion> findByName(String name) {
        return kmsRegionRepository.findByName(name);
    }
}
