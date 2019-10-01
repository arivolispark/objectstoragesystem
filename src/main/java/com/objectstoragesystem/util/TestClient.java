package com.objectstoragesystem.util;

public class TestClient {
    public static void main(String[] args) {
        System.out.println("\n Hello, world!");

        Util.awsS3BucketFolder = "qventus-dignity-files";

        String baseS3Key = Util.getBaseS3Key();
        System.out.println("\n baseS3Key: " + baseS3Key);
    }
}
