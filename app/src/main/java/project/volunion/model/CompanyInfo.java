package project.volunion.model;


public class CompanyInfo {

    public String email;
    public String name;
    public String description;
    public String downloadUrl;
    private String documentId;

    public CompanyInfo(String email, String name, String description, String downloadUrl) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.downloadUrl = downloadUrl;
    }

    public CompanyInfo(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
