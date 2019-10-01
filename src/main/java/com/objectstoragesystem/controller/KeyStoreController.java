package com.objectstoragesystem.controller;



import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import java.sql.Timestamp;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.kms.model.KeyMetadata;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;

import com.objectstoragesystem.entity.EncryptionKey;
import com.objectstoragesystem.exception.InternalServerException;
import com.objectstoragesystem.security.EnvelopeEncryptedMessage;
import com.objectstoragesystem.security.EnvelopeEncryptionService;
import com.objectstoragesystem.service.EncryptionKeyService;
import com.objectstoragesystem.util.Constants;
import com.objectstoragesystem.util.SecurityUtil;
import com.objectstoragesystem.util.Util;


@RestController
@RequestMapping(Constants.V1_KEYSTORE_SERVICE_RESOURCE_URI)
public class KeyStoreController {
    private final static Logger logger = Logger.getLogger(KeyStoreController.class.getName());

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    @RequestMapping(method=RequestMethod.GET, value = {"", "/aws/kms/alias/{awsRegion}/{kmsKeyAlias}"})
    @ResponseBody
    public KeyMetadata getAmazonKMSKey(@PathVariable Optional<String> awsRegion, @PathVariable Optional<String> kmsKeyAlias) {
        logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> awsRegion, @PathVariable Optional<String> kmsKeyAlias)");

        KeyMetadata keyMetadata = null;


        if (!awsRegion.isPresent()) {
            logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::awsRegion is not present");
        }

        if (!kmsKeyAlias.isPresent()) {
            logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::kmsKeyAlias is not present");
        }

        if (awsRegion.isPresent() && kmsKeyAlias.isPresent()) {
            String awsRegionStr = awsRegion.get();
            if (awsRegionStr != null && awsRegionStr.length() > 0) {
                logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::awsRegionStr is present");
            }

            String kmsKeyAliasStr = kmsKeyAlias.get();
            if (kmsKeyAliasStr != null && kmsKeyAliasStr.length() > 0) {
                logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::kmsKeyAliasStr is present");
            }

            keyMetadata = Util.getKMSCustomerMasterKey(awsRegionStr, kmsKeyAliasStr);
            if (keyMetadata == null) {
                throw new InternalServerException("There exists no CMK for keyAlias: " + kmsKeyAliasStr + " in region: " + awsRegionStr + ".  This is a pre-requisite.  Please create CMK Key and retry.");
            }

            Boolean enabledFlag = keyMetadata.isEnabled();
            if (!enabledFlag) {
                throw new InternalServerException("The CMK for keyAlias: " + kmsKeyAliasStr + " in region: " + awsRegionStr + " is not enabled.  This is a pre-requisite.  Please enable the CMK Key and retry.");
            }
        }

