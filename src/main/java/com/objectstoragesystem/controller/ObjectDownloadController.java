package com.objectstoragesystem.controller;



import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

import com.objectstoragesystem.exception.BadRequestException;
import com.objectstoragesystem.exception.ResourceNotFoundException;
import com.objectstoragesystem.util.Constants;
import com.objectstoragesystem.util.Util;
import com.objectstoragesystem.service.S3ObjectDownloadService;

import com.amazonaws.services.s3.model.S3ObjectSummary;


@RestController
@RequestMapping(Constants.V1_OBJECT_DOWNLOAD_SERVICE_RESOURCE_URI)
public class ObjectDownloadController {
    private static final Logger logger = Logger.getLogger(ObjectDownloadController.class.getName());

    @Autowired
    private S3ObjectDownloadService s3ObjectDownloadService;

    @RequestMapping(method=RequestMethod.GET, value = {"", "/list/timerange/{from}/{to}"})
    @ResponseBody
    public List<S3ObjectSummary> listObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to) {
        logger.log(Level.INFO, "\n ObjectDownloadController::listObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)");

        List<S3ObjectSummary> s3ObjectSummaryList = new ArrayList<S3ObjectSummary>();

        if (!from.isPresent()) {
            logger.info("\n ObjectDownloadController::listObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)::from is not present");
        }

        if (!to.isPresent()) {
            logger.info("\n ObjectDownloadController::listObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)::to is not present");
        }

        if (from.isPresent() && to.isPresent()) {
            String fromStr = from.get();

            if (fromStr != null && fromStr.length() > 0) {
                logger.info("\n ObjectDownloadController::listObjectsByTimeRange(@PathVariable String from, @PathVariable String to)::fromStr: " + fromStr);
            }

            String toStr = to.get();
            if (toStr!= null && toStr.length() > 0) {
                logger.info("\n ObjectDownloadController::listObjectsByTimeRange(@PathVariable String from, @PathVariable String to)::toStr: " + toStr);
            }

            try {
                String fromS3KeyPrefix = Util.getS3KeyPrefix(fromStr, "from");
                logger.info("\n fromS3KeyPrefix: " + fromS3KeyPrefix);

                String toS3KeyPrefix = Util.getS3KeyPrefix(toStr, "to");
                logger.info("\n toS3KeyPrefix: " + toS3KeyPrefix);

                s3ObjectSummaryList = Util.listS3EncryptedObjects(Util.amazonS3EncryptionClient, Util.awsS3Bucket, fromS3KeyPrefix, toS3KeyPrefix);
                logger.info("\n s3ObjectSummaryList: " + s3ObjectSummaryList);
            } catch(ParseException parseException) {
                logger.severe(parseException.getMessage());
            }
        }

        return s3ObjectSummaryList;
    }

    @RequestMapping(method=RequestMethod.GET, value = {"", "/download/timerange/{from}/{to}"})
    @ResponseBody
    public List<S3ObjectSummary> downloadObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to) {
        logger.log(Level.INFO, "\n ObjectDownloadController::downloadObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)");

        List<S3ObjectSummary> s3ObjectSummaryList = new ArrayList<S3ObjectSummary>();

        if (!from.isPresent()) {
            logger.info("\n ObjectDownloadController::downloadObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)::from is not present");
        }

        if (!to.isPresent()) {
            logger.info("\n ObjectDownloadController::downloadObjectsByTimeRange(@PathVariable Optional<String> from, @PathVariable Optional<String> to)::to is not present");
        }

        if (from.isPresent() && to.isPresent()) {
            String fromStr = from.get();

            if (fromStr != null && fromStr.length() > 0) {
                logger.info("\n ObjectDownloadController::downloadObjectsByTimeRange(@PathVariable String from, @PathVariable String to)::fromStr: " + fromStr);
            }

            String toStr = to.get();
            if (toStr!= null && toStr.length() > 0) {
                logger.info("\n ObjectDownloadController::downloadObjectsByTimeRange(@PathVariable String from, @PathVariable String to)::toStr: " + toStr);
            }

            try {
                String fromS3KeyPrefix = Util.getS3KeyPrefix(fromStr, "from");
                logger.info("\n fromS3KeyPrefix: " + fromS3KeyPrefix);

                String toS3KeyPrefix = Util.getS3KeyPrefix(toStr, "to");
                logger.info("\n toS3KeyPrefix: " + toS3KeyPrefix);

                s3ObjectSummaryList = Util.listS3EncryptedObjects(Util.amazonS3EncryptionClient, Util.awsS3Bucket, fromS3KeyPrefix, toS3KeyPrefix);
                logger.info("\n s3ObjectSummaryList: " + s3ObjectSummaryList);
            } catch(ParseException parseException) {
                logger.severe(parseException.getMessage());
            }

            if (s3ObjectSummaryList != null && !s3ObjectSummaryList.isEmpty()) {
                for (int i=0; i<s3ObjectSummaryList.size(); i++) {
                    S3ObjectSummary s3ObjectSummary = (S3ObjectSummary)s3ObjectSummaryList.get(i);
                    String objectKey = s3ObjectSummary.getKey();
                    logger.info("\n\n objectKey: " + objectKey);

                    try {
                        CompletableFuture<Boolean> completableFutureStatus = s3ObjectDownloadService.download(objectKey, null);
                        if (completableFutureStatus != null) {
                            logger.info("\n\n completableFutureStatus: " + completableFutureStatus);
                            Boolean status = completableFutureStatus.get();
                            logger.info("\n status: " + status);
                        }
                    } catch(Exception e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        }

        return s3ObjectSummaryList;
    }
}