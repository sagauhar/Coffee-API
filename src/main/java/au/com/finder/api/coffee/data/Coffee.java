package au.com.finder.api.coffee.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

public class Coffee {
    private int id;
    private String type;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ", timezone="UTC")
    private Date created;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ", timezone="UTC")
    private Date updated;

    public Coffee(int id, String type, Date created, Date updated) {
        this.id = id;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }

    public Coffee() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
