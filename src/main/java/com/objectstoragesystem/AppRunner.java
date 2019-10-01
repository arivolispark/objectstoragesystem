package com.objectstoragesystem;



import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.objectstoragesystem.service.ConfigParameterService;
import com.objectstoragesystem.service.KMSRegionService;
import com.objectstoragesystem.service.EncryptionKeyService;
import com.objectstoragesystem.service.S3ObjectUploadService;

import com.objectstoragesystem.util.Util;


@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private ConfigParameterService configParameterService;

    @Autowired
    private KMSRegionService kmsRegionService;

    @Autowired
    private EncryptionKeyService encryptionKeyService;
 
    @Autowired
    private S3ObjectUploadService s3ObjectUploadService;

    private static final Logger logger = Logger.getLogger(AppRunner.class.getName());

    @Override
    public void run(String... args) throws Exception {
        Util.initialize(configParameterService, kmsRegionService, encryptionKeyService, s3ObjectUploadService);
    }
}
