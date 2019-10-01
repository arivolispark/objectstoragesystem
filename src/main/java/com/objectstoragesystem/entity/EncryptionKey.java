package com.objectstoragesystem.entity;



import java.io.Serializable;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "objectstoragesystem_encryption_keys")
public class EncryptionKey implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(nullable=false)
    private String stackName;

    @Column(nullable=false)
    private String awsRegion;

    @Column(name="aws_kms_key_alias", nullable=false)
    private String awsKMSKeyAlias;

    @Column(nullable=false)
    private String secretKey;

    @Column(nullable=false)
    private String encodedSecretKey;

    @Column(nullable=false)
    private byte[] encryptedKey;

    @Column(nullable=false)
    private String ciphertext;

    @Column(nullable=false)
    private String status;

    private Timestamp qvCreatedTs;

    private String qvCreatedSrc;

    private Timestamp qvUpdatedTs;

    private String qvUpdatedSrc;

    public EncryptionKey() {
        super();
    }

    public EncryptionKey(Long id) {
        super();
        this.id = id;
    }

    public EncryptionKey(String stackName, String awsRegion, String awsKMSKeyAlias, String secretKey, String encodedSecretKey, byte[] encryptedKey, String ciphertext, String status, Timestamp qvCreatedTs, String qvCreatedSrc, Timestamp qvUpdatedTs, String qvUpdatedSrc) {
        super();
        this.stackName = stackName;
        this.awsRegion = awsRegion;
        this.awsKMSKeyAlias = awsKMSKeyAlias;
        this.secretKey = secretKey;
        this.encodedSecretKey = encodedSecretKey;
        this.encryptedKey = encryptedKey;
        this.ciphertext = ciphertext;
        this.status = status;
        this.qvCreatedTs = qvCreatedTs;
        this.qvCreatedSrc = qvCreatedSrc;
        this.qvUpdatedTs = qvUpdatedTs;
        this.qvUpdatedSrc = qvUpdatedSrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStackName() {
        return stackName;
    }

    public void setStackName(String stackName) {
        this.stackName = stackName;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public String getAwsKMSKeyAlias() {
        return awsKMSKeyAlias;
    }

    public void setAwsKMSKeyAlias(String awsKMSKeyAlias) {
        this.awsKMSKeyAlias = awsKMSKeyAlias;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEncodedSecretKey() {
        return encodedSecretKey;
    }

    public void setEncodedSecretKey(String encodedSecretKey) {
        this.encodedSecretKey = encodedSecretKey;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getQvCreatedTs() {
        return qvCreatedTs;
    }

    public void setQvCreatedTs(Timestamp qvCreatedTs) {
        this.qvCreatedTs = qvCreatedTs;
    }

    public String getQvCreatedSrc() {
        return qvCreatedSrc;
    }

    public void setQvCreatedSrc(String qvCreatedSrc) {
        this.qvCreatedSrc = qvCreatedSrc;
    }

    public Timestamp getQvUpdatedTs() {
        return qvUpdatedTs;
    }

    public void setQvUpdatedTs(Timestamp qvUpdatedTs) {
        this.qvUpdatedTs = qvUpdatedTs;
    }

    public String getQvUpdatedSrc() {
        return qvUpdatedSrc;
    }

    public void setQvUpdatedSrc(String qvUpdatedSrc) {
        this.qvUpdatedSrc = qvUpdatedSrc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptionKey)) return false;
        EncryptionKey that = (EncryptionKey) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getStackName(), that.getStackName()) &&
                Objects.equals(getAwsRegion(), that.getAwsRegion()) &&
                Objects.equals(getAwsKMSKeyAlias(), that.getAwsKMSKeyAlias()) &&
                Objects.equals(getSecretKey(), that.getSecretKey()) &&
                Objects.equals(getEncodedSecretKey(), that.getEncodedSecretKey()) &&
                Objects.equals(getEncryptedKey(), that.getEncryptedKey()) &&
                Objects.equals(getCiphertext(), that.getCiphertext()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getQvCreatedTs(), that.getQvCreatedTs()) &&
                Objects.equals(getQvCreatedSrc(), that.getQvCreatedSrc()) &&
                Objects.equals(getQvUpdatedTs(), that.getQvUpdatedTs()) &&
                Objects.equals(getQvUpdatedSrc(), that.getQvUpdatedSrc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStackName(), getAwsRegion(), getAwsKMSKeyAlias(), getSecretKey(), getEncodedSecretKey(), getEncryptedKey(), getCiphertext(), getStatus(), getQvCreatedTs(), getQvCreatedSrc(), getQvUpdatedTs(), getQvUpdatedSrc());
    }

    @Override
    public String toString() {
        return "EncryptionKey{" +
                "id=" + id +
                ", stackName='" + stackName + '\'' +
                ", awsRegion='" + awsRegion + '\'' +
                ", awsKMSKeyAlias='" + awsKMSKeyAlias + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", encodedSecretKey='" + encodedSecretKey + '\'' +
                //", encryptedKey ='" + encryptedKey + '\'' +
                ", ciphertext ='" + ciphertext + '\'' +
                ", status='" + status + '\'' +
                ", qvCreatedTs=" + qvCreatedTs +
                ", qvCreatedSrc='" + qvCreatedSrc + '\'' +
                ", qvUpdatedTs=" + qvUpdatedTs +
                ", qvUpdatedSrc='" + qvUpdatedSrc + '\'' +
                '}';
    }
}
