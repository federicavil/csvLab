package model;

import java.util.Date;
import java.util.HashMap;

public class Release {

    private String name;
    private Date releasedDate;
    private HashMap<String, JavaClassFile> classes;

    public Release(String name, Date releasedDate) {
        this.name = name;
        this.releasedDate = releasedDate;
    }

    public Release() {
        this.classes = new HashMap<>();
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

    public HashMap<String, JavaClassFile> getClasses() {
        return classes;
    }

    public void setClasses(HashMap<String, JavaClassFile> classes) {
        this.classes = classes;
    }

}
