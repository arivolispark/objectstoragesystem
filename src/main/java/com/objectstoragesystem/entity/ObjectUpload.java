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
@Table(name = "objectstoragesystem_object_uploads")
public class ObjectUpload implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable=false, unique=true)
    private String filePath;

    @Column(name="s3_key")
    private String s3Key;

    private Long fileSizeInBytes;

    private Long compressedFileSizeInBytes;

    private Timestamp transferStartTimeTs;

    private Timestamp transferEndTimeTs;

    private Long transferDurationInMilliSeconds;

    private Long iterationCount;

    private String status;

    private String message;

    private String stackName;

    private String feed;

    private String source;

    private Timestamp qvCreatedTs;

    private String qvCreatedSrc;

    private Timestamp qvUpdatedTs;

    private String qvUpdatedSrc;

    public ObjectUpload() {
        super();
    }

    public ObjectUpload(Long id) {
        super();
        this.id = id;
    }

    public ObjectUpload(String filePath, String s3Key, Long fileSizeInBytes, Long compressedFileSizeInBytes, Long iterationCount, String status, String message, Timestamp transferStartTimeTs, Timestamp transferEndTimeTs, Long transferDurationInMilliSeconds, String stackName, String feed, String source, Timestamp qvCreatedTs, String qvCreatedSrc, Timestamp qvUpdatedTs, String qvUpdatedSrc) {
        super();
        this.filePath = filePath;
        this.s3Key = s3Key;
        this.fileSizeInBytes = fileSizeInBytes;
        this.compressedFileSizeInBytes = compressedFileSizeInBytes;
        this.transferStartTimeTs = transferStartTimeTs;
        this.transferEndTimeTs = transferEndTimeTs;
        this.transferDurationInMilliSeconds = transferDurationInMilliSeconds;
        this.iterationCount = iterationCount;
        this.status = status;
        this.message = message;
        this.stackName = stackName;
        this.feed = feed;
        this.source = source;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(Long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public Long getCompressedFileSizeInBytes() {
        return compressedFileSizeInBytes;
    }

    public void setCompressedFileSizeInBytes(Long compressedFileSizeInBytes) {
        this.compressedFileSizeInBytes = compressedFileSizeInBytes;
    }

    public Timestamp getTransferStartTimeTs() {
        return transferStartTimeTs;
    }

    public void setTransferStartTimeTs(Timestamp transferStartTimeTs) {
        this.transferStartTimeTs = transferStartTimeTs;
    }

    public Timestamp getTransferEndTimeTs() {
        return transferEndTimeTs;
    }

    public void setTransferEndTimeTs(Timestamp transferEndTimeTs) {
        this.transferEndTimeTs = transferEndTimeTs;
    }

    public Long getTransferDurationInMilliSeconds() {
        return transferDurationInMilliSeconds;
    }

    public void setTransferDurationInMilliSeconds(Long transferDurationInMilliSeconds) {
        this.transferDurationInMilliSeconds = transferDurationInMilliSeconds;
    }

    public Long getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(Long iterationCount) {
        this.iterationCount = iterationCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackName() {
        return stackName;
    }

    public void setStackName(String stackName) {
        this.stackName = stackName;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        if (!(o instanceof ObjectUpload)) return false;
        ObjectUpload that = (ObjectUpload) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getFilePath(), that.getFilePath()) &&
                Objects.equals(getS3Key(), that.getS3Key()) &&
                Objects.equals(getFileSizeInBytes(), that.getFileSizeInBytes()) &&
                Objects.equals(getCompressedFileSizeInBytes(), that.getCompressedFileSizeInBytes()) &&
                Objects.equals(getIterationCount(), that.getIterationCount()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getTransferStartTimeTs(), that.getTransferStartTimeTs()) &&
                Objects.equals(getTransferEndTimeTs(), that.getTransferEndTimeTs()) &&
                Objects.equals(getTransferDurationInMilliSeconds(), that.getTransferDurationInMilliSeconds()) &&
                Objects.equals(getStackName(), that.getStackName()) &&
                Objects.equals(getFeed(), that.getFeed()) &&
                Objects.equals(getSource(), that.getSource()) &&
                Objects.equals(getQvCreatedTs(), that.getQvCreatedTs()) &&
                Objects.equals(getQvCreatedSrc(), that.getQvCreatedSrc()) &&
                Objects.equals(getQvUpdatedTs(), that.getQvUpdatedTs()) &&
                Objects.equals(getQvUpdatedSrc(), that.getQvUpdatedSrc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilePath(), getS3Key(), getFileSizeInBytes(), getCompressedFileSizeInBytes(), getTransferStartTimeTs(), getTransferEndTimeTs(), getTransferDurationInMilliSeconds(), getIterationCount(), getStatus(), getMessage(), getStackName(), getFeed(), getSource(), getQvCreatedTs(), getQvCreatedSrc(), getQvUpdatedTs(), getQvUpdatedSrc());
    }

    @Override
    public String toString() {
        return "ObjectUpload{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", s3Key='" + s3Key + '\'' +
                ", fileSizeInBytes=" + fileSizeInBytes +
                ", compressedFileSizeInBytes=" + compressedFileSizeInBytes +
                ", iterationCount=" + iterationCount +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", transferStartTimeTs=" + transferStartTimeTs +
                ", transferEndTimeTs=" + transferEndTimeTs +
                ", transferDurationInMilliSeconds=" + transferDurationInMilliSeconds +
                ", stackName='" + stackName + '\'' +
                ", feed='" + feed + '\'' +
                ", source='" + source + '\'' +
                ", qvCreatedTs=" + qvCreatedTs +
                ", qvCreatedSrc='" + qvCreatedSrc + '\'' +
                ", qvUpdatedTs=" + qvUpdatedTs +
                ", qvUpdatedSrc='" + qvUpdatedSrc + '\'' +
                '}';
    }
}