package com.objectstoragesystem.util;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import org.junit.*;     
//import org.junit.Assert.*;
import static org.junit.Assert.*;

import com.objectstoragesystem.entity.ConfigParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


//@RunWith(SpringRunner.class)
//@SpringBootTest
public class UtilTests {


    /*
    @Test
    public void testCommonConfigParameters() {
        String actual = null;
        String expected = null;
        List<CommonConfigParameter> commonConfigParameterList = null;
        CommonConfigParameter commonConfigParameter = null;

        try {
            Util.validate(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "There are no config parameters available.  Please populate required config parameters.";
            assertEquals(expected, actual);
        }

        commonConfigParameterList = new ArrayList<CommonConfigParameter>();
        try {
            Util.validate(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "There are no config parameters available.  Please populate required config parameters.";
            assertEquals(expected, actual);
        }

        commonConfigParameterList.clear();

        commonConfigParameter = new CommonConfigParameter();
        commonConfigParameterList.add(commonConfigParameter);

        try {
            Util.validate(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }

        //validate awsRegion CommonConfigParameter
        commonConfigParameterList.clear();
        commonConfigParameter = null;

        commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.CONFIGPARAMETERNAME_AWS_REGION);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            Util.validate(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.CONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }

        //validate awsRegion CommonConfigParameter
        commonConfigParameterList.clear();
        commonConfigParameter = null;

        commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.CONFIGPARAMETERNAME_AWS_REGION);
        commonConfigParameter.setValue("us-east-1");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            Util.validate(commonConfigParameterList);
            actual = Util.awsRegion;
            expected = "us-east-1";
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            fail("Should not be in this catch block");
        }


        //validate stack CommonConfigParameter
        commonConfigParameterList.clear();
        commonConfigParameter = null;

        commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.CONFIGPARAMETERNAME_STACK);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            Util.validate(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.CONFIGPARAMETERNAME_STACK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }

        //validate awsRegion CommonConfigParameter
        commonConfigParameterList.clear();
        commonConfigParameter = null;

        commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.CONFIGPARAMETERNAME_STACK);
        commonConfigParameter.setValue("emory");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            Util.validate(commonConfigParameterList);
            actual = Util.awsRegion;
            expected = "us-east-1";
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            fail("Should not be in this catch block");
        }
    */

