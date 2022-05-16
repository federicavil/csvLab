package model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Release {

    private String name;
    private String sha_code;
    private Date releasedDate;
    private Map<String, Boolean> classBugginess;


    public Release(String name, Date releasedDate) {
        this.name = name;
        this.releasedDate = releasedDate;
    }

    public Release() {
        this.classBugginess = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = releasedDate;
    }

    public Map<String, Boolean> getClassBugginess() {
        return classBugginess;
    }

    public void setClassBugginess(Map<String, Boolean> classBugginess) {
        this.classBugginess = classBugginess;
    }

    public String getSha_code() {
        return sha_code;
    }

    public void setSha_code(String sha_code) {
        this.sha_code = sha_code;
    }
}
