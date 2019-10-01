package com.objectstoragesystem.util;



import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;


public class SecurityUtil {
    private static final Logger logger = Logger.getLogger(SecurityUtil.class.getName());

    public static SecretKey secretKey = null;

    public static SecretKey getSecretKey() throws NoSuchAlgorithmException {
        if (secretKey == null) {
            secretKey = generateAESSymmetricKey();
        }
        return secretKey;
    }

    public static SecretKey generateAESSymmetricKey() throws NoSuchAlgorithmException {
        SecretKey symmetricKey = null;

        //Generate symmetric 256 bit AES key
        KeyGenerator symmetricKeyGenerator = KeyGenerator.getInstance(Constants.KEY_GENERATOR_ALGORITHM_AES);
        logger.info("\n symmetricKeyGenerator: " + symmetricKeyGenerator);

        symmetricKeyGenerator.init(Constants.KEY_BIT_SIZE_256);
        symmetricKey = symmetricKeyGenerator.generateKey();
        logger.info("\n symmetricKey: " + symmetricKey);

        return symmetricKey;
    }
}
