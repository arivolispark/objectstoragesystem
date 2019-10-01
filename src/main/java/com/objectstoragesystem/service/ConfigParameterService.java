package com.objectstoragesystem.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.objectstoragesystem.entity.ConfigParameter;
import com.objectstoragesystem.repository.ConfigParameterRepository;


@Service
public class ConfigParameterService {
    @Autowired
    private ConfigParameterRepository configParameterRepository;

    public List<ConfigParameter> findAll() {
        List<ConfigParameter> commonConfigParameterList = new ArrayList<ConfigParameter>();
        Iterable<ConfigParameter> iterable = configParameterRepository.findAll();
        if (iterable != null) {
            iterable.forEach(commonConfigParameterList::add);
        }
        return commonConfigParameterList;
    }

    public ConfigParameter findById(Long id) {
        return configParameterRepository.findOne(id);
    }

    public ConfigParameter save(ConfigParameter configParameter) {
        return configParameterRepository.save(configParameter);
    }

    public void delete(ConfigParameter configParameter) {
        configParameterRepository.delete(configParameter);
        return;
    }

    public List<ConfigParameter> findByName(String name) {
        return configParameterRepository.findByName(name);
    }
}