        return keyMetadata;
    }

    @RequestMapping(method=RequestMethod.POST, value = {"/rotateKey/{stack}/{awsRegion}/{kmsKeyAlias}"})
    @ResponseBody
    public EncryptionKey createEnvelopeEncryption(@PathVariable Optional<String> stack, @PathVariable Optional<String> awsRegion, @PathVariable Optional<String> kmsKeyAlias) {
        logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> stack, @PathVariable Optional<String> awsRegion, @PathVariable Optional<String> kmsKeyAlias)");

        EncryptionKey resultEnvelopeEncryptionKey = null;
        KeyMetadata keyMetadata = null;
        EnvelopeEncryptedMessage envelopeEncryptedMessage = null;
        SecretKey secretKey = null;
        String secretKeyStr = null;
        String base64EncodedSecretKeyString = null;


        if (!stack.isPresent()) {
            logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::stack is not present");
        }

        if (!awsRegion.isPresent()) {
            logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::awsRegion is not present");
        }

        if (!kmsKeyAlias.isPresent()) {
            logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::kmsKeyAlias is not present");
        }

        if (stack.isPresent() && awsRegion.isPresent() && kmsKeyAlias.isPresent()) {
            String stackStr = stack.get();
            if (stackStr != null && stackStr.length() > 0) {
                logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::stackStr is present");
            }

            String awsRegionStr = awsRegion.get();
            if (awsRegionStr != null && awsRegionStr.length() > 0) {
                logger.info("\n KeyStoreController::getAmazonKMSKey(@PathVariable Optional<String> kmsKeyAlias)::awsRegionStr is present");
            }

            String kmsKeyAliasStr = kmsKeyAlias.get();
            if (kmsKeyAliasStr != null && kmsKeyAliasStr.length() > 0) {
                logger.info("\n KeyStoreController::createEnvelopeEncryption(@PathVariable Optional<String> kmsKeyAlias)::kmsKeyAliasStr is present");
            }

            keyMetadata = Util.getKMSCustomerMasterKey(awsRegionStr, kmsKeyAliasStr);
            if (keyMetadata == null) {
                throw new InternalServerException("There exists no CMK for keyAlias: " + kmsKeyAliasStr + " in region: " + awsRegionStr + ".  This is a pre-requisite.  Please create CMK Key and retry.");
            }

            Boolean enabledFlag = keyMetadata.isEnabled();
            if (!enabledFlag) {
                throw new InternalServerException("The CMK for keyAlias: " + kmsKeyAliasStr + " in region: " + awsRegionStr + " is not enabled.  This is a pre-requisite.  Please enable the CMK Key and retry.");
            }

            String clientMasterKeyId = keyMetadata.getArn();
            logger.info("\n clientMasterKeyId: " + clientMasterKeyId);

            EncryptionKey latestEnvelopeEncryptionKey = getLatestEnvelopeEncryptionKey();
            logger.info("\n\n latestEnvelopeEncryptionKey: " + latestEnvelopeEncryptionKey);

            if (latestEnvelopeEncryptionKey == null) {
                logger.info("\n CREATE new SecretKey");
                try {
                    secretKey = SecurityUtil.getSecretKey();
                } catch(Exception e) {
                    throw new InternalServerException(e.getMessage(), e);
                }
            } else {
                logger.info("\n ROTATE existing SecretKey");

                String loadCiphertext = latestEnvelopeEncryptionKey.getCiphertext();
                logger.info("\n\n loadCiphertext: " + loadCiphertext);
                logger.info(" loadCiphertext.length(): " + loadCiphertext.length());

                byte[] loadEncryptedKeyByteArray = latestEnvelopeEncryptionKey.getEncryptedKey();
                logger.info("\n\n loadEncryptedKeyByteArray: " + loadEncryptedKeyByteArray);

                EnvelopeEncryptedMessage envelopeEncryptedMessageNew = new EnvelopeEncryptedMessage();
                logger.info("\n\n envelopeEncryptedMessageNew: " + envelopeEncryptedMessageNew);

                envelopeEncryptedMessageNew.setEncryptedKey(loadEncryptedKeyByteArray);

                envelopeEncryptedMessageNew.setCiphertext(loadCiphertext);

                logger.info("\n\n envelopeEncryptedMessageNew: " + envelopeEncryptedMessageNew);

                EnvelopeEncryptionService envelopeEncryptionServiceNew = null;
                logger.info("\n envelopeEncryptionServiceNew: " + envelopeEncryptionServiceNew);

                String decryptedString = envelopeEncryptionServiceNew.decrypt(envelopeEncryptedMessageNew);
                logger.info("\n decryptedString: " + decryptedString);
                logger.info("\n decryptedString.length(): " + decryptedString.length());

                String base64EncodedSecretKeyString2 = decryptedString.substring(Constants.SECRET_KEY_PREFIX.length(), decryptedString.length());
                System.out.println("\n base64EncodedSecretKeyString2: " + base64EncodedSecretKeyString2);
                logger.info("\n base64EncodedSecretKeyString2.length(): " + base64EncodedSecretKeyString2.length());

                secretKey = convertBase64EncodedStringToSecretKey(base64EncodedSecretKeyString2);
            }


            logger.info("\n secretKey: " + secretKey);

            secretKeyStr = secretKey.toString();
            logger.info("\n secretKeyStr: " + secretKeyStr);

            //Get Base64 encoded version of the key
            base64EncodedSecretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            logger.info("\n base64EncodedSecretKeyString: " + base64EncodedSecretKeyString);
            logger.info("\n base64EncodedSecretKeyString.length(): " + base64EncodedSecretKeyString.length());


            EnvelopeEncryptionService envelopeEncryptionService = null;
            logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

            logger.info("\n\n ENCRYPT");

            String modifiedBase64EncodedSecretKeyString = Constants.SECRET_KEY_PREFIX + base64EncodedSecretKeyString;
            logger.info("\n modifiedBase64EncodedSecretKeyString: " + modifiedBase64EncodedSecretKeyString);
            logger.info("\n modifiedBase64EncodedSecretKeyString.length(): " + modifiedBase64EncodedSecretKeyString.length());

            envelopeEncryptedMessage = envelopeEncryptionService.encrypt(modifiedBase64EncodedSecretKeyString);
            logger.info(" envelopeEncryptedMessage: " + envelopeEncryptedMessage);

            String ciphertext = envelopeEncryptedMessage.getCiphertext();
            logger.info(" ciphertext: " + ciphertext);
            logger.info(" ciphertext.length(): : " + ciphertext.length());

            byte[] encryptedKeyByteArray = envelopeEncryptedMessage.getEncryptedKey();
            String encryptedKey = Arrays.toString(encryptedKeyByteArray);
            logger.info(" encryptedKey: " + encryptedKey);
            logger.info(" encryptedKey.length(): " + encryptedKey.length());


            EncryptionKey envelopeEncryptionKey = createEnvelopeEncryptionKey(awsRegionStr, stackStr, kmsKeyAliasStr, secretKeyStr, modifiedBase64EncodedSecretKeyString, encryptedKeyByteArray, ciphertext);
            encryptionKeyService.save(envelopeEncryptionKey);
            logger.info("\n Successfully persisted envelopeEncryptionKey: " + envelopeEncryptionKey);

            resultEnvelopeEncryptionKey = envelopeEncryptionKey;
        }

        return resultEnvelopeEncryptionKey;
    }

    @RequestMapping(method=RequestMethod.GET, value = {"/secretKey"})
    @ResponseBody
    public SecretKey getSecretKey() {
        logger.info("\n KeyStoreController::getSecretKey()");

        SecretKey secretKey = null;
        String base64EncodedSecretKeyString = null;
        KeyMetadata keyMetadata = null;

        EncryptionKey envelopeEncryptionKey = getLatestKey();
        if (envelopeEncryptionKey != null) {
            logger.info("\n\n envelopeEncryptionKey: " + envelopeEncryptionKey);

            String awsRegion = envelopeEncryptionKey.getAwsRegion();
            logger.info("\n awsRegion: " + awsRegion);

            String kmsKeyAlias = envelopeEncryptionKey.getAwsKMSKeyAlias();
            logger.info("\n kmsKeyAlias: " + kmsKeyAlias);

            keyMetadata = Util.getKMSCustomerMasterKey(awsRegion, kmsKeyAlias);
            if (keyMetadata != null) {
                Boolean enabledFlag = keyMetadata.isEnabled();
                if (enabledFlag) {
                    //throw new InternalServerException("The CMK for keyAlias: " + kmsKeyAlias + " in region: " + awsRegion + " is not enabled.  Can not retrieve.  This is a pre-requisite.  Please enable the CMK Key and retry.");
                    String kmsKeyArn = keyMetadata.getArn();
                    logger.info("\n kmsKeyArn: " + kmsKeyArn);

                    String ciphertext = envelopeEncryptionKey.getCiphertext();
                    logger.info("\n ciphertext: " + ciphertext);

                    byte[] encryptedKeyByteArray = envelopeEncryptionKey.getEncryptedKey();
                    logger.info("\n encryptedKeyByteArray: " + encryptedKeyByteArray);

                    EnvelopeEncryptedMessage envelopeEncryptedMessage = new EnvelopeEncryptedMessage();
                    logger.info("\n envelopeEncryptedMessage: " + envelopeEncryptedMessage);

                    envelopeEncryptedMessage.setEncryptedKey(encryptedKeyByteArray);
                    envelopeEncryptedMessage.setCiphertext(ciphertext);

                    logger.info("\n\n envelopeEncryptedMessage: " + envelopeEncryptedMessage);

                    EnvelopeEncryptionService envelopeEncryptionService = null;
                    logger.info("\n envelopeEncryptionService: " + envelopeEncryptionService);

                    String decryptedString = envelopeEncryptionService.decrypt(envelopeEncryptedMessage);
                    logger.info("\n decryptedString: " + decryptedString);
                    logger.info("\n decryptedString.length(): " + decryptedString.length());

                    secretKey = convertBase64EncodedStringToSecretKey(decryptedString);

                    logger.info("\n\n secretKey: " + secretKey);
                }
            }
        }

        return secretKey;
    }

    /*
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public SecretKey create() {
        logger.info("\n SecretKeyController::create()");

        SecretKey secretKey = null;
        String base64EncodedSecretKeyString = null;

        try {
            secretKey = SecurityUtil.getSecretKey();
            if (secretKey != null) {
                logger.info("\n secretKey: " + secretKey);

                //Get Base64 encoded version of the key
                base64EncodedSecretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                logger.info("\n base64EncodedSecretKeyString: " + base64EncodedSecretKeyString);
            }
        } catch(Exception e) {
            throw new InternalServerException(e.getMessage(), e);
        }

        return secretKey;
    }
    */

    @RequestMapping(method=RequestMethod.GET, value = {"/keys"})
    @ResponseBody
    public List<EncryptionKey> getRecentKeys() {
        logger.info("\n KeyStoreController::getRecentKeys()");

        List<EncryptionKey> encryptionKeyList = encryptionKeyService.findAllByQvCreatedTsDescending();
        return encryptionKeyList;
    }

    @RequestMapping(method=RequestMethod.GET, value = {"/keys/latest"})
    @ResponseBody
    public EncryptionKey getLatestKey() {
        logger.info("\n KeyStoreController::getLatestKey()");
        return getLatestEnvelopeEncryptionKey();
    }

    private EncryptionKey getEnvelopeEncryptionKey() {
        logger.info("\n void getEnvelopeEncryptionKey()");
        EncryptionKey encryptionKey = null;

        List<EncryptionKey> envelopeEncryptionKeyList = encryptionKeyService.findAll();
        if (envelopeEncryptionKeyList == null || envelopeEncryptionKeyList.isEmpty()) {
            logger.info("\n envelopeEncryptionKeyList is null or empty");
        } else {
            logger.info("\n envelopeEncryptionKeyList.size(): " + envelopeEncryptionKeyList.size());
            encryptionKey = envelopeEncryptionKeyList.get(0);
        }

        return encryptionKey;
    }

    public EncryptionKey getLatestEnvelopeEncryptionKey() {
        logger.info("\n KeyStoreController::getLatestEnvelopeEncryptionKey()");

        EncryptionKey encryptionKey = null;

        List<EncryptionKey> envelopeEncryptionKeyList = encryptionKeyService.findLatest();
        if (envelopeEncryptionKeyList != null && !envelopeEncryptionKeyList.isEmpty()) {
            logger.info("\n envelopeEncryptionKeyList.size(): " + envelopeEncryptionKeyList.size());
            for (int i=0; i<envelopeEncryptionKeyList.size(); i++) {
                EncryptionKey r = envelopeEncryptionKeyList.get(i); 
                logger.info("\n r[" + i + "]: " + r);
            }

            encryptionKey = envelopeEncryptionKeyList.get(0);
        }

        return encryptionKey;
    }

    private EncryptionKey createEnvelopeEncryptionKey(String awsRegion, String stack, String kmsKeyAlias, String secretKey, String encodedSecretKey, byte[] encryptedKeyByteArray, String ciphertext) {
        String status = Constants.OBJECT_STORAGE_KEY_STORE_STATUS_ENABLED;

        Timestamp qvCreatedTs = null;
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

    public SecretKey convertBase64EncodedStringToSecretKey(String base64EncodedSecretKeyString) {
        SecretKey secretKey = null;
        if (base64EncodedSecretKeyString != null) {
            logger.info("\n base64EncodedSecretKeyString: " + base64EncodedSecretKeyString);

            //Decode the Base64 encoded string
            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedSecretKeyString);

            //Rebuild key using SecretKeySpec
            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, Constants.KEY_GENERATOR_ALGORITHM_AES);
        }
        return secretKey;
    }
}
