package com.objectstoragesystem.service;



import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import com.amazonaws.services.s3.model.GetObjectRequest;
import java.time.Instant;
import java.time.Duration;
import com.amazonaws.services.s3.transfer.Download;

import com.objectstoragesystem.util.Constants;
import com.objectstoragesystem.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import com.objectstoragesystem.entity.ObjectDownload;


@Service
public class S3ObjectDownloadService {
    private static final Logger logger = Logger.getLogger(S3ObjectDownloadService.class.getName());

    @Autowired
    private ObjectDownloadService objectDownloadService;

    public S3ObjectDownloadService() {
    }

    @Async
    public CompletableFuture<Boolean> download(String s3ObjectKey, String feed) throws IOException, InterruptedException {
        logger.info("\n CompletableFuture download(String s3ObjectKey, String feed)");
        Boolean status = Boolean.FALSE;

        if (s3ObjectKey == null || s3ObjectKey.length() == 0) {
            throw new IllegalArgumentException("Invalid s3ObjectKey supplied.  Please supply valid s3ObjectKey.");
        }

        if (objectDownloadService == null) {
            throw new IllegalArgumentException("Invalid objectDownloadService.  Please restart Spring Boot application.");
        }

        logger.info(" s3ObjectKey: " + s3ObjectKey);
        logger.info(" feed: " + feed);

        String compressedFileName = s3ObjectKey.substring(s3ObjectKey.lastIndexOf("/") + 1, s3ObjectKey.length());
        logger.info("\n compressedFileName: " + compressedFileName);

        File downloadFile = new File(Util.downloadFolder + File.separator + compressedFileName);
        logger.info(" downloadFile.getCanonicalPath(): " + downloadFile.getCanonicalPath());

 
        long fileSizeInBytes = 0L;
        logger.info(" fileSizeInBytes: " + fileSizeInBytes);

        long decompressedFileSizeInBytes = 0L;
        logger.info(" decompressedFileSizeInBytes: " + decompressedFileSizeInBytes);

        long decryptedFileSizeInBytes = 0L;
        logger.info(" decryptedFileSizeInBytes: " + decryptedFileSizeInBytes);


        String operationStatus = null;
        int iterationCount = 0;
 
        for (int i=0; i<Util.fileTransferRetryCount; i++) {

            ++iterationCount;
            logger.info("\n Iteration #: " + iterationCount);

            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            logger.info("\n startTime: " + startTime);

            operationStatus = downloadMultipartFile(s3ObjectKey, downloadFile);
            logger.info("\n operationStatus: " + operationStatus);

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            logger.info("\n endTime: " + endTime);

            long durationInMilliSeconds = endTime.getTime() - startTime.getTime();
            logger.info(" durationInMilliSeconds: " + durationInMilliSeconds);

            ObjectDownload objectDownload = Util.createObjectDownload(s3ObjectKey,
                                                                downloadFile.getCanonicalPath(),
                                                                new Long(fileSizeInBytes),
                                                                new Long(decompressedFileSizeInBytes),
                                                                new Long(decryptedFileSizeInBytes),
                                                                startTime,
                                                                endTime,
                                                                durationInMilliSeconds,
                                                                new Long(iterationCount),
                                                                Constants.FILETRANSFER_TRANSFER_COMPLETED,
                                                                operationStatus,
                                                                Util.stack,
                                                                feed,
                                                                Constants.RAWDATAFILE_SOURCE_HISTORICAL,
                                                                Constants.OBJECT_QVCREATEDSRC_SYSTEM,
                                                                Constants.OBJECT_QVUPDATEDSRC_SYSTEM
                                                               );
            logger.info("\n Persisting objectDownload: " + objectDownload);
            try {
                objectDownloadService.save(objectDownload);
                logger.info("\n Persisted objectDownload: " + objectDownload);
            } catch(Exception e) {
                logger.severe(e.getMessage());
            }

            if (operationStatus == null) {
                status = Boolean.TRUE;
                
                File decompressedFile = decompress(downloadFile);

                break;
            }

            Thread.sleep(Util.fileTransferInitialWaitTimeInMilliSeconds);
        }

        if (iterationCount ==  Util.fileTransferRetryCount) {
            logger.info("\n S3 file download unsuccessful for s3ObjectKey: " + s3ObjectKey);
        }

        return CompletableFuture.completedFuture(status);
    }

    public File decompress(File file) throws IOException {
        File decompressedFile = null;
        if (file != null) {
            logger.info("\n file.getCananonicalPath(): " + file.getCanonicalPath());

            String fileName = file.getName();
            String decompressedFileName = fileName.substring(0, fileName.lastIndexOf("."));
            logger.info("\n decompressedFileName: " + decompressedFileName);

            String decompressedAndEncrptedCanonicalFilePath = Util.downloadFolder + File.separator + decompressedFileName;
            logger.info("\n decompressedAndEncrptedCanonicalFilePath: " + decompressedAndEncrptedCanonicalFilePath);

            Util.decompressFromGzipFile(file.getCanonicalPath(), decompressedAndEncrptedCanonicalFilePath);

            decompressedFile = new File(decompressedAndEncrptedCanonicalFilePath);
        }
        return decompressedFile;
    }

    public String downloadMultipartFile(String s3Key, File file) {
        logger.info("\n\n ==>> void downloadMultipartFile(String s3Key, File file)");
        logger.info(" s3Key: " + s3Key);
        logger.info(" file: " + file);

        String operationStatus = null;

        final GetObjectRequest request = new GetObjectRequest(Util.awsS3Bucket, s3Key);
        logger.info("\n request: " + request);

        request.setGeneralProgressListener(new ProgressListener() {

            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                ProgressEventType progressEventType = progressEvent.getEventType();
                logger.info("\n progressEventType: " + progressEventType);
                logger.info(" progressEventType.isTransferEvent(): " + progressEventType.isTransferEvent());
                logger.info(" progressEventType.isRequestCycleEvent(): " + progressEventType.isRequestCycleEvent());
                logger.info(" progressEventType.isByteCountEvent(): " + progressEventType.isByteCountEvent());

                if (progressEventType.name().equals(ProgressEventType.TRANSFER_STARTED_EVENT.name())) {
                    logger.info("\n File transfer started");
                } else if (progressEventType.name().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT.name())) {
                    logger.info("\n File transfer completed");
                } else if (progressEventType.name().equals(ProgressEventType.TRANSFER_FAILED_EVENT.name())) {
                    logger.info("\n File transfer failed");
                }

                String transferredBytes = "\n Transferred bytes: " + progressEvent.getBytesTransferred();
                logger.info("\n transferredBytes: " + transferredBytes);
            }
        });

        Download download = Util.encryptedTransferManager.download(request, file);
        logger.info("\n download: " + download);

        try {
            download.waitForCompletion();
        } catch(AmazonServiceException e) {
            e.printStackTrace();
            if (e != null && e.getMessage() != null) {
                operationStatus = e.getMessage();
            }
        } catch(AmazonClientException e) {
            e.printStackTrace();
            if (e != null && e.getMessage() != null) {
                operationStatus = e.getMessage();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
            if (e != null && e.getMessage() != null) {
                operationStatus = e.getMessage();
            }
        }

        logger.info("\n operationStatus: " + operationStatus);
        return operationStatus;
    }
}
