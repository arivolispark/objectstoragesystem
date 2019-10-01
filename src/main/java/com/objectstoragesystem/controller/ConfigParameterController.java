package com.objectstoragesystem.controller;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.objectstoragesystem.entity.ConfigParameter;
import com.objectstoragesystem.exception.BadRequestException;
import com.objectstoragesystem.exception.ResourceNotFoundException;
import com.objectstoragesystem.service.ConfigParameterService;
import com.objectstoragesystem.util.Constants;
import com.objectstoragesystem.util.Util;


@RestController
@RequestMapping(Constants.V1_CONFIG_SERVICE_RESOURCE_URI)
public class ConfigParameterController {
    @Autowired
    private ConfigParameterService configParameterService;

    private final static Logger logger = Logger.getLogger(ConfigParameterController.class.getName());


    /**
     * This method can be used to get the list of all the CommonConfigParameter objects.
     * Optionally, the id parameter can be supplied as input parameter.  In that case,
     * a list of ConfigParamater objects are returned filtered based on id.
     *
     * @param id - The id.  This is an optional input parameter
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value = {"", "{id}"})
    @ResponseBody
    public List<ConfigParameter> listConfigParameters(@PathVariable Optional<Long> id) {
        logger.info("\n ConfigParameterController::listConfigParameters(@PathVariable Optional<Long> id)");

        List<ConfigParameter> configParameterList = new ArrayList<ConfigParameter>();

        if (!id.isPresent()) {
            return configParameterService.findAll();
        } else {
            Long idLong = id.get();
            if (idLong == null || idLong <= 0) {
                throw new BadRequestException("Unsupported value supplied for 'id'.  Please supply a value greater than zero and retry");
            }

            ConfigParameter configParameter = configParameterService.findById(idLong);
            if (configParameter == null) {
                throw new ResourceNotFoundException("There does not exist any ConfigParameter resource for id: " + idLong +
                        ".  Please provide the id of an existing ConfigParameter resource and retry");
            }

            configParameterList.add(configParameter);
            return configParameterList;
        }
    }

    /**
     * This method can be used to get the list of all the CommonConfigParameter objects filtered by name.
     *
     * @param name - The name of the config parameter
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value = {"/name/{name}"})
    @ResponseBody
    public List<ConfigParameter> getConfigParameters(@PathVariable String name) {
        logger.info("\n ConfigParameterController::getConfigParameters(@PathVariable <String> name)");
        return configParameterService.findByName(name);
    }

    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConfigParameter create(@RequestBody ConfigParameter configParameter) {
        logger.info("\n ConfigParameterController::create(@RequestBody ConfigParameter configParameter)");

        if (configParameter == null) {
            throw new BadRequestException("Null configParameter supplied.  Please provide valid input and retry");
        }

        if (configParameter.getId() != null && configParameter.getId() > 0) {
            throw new BadRequestException("'id' field should not be populated in the supplied configParameter object, as it is a system generated field.  Please provide valid input and retry");
        }

        if (configParameter.getName() == null || configParameter.getName().length() <= 0) {
            throw new BadRequestException("'name' is a not-nullable field in the ConfigParameter object.  Please provide valid input and retry");
        }

        if (configParameter.getValue() == null || configParameter.getValue().length() <= 0) {
            throw new BadRequestException("'value' is a not-nullable field in the ConfigParameter object.  Please provide valid input and retry");
        }

        configParameterService.save(configParameter);
        return configParameter;
    }

    @RequestMapping(method=RequestMethod.DELETE, value="{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable Long id) {
        logger.info("\n ConfigParameterController::delete(@PathVariable Long id)");

        if (id == null || id <= 0) {
            throw new BadRequestException("Unsupported value supplied for 'id'.  Please supply a value greater than zero and retry");
        }

        ConfigParameter configParameter = configParameterService.findById(id);
        if (configParameter == null) {
            throw new ResourceNotFoundException("There does not exist any ConfigParameter resource for id: " + id + " and so it can not be deleted."
                    + "  Please provide the id of an existing ConfigParameter resource and retry");
        }

        configParameterService.delete(configParameter);
    }

    @RequestMapping(method=RequestMethod.PUT, value="{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ConfigParameter update(@RequestBody ConfigParameter newConfigParameter, @PathVariable Long id) {
        logger.info("\n ConfigParameterController::update(@RequestBody ConfigParameter newConfigParameter, @PathVariable Long id)");

        if (newConfigParameter == null) {
            throw new BadRequestException("Null newConfigParameter supplied.  Please provide valid input and retry");
        }

        if (newConfigParameter.getId() != null && newConfigParameter.getId() > 0) {
            throw new BadRequestException("'id' field should not be populated in the supplied newConfigParameter object, as it is a system generated field.  Please provide valid input and retry");
        }

        if (newConfigParameter.getName() == null || newConfigParameter.getName().length() <= 0) {
            throw new BadRequestException("'name' is a not-nullable field in the newConfigParameter object.  Please provide valid input and retry");
        }

        if (newConfigParameter.getValue() == null || newConfigParameter.getValue().length() <= 0) {
            throw new BadRequestException("'value' is a not-nullable field in the newConfigParameter object.  Please provide valid input and retry");
        }

        if (id == null || id <= 0) {
            throw new BadRequestException("Unsupported value supplied for 'id'.  Please supply a value greater than zero and retry");
        }

        ConfigParameter existingConfigParameter = configParameterService.findById(id);
        if (existingConfigParameter == null) {
            throw new ResourceNotFoundException("There does not exist any ConfigParameter resource for id: " + id + " and so it can not be updated."
                    + "  Please provide the id of an existing ConfigParameter resource and retry");
        }

        existingConfigParameter.setName(newConfigParameter.getName());
        existingConfigParameter.setValue(newConfigParameter.getValue());

        return configParameterService.save(existingConfigParameter);
    }
}