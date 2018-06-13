package au.com.finder.api.coffee.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private List<Coffee> coffees;
    private String status;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ", timezone="UTC")
    private Date created;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ", timezone="UTC")
    private Date updated;

    public Order(int id, List<Coffee> coffees, String status, Date created, Date updated) {
        this.id = id;
        this.coffees = coffees;
        this.status = status;
        this.created = created;
        this.updated = updated;
    }

    public Order() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Coffee> getCoffees() {
        return coffees;
    }

    public void setCoffees(List<Coffee> coffees) {
        this.coffees = coffees;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
