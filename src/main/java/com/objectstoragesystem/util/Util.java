package com.objectstoragesystem.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import com.amazonaws.services.kms.model.DescribeKeyResult;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;

import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;

import com.objectstoragesystem.entity.ConfigParameter;
import com.objectstoragesystem.entity.KMSRegion;
import com.objectstoragesystem.entity.ObjectUpload;
import com.objectstoragesystem.entity.ObjectDownload;
import com.objectstoragesystem.entity.EncryptionKey;
import com.objectstoragesystem.service.ConfigParameterService;
import com.objectstoragesystem.service.KMSRegionService;
import com.objectstoragesystem.service.EncryptionKeyService;
import com.objectstoragesystem.service.S3ObjectUploadService;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import com.objectstoragesystem.security.EnvelopeEncryptedMessage;
import com.objectstoragesystem.security.EnvelopeEncryptionService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Util {
    private static final Logger logger = Logger.getLogger(Util.class.getName());

    public static String awsRegion = null;
    public static String stack = null;

    public static String awsAccessKeyID = null;
    public static String awsSecretAccessKey = null;
    public static String awsS3Bucket = null;
    public static String awsS3BucketFolder = null;
    public static String awsIamKmsCMK = null;

    public static String ingressFolder = null;
    public static String zipFolder = null;
    public static String uploadFolder = null;
    public static String compressDecompressFolder = null;
    public static String downloadFolder = null;

    public static String pollingIntervalInSecondsStr = null;
    public static String initialPollingDelayInSecondsStr = null;
    public static String fileAgeInSecondsStr = null;

    public static long pollingIntervalInSeconds = 5;
    public static long initialPollingDelayInSeconds = 5;
    public static long fileAgeInSeconds = 3600;

    public static String kmsRegionUrl = null;

    public static long fileTransferInitialWaitTimeInMilliSeconds = 10;  //0.01 seconds default value
    public static int fileTransferRetryCount = 3; //default value

    public static final long MILLISECOND_CONVERTING_FACTOR = 1000;

    public static AWSCredentials awsCredentials = null;
    public static AmazonS3EncryptionClient amazonS3EncryptionClient = null;
    public static TransferManager encryptedTransferManager = null;

    public static String getBaseS3Key() {
        StringBuilder builder = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND); // Note: no direct getter available.

        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);
        String hourString = String.format("%02d", hour);
        String minuteString = String.format("%02d", minute);

        builder.append(awsS3BucketFolder).
                //append(Constants.FORWARD_SLASH_SEPARATOR).
                //append(Constants.FEED).
                //append(Constants.EQUALS_SEPARATOR).
                //append(feed).
                append(Constants.FORWARD_SLASH_SEPARATOR).
                append(Constants.YEAR).
                append(Constants.EQUALS_SEPARATOR).
                append(year).
                append(Constants.FORWARD_SLASH_SEPARATOR).
                append(Constants.MONTH).
                append(Constants.EQUALS_SEPARATOR).
                append(monthString).
                append(Constants.FORWARD_SLASH_SEPARATOR).
                append(Constants.DAY).
                append(Constants.EQUALS_SEPARATOR).
                append(dayString).
                append(Constants.FORWARD_SLASH_SEPARATOR).
                append(Constants.HOUR).
                append(Constants.EQUALS_SEPARATOR).
                append(hourString).
                append(Constants.FORWARD_SLASH_SEPARATOR).
                append(Constants.MINUTE).
                append(Constants.EQUALS_SEPARATOR).
                append(minuteString).
                append(Constants.FORWARD_SLASH_SEPARATOR);

        return builder.toString();
    }

    public static ObjectUpload createObjectUpload(String filePath, String s3Key, Long fileSizeInBytes, Long compressedFileSizeInBytes, Timestamp transferStartTimeTs, Timestamp transferEndTimeTs, Long transferDurationInMilliSeconds, Long iterationCount, String status, String message, String stackName, String feed, String source, String qvCreatedSrc, String qvUpdatedSrc) {
        ObjectUpload objectUpload = new ObjectUpload();
        objectUpload.setFilePath(filePath);
        objectUpload.setS3Key(s3Key);
        objectUpload.setFileSizeInBytes(fileSizeInBytes);
        objectUpload.setCompressedFileSizeInBytes(compressedFileSizeInBytes);
        objectUpload.setTransferStartTimeTs(transferStartTimeTs);
        objectUpload.setTransferEndTimeTs(transferEndTimeTs);
        objectUpload.setTransferDurationInMilliSeconds(transferDurationInMilliSeconds);
        objectUpload.setIterationCount(iterationCount);
        objectUpload.setStatus(status);
        objectUpload.setMessage(message);
        objectUpload.setStackName(stackName);
        objectUpload.setFeed(feed);
        objectUpload.setSource(source);
        objectUpload.setQvCreatedSrc(qvCreatedSrc);
        objectUpload.setQvUpdatedSrc(qvUpdatedSrc);
        return objectUpload;
    }

    public static ObjectDownload createObjectDownload(String s3Key, String filePath, Long fileSizeInBytes, Long decompressedFileSizeInBytes, Long decryptedFileSizeInBytes, Timestamp transferStartTimeTs, Timestamp transferEndTimeTs, Long transferDurationInMilliSeconds, Long iterationCount, String status, String message, String stackName, String feed, String source, String qvCreatedSrc, String qvUpdatedSrc) {
        ObjectDownload objectDownload = new ObjectDownload();
        objectDownload.setS3Key(s3Key);
        objectDownload.setFilePath(filePath);
        objectDownload.setFileSizeInBytes(fileSizeInBytes);
        objectDownload.setDecompressedFileSizeInBytes(decompressedFileSizeInBytes);
        objectDownload.setDecryptedFileSizeInBytes(decryptedFileSizeInBytes);
        objectDownload.setTransferStartTimeTs(transferStartTimeTs);
        objectDownload.setTransferEndTimeTs(transferEndTimeTs);
        objectDownload.setTransferDurationInMilliSeconds(transferDurationInMilliSeconds);
        objectDownload.setIterationCount(iterationCount);
        objectDownload.setStatus(status);
        objectDownload.setMessage(message);
        objectDownload.setStackName(stackName);
        objectDownload.setFeed(feed);
        objectDownload.setSource(source);
        objectDownload.setQvCreatedSrc(qvCreatedSrc);
        objectDownload.setQvUpdatedSrc(qvUpdatedSrc);
        return objectDownload;
    }

    /**
     * Validate directory
     *
     * @param directoryCanonicalPathName
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static void isValidDirectory(String directoryCanonicalPathName) throws IllegalArgumentException, IOException {
        if (directoryCanonicalPathName == null || directoryCanonicalPathName.length() <= 0) {
            throw new IllegalArgumentException("Invalid directory name supplied.  Please supply a directory name");
        }

        File dir = new File(directoryCanonicalPathName);
        if (dir == null || !dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            throw new IllegalArgumentException("Invalid dir: " + dir.getCanonicalPath());
        }
    }

    public static void validate(String canonicalFilePathName) throws IllegalArgumentException, IOException {
        if (canonicalFilePathName == null) {
            throw new IllegalArgumentException(" Invalid: canonicalFilePathName is null.");
        }

        File file = new File(canonicalFilePathName);
        validate(file);
    }

    /**
     * Validate a file
     *
     * @param file
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static void validate(File file) throws IllegalArgumentException, IOException {
        if (file == null) {
            throw new IllegalArgumentException(" Invalid: file is null.");
        }

        String filePathName = file.getCanonicalPath();
        if (filePathName == null) {
            throw new IllegalArgumentException(" Invalid: filePathName is null.");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException(" Invalid because " + filePathName + " does not exist.");
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException(" Invalid because " + filePathName + " is a directory.");
        }

        if (!file.canRead()) {
            throw new IllegalArgumentException(" Invalid because " + filePathName + " is not read-able.");
        }
    }

    public static void compressToGzipFile(String uncompressedFile, String gzipCompressedFile) throws IOException {
        logger.info("\n uncompressedFile: " + uncompressedFile);
        logger.info(" gzipCompressedFile: " + gzipCompressedFile);
        FileInputStream fis = new FileInputStream(uncompressedFile);
        FileOutputStream fos = new FileOutputStream(gzipCompressedFile);
        GZIPOutputStream gzipOS = new GZIPOutputStream(fos);

        byte[] buffer = new byte[1024];
        int len;

        while ((len = fis.read(buffer)) != -1) {
            gzipOS.write(buffer, 0, len);
        }

        //close resources
        gzipOS.close();
        fos.close();
        fis.close();
    }

    public static void decompressFromGzipFile(String gzipCompressedFile, String uncompressedFile) throws IOException {
        FileInputStream fis = new FileInputStream(gzipCompressedFile);
        GZIPInputStream gis = new GZIPInputStream(fis);
        FileOutputStream fos = new FileOutputStream(uncompressedFile);

        byte[] buffer = new byte[1024];
        int len;

        while ((len = gis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        //close resources
        fos.close();
        gis.close();
    }

    public static AliasListEntry getMatchingAliasListEntry(ListAliasesResult listAliasesResult, String amazonKMSKeyAlias) {
        AliasListEntry matchingAliasListEntry = null;
        if (listAliasesResult != null && 
            listAliasesResult.getAliases() != null && 
            !listAliasesResult.getAliases().isEmpty() && 
            amazonKMSKeyAlias != null && amazonKMSKeyAlias.length() > 0
           ) {
            List<AliasListEntry> aliasListEntryList  = listAliasesResult.getAliases(); 

            for (int i=0; i<aliasListEntryList.size(); i++) {
                AliasListEntry aliasListEntry = aliasListEntryList.get(i);
                String aliasName = aliasListEntry.getAliasName();
                if (aliasName != null) {
                    String suffix = aliasName.substring(Constants.ALIAS.length() + Constants.FORWARD_SLASH_SEPARATOR.length(), aliasName.length());
                    logger.info("\n suffix: " + suffix);
                    if (suffix != null && suffix.equals(amazonKMSKeyAlias)) {
                        matchingAliasListEntry = aliasListEntry;
                    }
                }
            }
        }

        logger.info("\n matchingAliasListEntry: " + matchingAliasListEntry);
        return matchingAliasListEntry;
    }

    public static String getS3KeyPrefix(String dateInString, String fieldName) throws ParseException {
        logger.info("\n\n String getS3KeyPrefix(String dateInString, String fieldName)");
        
        if (dateInString == null || dateInString.length() != 12) {
            throw new IllegalArgumentException("Invalid format for '" + fieldName + "' supplied.  Valid format is <yyyyMMddHHmm>. Please supply valid '" + fieldName + "'.");
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");

        Date fromDate = formatter.parse(dateInString);
        logger.info(formatter.format(fromDate));

        LocalDateTime localDateTime = convertToLocalDateTimeViaMilisecond(fromDate);

        int year = getYear(localDateTime);
        logger.info(" year: " + year);

        int month = getMonth(localDateTime);
        logger.info(" month: " + month);

        int dayOfMonth = getDayOfMonth(localDateTime);
        logger.info(" dayOfMonth: " + dayOfMonth);

        int hour = getHour(localDateTime);
        logger.info(" hour: " + hour);

        int minute = getMinute(localDateTime);
        logger.info(" minute: " + minute);


        String monthString = String.format("%02d", month);
        logger.info(" monthString: " + monthString);

        String dayString = String.format("%02d", dayOfMonth);
        logger.info(" dayString: " + dayString);

        String hourString = String.format("%02d", hour);
        logger.info(" hourString: " + hourString);

        String minuteString = String.format("%02d", minute);
        logger.info(" minuteString: " + minuteString);


        StringBuilder s3KeyPrefixBuilder = new StringBuilder();;

        s3KeyPrefixBuilder.append(awsS3BucketFolder).
                           //append(Constants.FORWARD_SLASH_SEPARATOR).
                           //append(Constants.FEED).
                           //append(Constants.EQUALS_SEPARATOR).
                           //append(feed).
                           append(Constants.FORWARD_SLASH_SEPARATOR).
                           append(Constants.YEAR).
                           append(Constants.EQUALS_SEPARATOR).
                           append(year).
                           append(Constants.FORWARD_SLASH_SEPARATOR).
                           append(Constants.MONTH).
                           append(Constants.EQUALS_SEPARATOR).
                           append(monthString).
                           append(Constants.FORWARD_SLASH_SEPARATOR).
                           append(Constants.DAY).
                           append(Constants.EQUALS_SEPARATOR).
                           append(dayString).
                           append(Constants.FORWARD_SLASH_SEPARATOR).
                           append(Constants.HOUR).
                           append(Constants.EQUALS_SEPARATOR).
                           append(hourString).
                           append(Constants.FORWARD_SLASH_SEPARATOR).
                           append(Constants.MINUTE).
                           append(Constants.EQUALS_SEPARATOR).
                           append(minuteString).
                           append(Constants.FORWARD_SLASH_SEPARATOR);

        return s3KeyPrefixBuilder.toString();
    }

    public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date date) {
        LocalDateTime localDateTime = null;
        if (date != null) {
            localDateTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return localDateTime;
    }

    public static int getYear(LocalDateTime localDateTime) {
        int year = -1;
        if (localDateTime != null) {
            year  = localDateTime.getYear();
        }
        return year;
    }

    public static int getMonth(LocalDateTime localDateTime) {
        int month = -1;
        if (localDateTime != null) {
            month  = localDateTime.getMonthValue();
        }
        return month;
    }

    public static int getDayOfMonth(LocalDateTime localDateTime) {
        int day = -1;
        if (localDateTime != null) {
            day  = localDateTime.getDayOfMonth();
        }
        return day;
    }

    public static int getHour(LocalDateTime localDateTime) {
        int hour = -1;
        if (localDateTime != null) {
            hour  = localDateTime.getHour();
        }
        return hour;
    }

    public static int getMinute(LocalDateTime localDateTime) {
        int minutes = -1;
        if (localDateTime != null) {
            minutes  = localDateTime.getMinute();
        }
        return minutes;
    }

    public static List<S3ObjectSummary> listS3Objects(AmazonS3 s3Client, String bucketName, String fromKey, String toKey) {
        logger.info("\n List<S3ObjectSummary> listS3Objects(AmazonS3 s3Client, String bucketName, String fromKey, String toKey)");
        logger.info("\n s3Client: " + s3Client);
        logger.info("\n bucketName: " + bucketName);
        logger.info("\n fromKey: " + fromKey);
        logger.info("\n toKey: " + toKey);

        List<S3ObjectSummary> s3ObjectSummaryList = new ArrayList<S3ObjectSummary>();

        try {
            // maxKeys is set to 10 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(10);
            //logger.info("\n req: " + req);
            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(req);
                //logger.info("\n result: " + result);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();

                    if (isMatchingS3Object(fromKey, toKey, key)) {
                        s3ObjectSummaryList.add(objectSummary);
                    }
                }

                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                logger.info("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            logger.severe(e.getMessage());
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            logger.severe(e.getMessage());
        }

        //displayS3ObjectSummaryList(s3ObjectSummaryList);
        return s3ObjectSummaryList;
    }

    public static List<S3ObjectSummary> listS3EncryptedObjects(AmazonS3 s3Client, String bucketName, String fromKey, String toKey) {
        logger.info("\n List<S3ObjectSummary> listS3EncryptedObjects(AmazonS3EncryptionClient amazonS3EncryptionClient, String bucketName, String fromKey, String toKey)");
        logger.info("\n amazonS3EncryptionClient: " + amazonS3EncryptionClient);
        logger.info("\n bucketName: " + bucketName);
        logger.info("\n fromKey: " + fromKey);
        logger.info("\n toKey: " + toKey);

        List<S3ObjectSummary> s3ObjectSummaryList = new ArrayList<S3ObjectSummary>();

        try {
            // maxKeys is set to 10 to demonstrate the use of
            // ListObjectsV2Result.getNextContinuationToken()
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(10);
            ListObjectsV2Result result;

            do {
                //result = s3Client.listObjectsV2(req);
                result = amazonS3EncryptionClient.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();

                    if (isMatchingS3Object(fromKey, toKey, key)) {
                        s3ObjectSummaryList.add(objectSummary);
                    }
                }

                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                logger.info("Next Continuation Token: " + token);
                req.setContinuationToken(token);
            } while (result.isTruncated());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            logger.severe(e.getMessage());
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            logger.severe(e.getMessage());
        }

        //displayS3ObjectSummaryList(s3ObjectSummaryList);
        return s3ObjectSummaryList;
    }

    public static boolean isMatchingS3Object(String fromString, String toString, String objectKey) {
        boolean matchFlag = false;

        logger.info(" fromString: " + fromString);
        logger.info(" toString: " + toString);
        logger.info(" objectKey: " + objectKey);

        String objectKeyPrefix = objectKey.substring(0, objectKey.lastIndexOf(File.separator));
        logger.info(" objectKeyPrefix: " + objectKeyPrefix);

        if (fromString.compareTo(objectKeyPrefix) <= 0 && objectKeyPrefix.compareTo(toString) <= 0) {
            matchFlag = true;
        }

        logger.info(" matchFlag: " + matchFlag);
        return matchFlag;
    }

    /*
    public static void displayS3ObjectSummaryList(List<S3ObjectSummary> s3ObjectSummaryList) {
        logger.info(" void displayS3ObjectSummaryList(List<S3ObjectSummary> s3ObjectSummaryList)");
        if (s3ObjectSummaryList != null && !s3ObjectSummaryList.isEmpty()) {
            int i = 0;
            for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaryList) {
                logger.info(" (" + i++ + ") s3ObjectSummary: " + s3ObjectSummary);
            }
        }
    }
    */

    public static void displayConfigProperties() {
        logger.info("\n void displayConfigProperties()");

        logger.info(" stack: " + stack);
        logger.info(" awsRegion: " + awsRegion);
        logger.info(" awsAccessKeyID: " + awsAccessKeyID);

        logger.info(" awsSecretAccessKey: " + awsSecretAccessKey);
        logger.info(" awsS3Bucket: " + awsS3Bucket);
        logger.info(" awsS3BucketFolder: " + awsS3BucketFolder);

        logger.info(" awsIamKmsCMK: " + awsIamKmsCMK);
        logger.info(" ingressFolder: " + ingressFolder);
        logger.info(" zipFolder: " + zipFolder);

        logger.info(" uploadFolder: " + uploadFolder);
        logger.info(" compressDecompressFolder: " + compressDecompressFolder);
        logger.info(" downloadFolder: " + downloadFolder);

        logger.info(" pollingIntervalInSeconds: " + pollingIntervalInSeconds);
        logger.info(" initialPollingDelayInSeconds: " + initialPollingDelayInSeconds);
        logger.info(" fileAgeInSeconds: " + fileAgeInSeconds);

        logger.info(" kmsRegionUrl: " + kmsRegionUrl);
        logger.info(" fileTransferInitialWaitTimeInMilliSeconds: " + fileTransferInitialWaitTimeInMilliSeconds);
        logger.info(" fileTransferRetryCount: " + fileTransferRetryCount);

        logger.info(" awsCredentials: " + awsCredentials);
        logger.info(" amazonS3EncryptionClient: " + amazonS3EncryptionClient);
        logger.info(" encryptedTransferManager: " + encryptedTransferManager);
    }

    public static String getConfigParameterValue(List<ConfigParameter> configParameterList, String name) {
        String value = null;
        if (configParameterList != null && !configParameterList.isEmpty() && name != null) {
            for (int i=0; i<configParameterList.size(); i++) {
                ConfigParameter configParameter = configParameterList.get(i);
                if (configParameter != null && configParameter.getName() != null && configParameter.getName().length()>0 && configParameter.getName().equals(name)) {
                    value = configParameter.getValue();
                }
            }
        }
        return value;
    }

    public static String getStack(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_STACK);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_STACK + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getIngressFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_INGRESS_FOLDER);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_INGRESS_FOLDER + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getZipFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_ZIP_FOLDER);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_ZIP_FOLDER + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getUploadFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_UPLOAD_FOLDER);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_UPLOAD_FOLDER + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getCompressDecompressFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_COMPRESS_DECOMPRESS_DIRECTORY);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_COMPRESS_DECOMPRESS_DIRECTORY + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getDownloadFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_DOWNLOAD_FOLDER);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_DOWNLOAD_FOLDER + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getAWSRegion(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_REGION);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getIamKmsCMK(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_IAM_KMS_CMK);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_IAM_KMS_CMK + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getAWSAccessKeyID(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getAWSSecretAccessKey(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getAWSS3Bucket(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_S3_BUCKET);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_S3_BUCKET + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getAWSS3BucketFolder(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_AWS_S3_BUCKET_FOLDER);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_S3_BUCKET_FOLDER + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getPollingIntervalInSeconds(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_POLLING_INTERVAL_IN_SECONDS);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_POLLING_INTERVAL_IN_SECONDS + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getInitialPollingDelayInSeconds(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_INITIAL_POLLING_DELAY_IN_SECONDS);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_INITIAL_POLLING_DELAY_IN_SECONDS + ".  Please populate and retry.");
        }
        return value;
    }

    public static String getFileAgeInSeconds(List<ConfigParameter> configParameterList) {
        String value = getConfigParameterValue(configParameterList, Constants.CONFIGPARAMETERNAME_FILE_AGE_IN_SECONDS);
        if (value == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_FILE_AGE_IN_SECONDS + ".  Please populate and retry.");
        }
        return value;
    }

    public static void validate(List<ConfigParameter> configParameterList) {
        logger.info("\n\n void validate(List<ConfigParameter> commonConfigParameterList) ");
        if (configParameterList == null || configParameterList.isEmpty()) {
            throw new IllegalArgumentException("There are no common config parameters available.  Please populate required common config parameters.");
        }
        logger.info("\n configParameterList.size(): " + configParameterList.size());

        stack = getStack(configParameterList);
        logger.info("\n stack: " + stack);

        awsRegion = getAWSRegion(configParameterList);
        logger.info("\n awsRegion: " + awsRegion);

        awsAccessKeyID = getAWSAccessKeyID(configParameterList);
        logger.info("\n awsAccessKeyID: " + awsAccessKeyID);

        awsSecretAccessKey = getAWSSecretAccessKey(configParameterList);
        logger.info("\n awsSecretAccessKey: " + awsSecretAccessKey);

        awsS3Bucket = getAWSS3Bucket(configParameterList);
        logger.info("\n awsS3Bucket: " + awsS3Bucket);

        awsS3BucketFolder = getAWSS3BucketFolder(configParameterList);
        logger.info("\n awsS3BucketFolder: " + awsS3BucketFolder);

        awsIamKmsCMK = getIamKmsCMK(configParameterList);
        logger.info("\n awsIamKmsCMK: " + awsIamKmsCMK);

        ingressFolder = getIngressFolder(configParameterList);
        logger.info("\n ingressFolder: " + ingressFolder);

        zipFolder = getZipFolder(configParameterList);
        logger.info("\n zipFolder: " + zipFolder);

        uploadFolder = getUploadFolder(configParameterList);
        logger.info("\n uploadFolder: " + uploadFolder);

        compressDecompressFolder = getCompressDecompressFolder(configParameterList);
        logger.info("\n compressDecompressFolder: " + compressDecompressFolder);

        downloadFolder = getDownloadFolder(configParameterList);
        logger.info("\n downloadFolder: " + downloadFolder);

        pollingIntervalInSecondsStr = getPollingIntervalInSeconds(configParameterList);
        logger.info("\n pollingIntervalInSecondsStr: " + pollingIntervalInSecondsStr);

        if (pollingIntervalInSecondsStr != null) {
            Long pollingIntervalInSecondsLong = Long.parseLong(pollingIntervalInSecondsStr);
            if (pollingIntervalInSecondsLong != null) {
                pollingIntervalInSeconds = pollingIntervalInSecondsLong.longValue();
            }
        }

        initialPollingDelayInSecondsStr = getInitialPollingDelayInSeconds(configParameterList);
        logger.info("\n initialPollingDelayInSecondsStr: " + initialPollingDelayInSecondsStr);

        if (initialPollingDelayInSecondsStr != null) {
            Long initialPollingDelayInSecondsLong = Long.parseLong(initialPollingDelayInSecondsStr);
            if (initialPollingDelayInSecondsLong != null) {
                initialPollingDelayInSeconds = initialPollingDelayInSecondsLong.longValue();
            }
        }

        fileAgeInSecondsStr = getFileAgeInSeconds(configParameterList);
        logger.info("\n fileAgeInSecondsStr: " + fileAgeInSecondsStr);

        if (fileAgeInSecondsStr != null) {
            Long fileAgeInSecondsLong = Long.parseLong(fileAgeInSecondsStr);
            if (fileAgeInSecondsLong != null) {
                fileAgeInSeconds = fileAgeInSecondsLong.longValue();
            }
        }
    }

    public static void initialize(ConfigParameterService configParameterService, KMSRegionService kmsRegionService, EncryptionKeyService encryptionKeyService, S3ObjectUploadService s3ObjectUploadService) throws Exception {
        logger.info("\n\n void initialize(ConfigParameterService configParameterService, KMSRegionService kmsRegionService, EncryptionKeyService encryptionKeyService, S3ObjectUploadService s3ObjectUploadService)");

        if (configParameterService == null) {
            throw new IllegalArgumentException("Invalid configParameterService");
        }

        if (kmsRegionService == null) {
            throw new IllegalArgumentException("Invalid kmsRegionService");
        }

        if (encryptionKeyService == null) {
            throw new IllegalArgumentException("Invalid encryptionKeyService");
        }

        if (s3ObjectUploadService == null) {
            throw new IllegalArgumentException("Invalid s3ObjectUploadService");
        }

        List<ConfigParameter> configParameterList = configParameterService.findAll();
        if (configParameterList == null || configParameterList.isEmpty()) {
            throw new IllegalArgumentException("There are no common config parameters available.  Please populate required common config parameters.");
        }

        validate(configParameterList);

        isValidDirectory(ingressFolder);

        isValidDirectory(zipFolder);

        isValidDirectory(uploadFolder);

        isValidDirectory(compressDecompressFolder);

        isValidDirectory(downloadFolder);


        awsCredentials = constructAWSCredentials(awsAccessKeyID, awsSecretAccessKey);

        AWSKMS awsKMSClient = constructAWSKMSClient(awsRegion, awsCredentials);

        KeyMetadata keyMetadata = getKMSCustomerMasterKey(awsKMSClient, awsIamKmsCMK);
        if (keyMetadata == null) {
            throw new IllegalArgumentException("There exists no CMK for keyAlias: " + awsIamKmsCMK + " in region: " + awsRegion + ".  This is a pre-requisite.  Please create CMK Key and retry.");
        }
        logger.info("\n keyMetadata: " + keyMetadata);

        Boolean enabledFlag = keyMetadata.isEnabled();
        if (!enabledFlag) {
            throw new IllegalArgumentException("The CMK for keyAlias: " + awsIamKmsCMK + " in region: " + awsRegion + " is not enabled.  This is a pre-requisite.  Please enable the CMK Key and retry.");
        }

        String clientMasterKeyId = keyMetadata.getArn();
        if (clientMasterKeyId == null) {
            throw new IllegalArgumentException("Invalid clientMasterKeyId.  Please ensure if " + Constants.CONFIGPARAMETERNAME_AWS_IAM_KMS_CMK + " is valid.");
        }
        logger.info("\n clientMasterKeyId: " + clientMasterKeyId);

        List<KMSRegion> kmsRegionList = kmsRegionService.findByName(awsRegion);
        if (kmsRegionList == null || kmsRegionList.isEmpty()) {
            throw new IllegalArgumentException("There are no KMS regions available.  Please populate required KMS regions.");
        }

        KMSRegion kmsRegion = kmsRegionList.get(0);
        if (kmsRegion == null) {
            throw new IllegalArgumentException("There are no matching KMS region for: " + awsRegion + ".  Please populate required KMS regions.");
        }

        kmsRegionUrl = kmsRegion.getUrl();
        if (kmsRegionUrl == null) {
            throw new IllegalArgumentException("Missing KMS region url for: " + awsRegion + ".  Please populate and retry.");
        }
        logger.info("\n kmsRegionUrl: " + kmsRegionUrl);


        EnvelopeEncryptionService envelopeEncryptionService = constructEnvelopeEncryptionService(awsCredentials, kmsRegionUrl, clientMasterKeyId);
        logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

        SecretKey secretKey = null;
        String base64EncodedSecretKeyString = null;

        EnvelopeEncryptedMessage envelopeEncryptedMessage = null;

        EncryptionKey latestEncryptionKey = getLatestEncryptionKey(encryptionKeyService);
        if (latestEncryptionKey == null) {
            logger.info("\n\n Create SecretKey");

            secretKey = SecurityUtil.getSecretKey();
            logger.info("\n secretKey: " + secretKey);

            EncryptionKey encryptionKey = getEncryptionKey(envelopeEncryptionService, secretKey);
            if (encryptionKey == null) {
                throw new IllegalArgumentException("Invalid encryptionKey");
            }
            logger.info("\n encryptionKey: " + encryptionKey);

            encryptionKeyService.save(encryptionKey);
            logger.info("\n Successfully persisted encryptionKey: " + encryptionKey);
        } else {
            logger.info("\n\n Use existing SecretKey");
            secretKey = getSecretKey(envelopeEncryptionService, latestEncryptionKey);

            if (secretKey == null) {
                throw new IllegalArgumentException("Invalid secretKey");
            }
            logger.info("\n secretKey: " + secretKey);
        }

        encryptedTransferManager = constructEncryptedTransferManager(awsCredentials, secretKey);

        displayConfigProperties();

        schedulerService(pollingIntervalInSeconds, initialPollingDelayInSeconds, ingressFolder, zipFolder, fileAgeInSeconds, uploadFolder, s3ObjectUploadService);
    }

    public static EnvelopeEncryptionService constructEnvelopeEncryptionService(AWSCredentials awsCredentials, String kmsRegionUrl, String clientMasterKeyId) {
        EnvelopeEncryptionService envelopeEncryptionService = null;

        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid awsCredentials");
        }

        if (kmsRegionUrl == null || kmsRegionUrl.length() <= 0) {
            throw new IllegalArgumentException("Invalid kmsRegionUrl");
        }

        if (clientMasterKeyId == null || clientMasterKeyId.length() <= 0) {
            throw new IllegalArgumentException("Invalid clientMasterKeyId");
        }

        envelopeEncryptionService = new EnvelopeEncryptionService(awsCredentials, kmsRegionUrl, clientMasterKeyId);
        if (envelopeEncryptionService == null) {
            throw new IllegalArgumentException("Invalid envelopeEncryptionService");
        }
        logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

        return envelopeEncryptionService;
    }

    public static EncryptionKey getLatestEncryptionKey(EncryptionKeyService encryptedKeyService) {
        logger.info("\n Util::getLatestEncryptionKey()");
        EncryptionKey encryptionKey = null;

        List<EncryptionKey> encryptionKeyList = encryptedKeyService.findLatest();
        if (encryptionKeyList != null && !encryptionKeyList.isEmpty()) {
            logger.info("\n encryptionKeyList.size(): " + encryptionKeyList.size());

            for (int i=0; i<encryptionKeyList.size(); i++) {
                EncryptionKey r = encryptionKeyList.get(i);
                logger.info("\n r[" + i + "]: " + r);
            }

            encryptionKey = encryptionKeyList.get(0);
        }

        return encryptionKey;
    }

    private static EncryptionKey createEnvelopeEncryptionKey(String awsRegion, String stack, String kmsKeyAlias, String secretKey, String encodedSecretKey, byte[] encryptedKeyByteArray, String ciphertext) {
        String status = Constants.OBJECT_STORAGE_KEY_STORE_STATUS_ENABLED;

        Timestamp qvCreatedTs = null;  //FIXME:  Populate current time here
        String qvCreatedSrc = "";
        Timestamp qvUpdatedTs = null;
        String qvUpdatedSrc = "";

        EncryptionKey encryptionKey = new EncryptionKey(stack,
                                                        awsRegion,
                                                        kmsKeyAlias,
                                                        secretKey,
                                                        encodedSecretKey,
                                                        encryptedKeyByteArray,
                                                        ciphertext,
                                                        status,
                                                        qvCreatedTs,
                                                        qvCreatedSrc,
                                                        qvUpdatedTs,
                                                        qvUpdatedSrc
                                                       );

        logger.info("\n encryptionKey: " + encryptionKey);
        return encryptionKey;
    }

    public static SecretKey convertBase64EncodedStringToSecretKey(String base64EncodedString) {
        SecretKey secretKey = null;
        if (base64EncodedString != null) {
            //Decode the Base64 encoded string
            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedString);
            
            //Rebuild key using SecretKeySpec
            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, Constants.KEY_GENERATOR_ALGORITHM_AES);
        }
        return secretKey;
    }

    public static KeyMetadata getKMSCustomerMasterKey(String awsRegion, String amazonKMSKeyAlias) {
        logger.info("\n KeyMetadata getKMSCustomerMasterKey(String awsRegion, String amazonKMSKeyAlias)");
        KeyMetadata keyMetadata = null;

        if (awsRegion == null) {
            throw new IllegalArgumentException("Invalid AWS region supplied.  Please supply valid AWS region.");
        }

        if (amazonKMSKeyAlias == null || amazonKMSKeyAlias.length() == 0) {
            throw new IllegalArgumentException("Invalid AWS KMS Key Alias supplied.  Please supply valid AWS KMS Key Alias.");
        }

        logger.info("\n awsRegion: " + awsRegion);
        logger.info(" amazonKMSKeyAlias: " + amazonKMSKeyAlias);

        Region region = RegionUtils.getRegion(awsRegion);
        if (region == null) {
            throw new IllegalArgumentException("The supplied AWS Region: " + awsRegion + " is invalid.  Please supply valid AWS Region.");
        }
        logger.info(" region: " + region);

        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid " + Constants.CONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + " and " + Constants.CONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please supply valid values and retry.");
        }


        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                                              .withCredentials(new StaticCredentialsProvider(awsCredentials))
                                              .withRegion(region.getName())
                                              .build();
        logger.info("\n kmsClient: " + kmsClient);


        ListAliasesRequest listAliasesRequest = new ListAliasesRequest();
        logger.info("\n listAliasesRequest: " + listAliasesRequest);

        ListAliasesResult listAliasesResult = kmsClient.listAliases(listAliasesRequest);
        logger.info("\n listAliasesResult: " + listAliasesResult);

        AliasListEntry matchingAliasListEntry = getMatchingAliasListEntry(listAliasesResult, amazonKMSKeyAlias);
        if (matchingAliasListEntry != null) {
            String targetKeyId = matchingAliasListEntry.getTargetKeyId();
            if (targetKeyId != null) {
                DescribeKeyRequest describeKeyRequest = new DescribeKeyRequest().withKeyId(targetKeyId);
                logger.info("\n describeKeyRequest: " + describeKeyRequest);

                DescribeKeyResult describeKeyResult = kmsClient.describeKey(describeKeyRequest);
                if (describeKeyResult != null) {
                    logger.info("\n describeKeyResult: " + describeKeyResult);
                    keyMetadata = describeKeyResult.getKeyMetadata();
                }
            }
        }

        logger.info("\n keyMetadata: " + keyMetadata);
        return keyMetadata;
    }

    public static TransferManager constructEncryptedTransferManager(AWSCredentials awsCredentials, SecretKey secretKey) {
        logger.info("\n\n TransferManager constructEncryptedTransferManager(AWSCredentials awsCredentials, SecretKey secretKey)");

        TransferManager transferManager = null;

        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid awsCredentials");
        }

        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid secretKey");
        }

        if (awsCredentials != null && secretKey != null) {
            try {
                EncryptionMaterials encryptionMaterials = constructAESSymmetricKeyEncryptionMaterials(secretKey);
                if (encryptionMaterials != null) {
                    logger.info("\n encryptionMaterials: " + encryptionMaterials);
                    amazonS3EncryptionClient = createAmazonS3EncryptionClient(awsCredentials, encryptionMaterials);
                    if (amazonS3EncryptionClient != null) {
                        logger.info("\n amazonS3EncryptionClient: " + amazonS3EncryptionClient);

                        transferManager = TransferManagerBuilder.standard()
                                                                .withS3Client(amazonS3EncryptionClient)
                                                                .build();
                    }
                }
            } catch(NoSuchAlgorithmException e) {
                logger.severe(e.getMessage());
            }
        }

        return transferManager;
    }

    public static EncryptionMaterials constructAESSymmetricKeyEncryptionMaterials(SecretKey secretKey) throws NoSuchAlgorithmException {
        EncryptionMaterials encryptionMaterials = null;
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid secretKey");
        }

        encryptionMaterials = new EncryptionMaterials(secretKey);

        if (encryptionMaterials == null) {
            throw new IllegalArgumentException("Invalid encryptionMaterials");
        }

        logger.info("\n encryptionMaterials: " + encryptionMaterials);
        return encryptionMaterials;
    }

    public static AmazonS3EncryptionClient createAmazonS3EncryptionClient(AWSCredentials awsCredentials, EncryptionMaterials encryptionMaterials) {
        AmazonS3EncryptionClient amazonS3EncryptionClient = null;

        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid awsCredentials");
        }

        if (encryptionMaterials == null) {
            throw new IllegalArgumentException("Invalid encryptionMaterials");
        }

        amazonS3EncryptionClient = new AmazonS3EncryptionClient(awsCredentials, encryptionMaterials);
        if (amazonS3EncryptionClient == null) {
            throw new IllegalArgumentException("Invalid amazonS3EncryptionClient");
        }
        logger.info("\n amazonS3EncryptionClient: " + amazonS3EncryptionClient);

        return amazonS3EncryptionClient;
    }

    public static KeyMetadata getKMSCustomerMasterKey(AWSKMS kmsClient, String amazonKMSKeyAlias) {
        logger.info("\n KeyMetadata getKMSCustomerMasterKey(AWSKMS kmsClient, String amazonKMSKeyAlias)");
        KeyMetadata keyMetadata = null;

        if (kmsClient == null) {
            throw new IllegalArgumentException("Invalid kmsClient");
        }

        if (amazonKMSKeyAlias == null || amazonKMSKeyAlias.length() == 0) {
            throw new IllegalArgumentException("Invalid AWS KMS Key Alias supplied.  Please supply valid AWS KMS Key Alias.");
        }

        ListAliasesRequest listAliasesRequest = new ListAliasesRequest();
        logger.info("\n listAliasesRequest: " + listAliasesRequest);

        ListAliasesResult listAliasesResult = kmsClient.listAliases(listAliasesRequest);
        logger.info("\n listAliasesResult: " + listAliasesResult);

        AliasListEntry matchingAliasListEntry = getMatchingAliasListEntry(listAliasesResult, amazonKMSKeyAlias);
        if (matchingAliasListEntry != null) {
            String targetKeyId = matchingAliasListEntry.getTargetKeyId();
            if (targetKeyId != null) {
                DescribeKeyRequest describeKeyRequest = new DescribeKeyRequest().withKeyId(targetKeyId);
                logger.info("\n describeKeyRequest: " + describeKeyRequest);

                DescribeKeyResult describeKeyResult = kmsClient.describeKey(describeKeyRequest);
                if (describeKeyResult != null) {
                    logger.info("\n describeKeyResult: " + describeKeyResult);
                    keyMetadata = describeKeyResult.getKeyMetadata();
                }
            }
        }

        logger.info("\n keyMetadata: " + keyMetadata);
        return keyMetadata;
    }

    public static AWSCredentials constructAWSCredentials(String awsAccessKeyID, String awsSecretAccessKey) {
        AWSCredentials awsCredentials = null;

        if (awsAccessKeyID == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + ".  Please populate and retry.");
        }

        if (awsSecretAccessKey == null) {
            throw new IllegalArgumentException("Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please populate and retry.");
        }

        awsCredentials = new BasicAWSCredentials(awsAccessKeyID, awsSecretAccessKey);
        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid " + Constants.CONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + " and " + Constants.CONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please supply valid values and retry.");
        }
        logger.info("\n awsCredentials: " + awsCredentials);

        return awsCredentials;
    }

    public static AWSKMS constructAWSKMSClient(String awsRegion, AWSCredentials awsCredentials) {
        logger.info("\n AWSKMS constructAWSKMSClient(String awsRegion, AWSCredentials awsCredentials)");
        AWSKMS kmsClient = null;

        if (awsRegion == null) {
            throw new IllegalArgumentException("Invalid AWS region supplied.  Please supply valid AWS region.");
        }

        if (awsCredentials == null) {
            throw new IllegalArgumentException("Invalid awsCredentials supplied.  Please supply valid awsCredentials.");
        }

        Region region = RegionUtils.getRegion(awsRegion);
        if (region == null) {
            throw new IllegalArgumentException("The supplied AWS Region: " + awsRegion + " is invalid.  Please supply valid AWS Region.");
        }
        logger.info(" region: " + region);

        kmsClient = AWSKMSClientBuilder.standard()
                                       .withCredentials(new StaticCredentialsProvider(awsCredentials))
                                       .withRegion(region.getName())
                                       .build();

        if (kmsClient == null) {
            throw new IllegalArgumentException("Invalid kmsClient.");
        }
        logger.info("\n kmsClient: " + kmsClient);

        return kmsClient;
    }

    public static EncryptionKey getEncryptionKey(EnvelopeEncryptionService envelopeEncryptionService, SecretKey secretKey) {
        logger.info("\n\n EncryptionKey getEncryptionKey(EnvelopeEncryptionService envelopeEncryptionService, SecretKey secretKey)");
        EncryptionKey encryptionKey = null;

        if (envelopeEncryptionService == null) {
            throw new IllegalArgumentException("Invalid envelopeEncryptionService");
        }
        logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid secretKey");
        }
        logger.info("\n secretKey: " + secretKey);

        String secretKeyStr = secretKey.toString();
        logger.info("\n secretKeyStr: " + secretKeyStr);

        //Get Base64 encoded version of the key
        String base64EncodedSecretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        if (base64EncodedSecretKeyString == null) {
            throw new IllegalArgumentException("Invalid base64EncodedSecretKeyString");
        }
        logger.info("\n base64EncodedSecretKeyString: " + base64EncodedSecretKeyString);
        logger.info("\n base64EncodedSecretKeyString.length(): " + base64EncodedSecretKeyString.length());

        String modifiedBase64EncodedSecretKeyString = Constants.SECRET_KEY_PREFIX + base64EncodedSecretKeyString;
        if (modifiedBase64EncodedSecretKeyString == null) {
            throw new IllegalArgumentException("Invalid modifiedBase64EncodedSecretKeyString");
        }
        logger.info("\n modifiedBase64EncodedSecretKeyString: " + modifiedBase64EncodedSecretKeyString);
        logger.info("\n modifiedBase64EncodedSecretKeyString.length(): " + modifiedBase64EncodedSecretKeyString.length());


        logger.info("\n\n ENCRYPT");

        EnvelopeEncryptedMessage envelopeEncryptedMessage = envelopeEncryptionService.encrypt(modifiedBase64EncodedSecretKeyString);
        if (envelopeEncryptedMessage == null) {
            throw new IllegalArgumentException("Invalid envelopeEncryptedMessage");
        }
        logger.info(" envelopeEncryptedMessage: " + envelopeEncryptedMessage);

        byte[] encryptedKeyByteArray = envelopeEncryptedMessage.getEncryptedKey();
        if (encryptedKeyByteArray == null) {
            throw new IllegalArgumentException("Invalid encryptedKeyByteArray");
        }

        String encryptedKey = Arrays.toString(encryptedKeyByteArray);
        logger.info(" encryptedKey: " + encryptedKey);
        logger.info(" encryptedKey.length(): " + encryptedKey.length());

        String ciphertext = envelopeEncryptedMessage.getCiphertext();
        if (ciphertext == null) {
            throw new IllegalArgumentException("Invalid ciphertext");
        }

        logger.info(" ciphertext: " + ciphertext);
        logger.info(" ciphertext.length(): : " + ciphertext.length());

        encryptionKey = createEnvelopeEncryptionKey(awsRegion, stack, awsIamKmsCMK, secretKeyStr, modifiedBase64EncodedSecretKeyString, encryptedKeyByteArray, ciphertext);

        return encryptionKey;
    }

    public static SecretKey getSecretKey(EnvelopeEncryptionService envelopeEncryptionService, EncryptionKey encryptionKey) {
        logger.info("\n\n SecretKey getSecretKey(EnvelopeEncryptionService envelopeEncryptionService, EncryptionKey encryptionKey)");
        SecretKey secretKey = null;

        if (envelopeEncryptionService == null) {
            throw new IllegalArgumentException("Invalid envelopeEncryptionService");
        }
        logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

        if (encryptionKey == null) {
            throw new IllegalArgumentException("Invalid encryptionKey");
        }
        logger.info("\n encryptionKey: " + encryptionKey);

        byte[] encryptedKeyByteArray = encryptionKey.getEncryptedKey();
        if (encryptedKeyByteArray == null) {
            throw new IllegalArgumentException("Invalid encryptedKeyByteArray");
        }
        logger.info("\n\n encryptedKeyByteArray: " + encryptedKeyByteArray);

        String ciphertext = encryptionKey.getCiphertext();
        if (ciphertext == null) {
            throw new IllegalArgumentException("Invalid ciphertext");
        }
        logger.info("\n\n ciphertext: " + ciphertext);
        logger.info(" ciphertext.length(): " + ciphertext.length());


        EnvelopeEncryptedMessage envelopeEncryptedMessage = new EnvelopeEncryptedMessage();
        envelopeEncryptedMessage.setEncryptedKey(encryptedKeyByteArray);
        envelopeEncryptedMessage.setCiphertext(ciphertext);
        logger.info("\n\n envelopeEncryptedMessage: " + envelopeEncryptedMessage);

        String decryptedString = envelopeEncryptionService.decrypt(envelopeEncryptedMessage);
        if (decryptedString == null) {
            throw new IllegalArgumentException("Invalid decryptedString");
        }
        logger.info("\n decryptedString: " + decryptedString);
        logger.info("\n decryptedString.length(): " + decryptedString.length());

        String base64EncodedSecretKeyString = decryptedString.substring(Constants.SECRET_KEY_PREFIX.length(), decryptedString.length());
        if (base64EncodedSecretKeyString == null) {
            throw new IllegalArgumentException("Invalid base64EncodedSecretKeyString");
        }
        System.out.println("\n base64EncodedSecretKeyString: " + base64EncodedSecretKeyString);
        logger.info("\n base64EncodedSecretKeyString.length(): " + base64EncodedSecretKeyString.length());

        secretKey = convertBase64EncodedStringToSecretKey(base64EncodedSecretKeyString);
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid secretKey");
        }

        return secretKey;
    }

    private static void schedulerService(long pollingIntervalInSeconds, long initialPollingDelayInSeconds, String ingressFolder, String zipFolder, long fileAgeInSeconds, String uploadFolder, S3ObjectUploadService s3ObjectUploadService) throws InterruptedException, ExecutionException {
        logger.info("\n\n\n\n void schedulerService(long pollingIntervalInSeconds, long initialPollingDelayInSeconds, String ingressFolder, String zipFolder, long fileAgeInSeconds, String uploadFolder, S3ObjectUploadService s3ObjectUploadService)");

        //ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        logger.info("\n scheduledExecutorService: " + scheduledExecutorService);

        scheduledExecutorService.scheduleWithFixedDelay(new ZipRunnable(ingressFolder, zipFolder, fileAgeInSeconds, uploadFolder, s3ObjectUploadService), pollingIntervalInSeconds, initialPollingDelayInSeconds, TimeUnit.SECONDS);
    }

    public static void moveDirectoryContents(String fromDir, String toDir, long fileAgeInSeconds) {
        if (fromDir != null) {
            File sourceDir = new File(fromDir);

            try {
                File[] files = sourceDir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        logger.info("\n directory: " + file.getCanonicalPath());
                        moveDirectoryContents(file.getCanonicalPath(), toDir, fileAgeInSeconds);
                    } else {
                        long diff = new Date().getTime() - file.lastModified();
            
                        if (diff > fileAgeInSeconds * MILLISECOND_CONVERTING_FACTOR) {
                            logger.info(" file: " + file.getCanonicalPath());
                            file.renameTo(new File(toDir + File.separator + file.getName()));
                        }
                    }
                }
            } catch(IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public static void upload(String sourceFile, String destinationDir, S3ObjectUploadService s3ObjectUploadService) {
        logger.info("\n void uploadZip(String sourceFile, String destinationDir, S3ObjectUploadService s3ObjectUploadService)");

        if (sourceFile != null &&
            sourceFile.length() > 0 &&
            destinationDir != null &&
            destinationDir.length() > 0) {

            logger.info(" sourceFile: " + sourceFile);
            logger.info(" destinationDir: " + destinationDir);

            File source = new File(sourceFile);

            String zipFilePath = destinationDir + File.separator + source.getName();

            source.renameTo(new File(zipFilePath));

            uploadToS3(zipFilePath, s3ObjectUploadService);
        }
    }

    public static void uploadToS3(String canonicalFilePathName, S3ObjectUploadService s3ObjectUploadService) {
        logger.info("\n void uploadToS3(String canonicalFilePathName, S3ObjectUploadService s3ObjectUploadService)");

        if (canonicalFilePathName != null && 
            canonicalFilePathName.length() > 0 &&
            s3ObjectUploadService != null) {
            logger.info("\n\n canonicalFilePathName: " + canonicalFilePathName);
            logger.info("\n s3ObjectUploadService: " + s3ObjectUploadService);
        }

        String dataSourceType = null;  //Not needed
        String feed = null;  //Not needed

        try {
            validate(canonicalFilePathName);

            CompletableFuture<Boolean> completableFutureStatus = s3ObjectUploadService.upload(canonicalFilePathName, dataSourceType, feed, fileTransferInitialWaitTimeInMilliSeconds);

            //Wait until they are all done
            CompletableFuture.allOf(completableFutureStatus).join();

            if (completableFutureStatus != null) {
                logger.info("\n\n completableFutureStatus: " + completableFutureStatus);
                Boolean status = completableFutureStatus.get();
                logger.info("\n status: " + status);
            }
        } catch(Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public static void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}