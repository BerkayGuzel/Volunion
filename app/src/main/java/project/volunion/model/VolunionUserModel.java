package project.volunion.model;

import java.io.Serializable;

public class VolunionUserModel implements Serializable {

    private String id;
    private String name;
    private String surname;
    private String job;
    private String email;
    private String city;
    private String town;
    private String companyID;
    private String companyName;
    private String password;
    private String url;
    private String documentId;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public VolunionUserModel() {
    }

    public VolunionUserModel(String name, String surname, String id, String email, String companyID, String url, String documentId,String job,
                             String companyName,String town, String city) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.companyID = companyID;
        this.url = url;
        this.documentId = documentId;
        this.job = job;
        this.companyName = companyName;
        this.town = town;
        this.city = city;
    }

    public VolunionUserModel(String name, String surname, String job, String email,
                             String password, String city, String town, String companyID,
                             String companyName) {
        this.name = name;
        this.surname = surname;
        this.job = job;
        this.email = email;
        this.city = city;
        this.town = town;
        this.companyID = companyID;
        this.password = password;
        this.companyName = companyName;
    }

    public VolunionUserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String setId(String id) {
        this.id = id;
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }
}