    /*

    //=====
    //awsRegion

    @Test
    public void testGetAWSRegionWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsRegion = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            awsRegion = Util.getAWSRegion(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAWSRegionWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsRegion = null;
 
        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            awsRegion = Util.getAWSRegion(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAWSRegionWithNoAWSRegionCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsRegion = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            awsRegion = Util.getAWSRegion(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_REGION + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAWSRegionWithAWSRegionCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsRegion = "us-west-1";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_AWS_REGION);
        commonConfigParameter.setValue(awsRegion);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getAWSRegion(commonConfigParameterList);
            expected = awsRegion;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //stack

    @Test
    public void testGetStackWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String stack = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            stack = Util.getStack(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_STACK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetStackWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String stack = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            stack = Util.getStack(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_STACK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAWSRegionWithNoStackCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String stack = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            stack = Util.getStack(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_STACK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAWSRegionWithStackCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String stack = "dignity";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_STACK);
        commonConfigParameter.setValue(stack);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getStack(commonConfigParameterList);
            expected = stack;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //awsIamKmsCMK

    @Test
    public void testGetAwsIamKmsCMKWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsIamKmsCMK = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            awsIamKmsCMK = Util.getIamKmsCMK(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_IAM_KMS_CMK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsIamKmsCMKWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsIamKmsCMK = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            awsIamKmsCMK = Util.getIamKmsCMK(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_IAM_KMS_CMK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsIamKmsCMKWithNoCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsIamKmsCMK = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            awsIamKmsCMK = Util.getIamKmsCMK(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_IAM_KMS_CMK + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsIamKmsCMKWithCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsIamKmsCMK = "qventus_dignity_rawdatastore_kms";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_AWS_IAM_KMS_CMK);
        commonConfigParameter.setValue(awsIamKmsCMK);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getIamKmsCMK(commonConfigParameterList);
            expected = awsIamKmsCMK;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //rootIngressFolder

    @Test
    public void testGetUploadDirectoryWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String rootIngressFolder = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            rootIngressFolder = Util.getIngressFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_INGRESS_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetUploadDirectoryWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String rootIngressFolder = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            rootIngressFolder = Util.getIngressFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_INGRESS_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetUploadDirectoryWithNoCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String rootIngressFolder = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            rootIngressFolder = Util.getIngressFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_INGRESS_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetUploadDirectoryWithCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String rootIngressFolder = File.separator + "data" + File.separator + "amdData" + File.separator + "upload";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_INGRESS_FOLDER);
        commonConfigParameter.setValue(rootIngressFolder);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getIngressFolder(commonConfigParameterList);
            expected = rootIngressFolder;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //downloadDirectory

    @Test
    public void testGetDownloadDirectoryWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String downloadDirectory = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            downloadDirectory = Util.getDownloadFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_DOWNLOAD_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDownloadDirectoryWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String downloadDirectory = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            downloadDirectory = Util.getDownloadFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_DOWNLOAD_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDownloadDirectoryWithNoCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String downloadDirectory = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            downloadDirectory = Util.getDownloadFolder(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_DOWNLOAD_FOLDER + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDownloadDirectoryWithCMKCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String downloadDirectory = File.separator + "data" + File.separator + "amdData" + File.separator + "download";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_DOWNLOAD_FOLDER);
        commonConfigParameter.setValue(downloadDirectory);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getDownloadFolder(commonConfigParameterList);
            expected = downloadDirectory;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //awsAccessKeyID

    @Test
    public void testGetAwsAccessKeyIDWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsAccessKeyID = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            awsAccessKeyID = Util.getAWSAccessKeyID(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsAccessKeyIDWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsAccessKeyID = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            awsAccessKeyID = Util.getAWSAccessKeyID(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsAccessKeyIDWithNoAccessKeyIDCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsAccessKeyID = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            awsAccessKeyID = Util.getAWSAccessKeyID(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsAccessKeyIDWithAccessKeyIDCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsAccessKeyID = "ABCDEFGHIJKLMNOPQRST";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_AWS_ACCESS_KEY_ID);
        commonConfigParameter.setValue(awsAccessKeyID);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getAWSAccessKeyID(commonConfigParameterList);
            expected = awsAccessKeyID;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }

    //=====
    //awsSecretAccessKey

    @Test
    public void testGetAwsSecretAccessKeyWithNullCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsSecretAccessKey = null;

        List<CommonConfigParameter> commonConfigParameterList = null;

        try {
            awsSecretAccessKey = Util.getAWSSecretAccessKey(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsSecretAccessKeyWithEmptyCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsSecretAccessKey = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        try {
            awsSecretAccessKey = Util.getAWSSecretAccessKey(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsSecretAccessKeyWithNoAccessKeyIDCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsSecretAccessKey = null;

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName("bogus");
        commonConfigParameterList.add(commonConfigParameter);

        try {
            awsSecretAccessKey = Util.getAWSSecretAccessKey(commonConfigParameterList);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Missing config parameter: " + Constants.COMMONCONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY + ".  Please populate and retry.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetAwsSecretAccessKeyWithAccessKeyIDCommonConfigParameterList() {
        String actual = null;
        String expected = null;
        String awsSecretAccessKey = "1234567890ABCDEFGHIJKLMNOPQRST/1ABCDEFGH";

        List<CommonConfigParameter> commonConfigParameterList = new ArrayList<CommonConfigParameter>();

        CommonConfigParameter commonConfigParameter = new CommonConfigParameter();
        commonConfigParameter.setName(Constants.COMMONCONFIGPARAMETERNAME_AWS_SECRET_ACCESS_KEY);
        commonConfigParameter.setValue(awsSecretAccessKey);
        commonConfigParameterList.add(commonConfigParameter);

        try {
            actual = Util.getAWSSecretAccessKey(commonConfigParameterList);
            expected = awsSecretAccessKey;
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this block");
        }
    }




    //=====
    //Region name

    @Test
    public void testAWSRegionNameWithNullAWSRegion() {
        String actual = null;
        String expected = null;
        String awsRegion = null;
        String awsRegionName = null;

        try {
            awsRegionName = Util.getAWSRegionName(awsRegion);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid awsRegion.  Please supply valid awsRegion.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testAWSRegionNameWithBogusAWSRegion() {
        String actual = null;
        String expected = null;
        String awsRegion = "bogus";
        String awsRegionName = null;

        try {
            awsRegionName = Util.getAWSRegionName(awsRegion);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "The supplied AWS Region: null is invalid.  Please supply valid AWS Region.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testAWSRegionNameFor_UsEast1_AWSRegion() {
        String actual = null;
        String expected = "us-east-1";
        String awsRegion = "us-east-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_UsWest1_AWSRegion() {
        String actual = null;
        String expected = "us-west-1";
        String awsRegion = "us-west-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_UsWest2_AWSRegion() {
        String actual = null;
        String expected = "us-west-2";
        String awsRegion = "us-west-2";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_ApSouth1_AWSRegion() {
        String actual = null;
        String expected = "ap-south-1";
        String awsRegion = "ap-south-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_ApSouthEast1_AWSRegion() {
        String actual = null;
        String expected = "ap-southeast-1";
        String awsRegion = "ap-southeast-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_ApSouthEast2_AWSRegion() {
        String actual = null;
        String expected = "ap-southeast-2";
        String awsRegion = "ap-southeast-2";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_ApNorthEast1_AWSRegion() {
        String actual = null;
        String expected = "ap-northeast-1";
        String awsRegion = "ap-northeast-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_ApNorthEast2_AWSRegion() {
        String actual = null;
        String expected = "ap-northeast-2";
        String awsRegion = "ap-northeast-2";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_CaCentral1_AWSRegion() {
        String actual = null;
        String expected = "ca-central-1";
        String awsRegion = "ca-central-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_EuCentral1_AWSRegion() {
        String actual = null;
        String expected = "eu-central-1";
        String awsRegion = "eu-central-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_EuWest1_AWSRegion() {
        String actual = null;
        String expected = "eu-west-1";
        String awsRegion = "eu-west-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testAWSRegionNameFor_SaEast1_AWSRegion() {
        String actual = null;
        String expected = "sa-east-1";
        String awsRegion = "sa-east-1";

        try {
            actual = Util.getAWSRegionName(awsRegion);
            assertEquals(expected, actual);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testGetDataSourceTypeWithNullFileName() {
        String actual = null;
        String expected = null;
        String fileName = null;
        String dataSourceType = null;

        try {
            dataSourceType = Util.getDataSourceType(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid dataSourceType.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDataSourceTypeWithEmptyFileName() {
        String actual = null;
        String expected = null;
        String fileName = "";
        String dataSourceType = null;

        try {
            dataSourceType = Util.getDataSourceType(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid dataSourceType.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDataSourceTypeWithNoDelimiterFileName() {
        String actual = null;
        String expected = null;
        String fileName = "HL7#ADT#2018-8-21#1534868127893.dat";
        String dataSourceType = null;

        try {
            dataSourceType = Util.getDataSourceType(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid dataSourceType.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetDataSourceTypeWithValidFileName() {
        String actual = null;
        String expected = null;
        String fileName = "HL7+ADT+2018-8-21+1534868127893.dat";
        String dataSourceType = null;

        try {
            dataSourceType = Util.getDataSourceType(fileName);
            expected = "HL7";
            assertEquals(expected, dataSourceType);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }

    @Test
    public void testGetFeedWithNullFileName() {
        String actual = null;
        String expected = null;
        String fileName = null;
        String feed = null;

        try {
            feed = Util.getFeed(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid feed.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetFeedWithEmptyFileName() {
        String actual = null;
        String expected = null;
        String fileName = "";
        String feed = null;

        try {
            feed = Util.getFeed(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid feed.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetFeedWithNoDelimiterFileName() {
        String actual = null;
        String expected = null;
        String fileName = "HL7#ADT#2018-8-21#1534868127893.dat";
        String feed = null;

        try {
            feed = Util.getFeed(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid feed.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetFeedWithInvalidDelimiterFileName() {
        String actual = null;
        String expected = null;
        String fileName = "HL7+ADT#2018-8-21#1534868127893.dat";
        String feed = null;

        try {
            feed = Util.getFeed(fileName);
        } catch(IllegalArgumentException e) {
            actual = e.getMessage();
            expected = "Invalid feed.  Please supply valid fileName.";
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetFeedWithValidDelimiterFileName() {
        String actual = null;
        String expected = null;
        String fileName = "HL7+ADT+2018-8-21#1534868127893.dat";
        String feed = null;

        try {
            feed = Util.getFeed(fileName);
            expected = "ADT";
            assertEquals(expected, feed);
        } catch(IllegalArgumentException e) {
            fail("Should not be in this catch block");
        }
    }
    */
}
