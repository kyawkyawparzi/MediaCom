package pmt.kyawkyaw.myapp.mediacom.model;

public class User {
    private String id;
    private String username;
    private String picture;
    private String status;
    private String search;

    public User(String id, String username, String picture, String status,String search) {
        this.id = id;
        this.username = username;
        this.picture = picture;
        this.status = status;
        this.search=search;
    }
    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
