package Model;

import java.util.Date;

public class Release {

    private String name;
    private Date releasedDate;

    public Release(String name, Date releasedDate) {
        this.name = name;
        this.releasedDate = releasedDate;
    }

    public Release() {

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
}
