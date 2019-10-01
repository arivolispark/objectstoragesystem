package com.objectstoragesystem.controller;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.objectstoragesystem.entity.ObjectUpload;
import com.objectstoragesystem.repository.ObjectUploadRepository;
import com.objectstoragesystem.exception.BadRequestException;
import com.objectstoragesystem.exception.ResourceNotFoundException;
import com.objectstoragesystem.util.Constants;
import com.objectstoragesystem.util.Util;


@RestController
@RequestMapping(Constants.V1_OBJECT_UPLOAD_SERVICE_RESOURCE_URI)
public class ObjectUploadController {
    @Autowired
    private ObjectUploadRepository objectUploadRepository;

    private final static Logger logger = Logger.getLogger(ObjectUploadController.class.getName());


    /**
     * This method can be used to get the list of all the ConfigParameter objects.
     * Optionally, the level parameter can be supplied as input parameter.  In that case,
     * a list of ConfigParamater objects are returned filtered based on levels.
     *
     * @param objectName - The objectName.  This is an optional input parameter
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value = {"", "{objectName}"})
    @ResponseBody
    public Page<ObjectUpload> getObjectUpload(Pageable pageable, Optional<String> objectName, Optional<String> status) {
        logger.info("\n ObjectUploadController::getObjectUpload(Pageable pageable, @PathVariable Optional<String> objectName, Optional<String> status)");

        if (!objectName.isPresent()) {
            logger.info("\n ObjectUploadController::getObjectUpload(Pageable pageable, @PathVariable Optional<String> objectName)::objectName is not present");
            return objectUploadRepository.findAll(pageable);
        } else {
            String objectNameString = objectName.get();
            logger.info("\n ObjectUploadController::getObjectUpload(Pageable pageable, @PathVariable Optional<String> objectName)::objectNameString: " + objectNameString);
            return objectUploadRepository.findByFileName(objectNameString, pageable);
        }
    }

    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ObjectUpload create(@RequestBody ObjectUpload objectUpload) {
        logger.log(Level.INFO, "\n ObjectUploadController::create(@RequestBody ObjectUpload objectUpload)");

        if (objectUpload == null) {
            throw new BadRequestException("Null objectUpload supplied.  Please provide valid input and retry");
        }

        if (objectUpload.getId() != null && objectUpload.getId() > 0) {
            throw new BadRequestException("'id' field should not be populated in the supplied objectUpload object, as it is a system generated field.  Please provide valid input and retry");
        }

        if (objectUpload.getFilePath() == null || objectUpload.getFilePath().length() <= 0) {
            throw new BadRequestException("'filePath' is a not-nullable field in the ObjectUpload object.  Please provide valid input and retry");
        }

        if (objectUpload.getS3Key() == null || objectUpload.getS3Key().length() <= 0) {
            throw new BadRequestException("'s3Key' is a not-nullable field in the ObjectUpload object.  Please provide valid input and retry");
        }

        String source = objectUpload.getSource();
        if (source == null || source.length() <= 0) {
            throw new BadRequestException("Invalid 'source' value supplied in ObjectUpload object.  Please provide valid input and retry");
        }

        if (!(source.equals(Constants.RAWDATAFILE_SOURCE_HISTORICAL) ||
                source.equals(Constants.RAWDATAFILE_SOURCE_HL7))) {
            throw new BadRequestException("Unsupported value supplied for 'source'.  The supported values for 'source' are [HISTORICAL | HL7].  Please supply valid input and retry");
        }

        String status = objectUpload.getStatus();
        if (status == null || status.length() <= 0) {
            throw new BadRequestException("Invalid 'status' value supplied in ObjectUpload object.  Please provide valid input and retry");
        }

        if (!(status.equals(Constants.FILETRANSFER_TRANSFER_NOT_STARTED) ||
                status.equals(Constants.FILETRANSFER_TRANSFER_IN_PROGRESS) ||
                status.equals(Constants.FILETRANSFER_TRANSFER_COMPLETED) ||
                status.equals(Constants.FILETRANSFER_TRANSFER_FAILED))) {
            throw new BadRequestException("Unsupported value supplied for 'status'.  The supported values for 'status' are [TRANSFER_NOT_STARTED | TRANSFER_IN_PROGRESS | TRANSFER_COMPLETED | TRANSFER_FAILED].  Please supply valid input and retry");
        }

        objectUploadRepository.save(objectUpload);
        return objectUpload;
    }

    @RequestMapping(method=RequestMethod.DELETE, value="{id}")
    public void delete(@PathVariable Long id) {
        logger.log(Level.INFO, "\n ObjectUploadController::delete(@PathVariable Long id)");

        if (id == null || id <= 0) {
            throw new BadRequestException("Unsupported value supplied for 'id'.  Please supply a value greater than zero and retry");
        }

        //TODO:  Complete this implementation
    }
}