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
@Table(name = "objectstoragesystem_config_parameters")
public class ConfigParameter implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String value;

    private Timestamp qvCreatedTs;

    private String qvCreatedSrc;

    private Timestamp qvUpdatedTs;

    private String qvUpdatedSrc;

    public ConfigParameter() {
        super();
    }

    public ConfigParameter(Long id) {
        super();
        this.id = id;
    }

    public ConfigParameter(String name, String value, Timestamp qvCreatedTs, String qvCreatedSrc, Timestamp qvUpdatedTs, String qvUpdatedSrc) {
        super();
        this.name = name;
        this.value = value;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(o instanceof ConfigParameter)) return false;
        ConfigParameter that = (ConfigParameter) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getQvCreatedTs(), that.getQvCreatedTs()) &&
                Objects.equals(getQvCreatedSrc(), that.getQvCreatedSrc()) &&
                Objects.equals(getQvUpdatedTs(), that.getQvUpdatedTs()) &&
                Objects.equals(getQvUpdatedSrc(), that.getQvUpdatedSrc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getValue(), getQvCreatedTs(), getQvCreatedSrc(), getQvUpdatedTs(), getQvUpdatedSrc());
    }

    @Override
    public String toString() {
        return "ConfigParameter {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", qvCreatedTs=" + qvCreatedTs +
                ", qvCreatedSrc='" + qvCreatedSrc + '\'' +
                ", qvUpdatedTs=" + qvUpdatedTs +
                ", qvUpdatedSrc='" + qvUpdatedSrc + '\'' +
                '}';
    }
}