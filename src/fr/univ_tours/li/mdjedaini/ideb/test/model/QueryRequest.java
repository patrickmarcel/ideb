package fr.univ_tours.li.mdjedaini.ideb.test.model;


import java.math.BigInteger;
import java.util.Date;

public class QueryRequest {
    private int id;
    private String query;
    private Date datetime;
    private BigInteger duration;
    private Result result;

    public QueryRequest(int id, String query, Date datetime, BigInteger duration) {
        this.id = id;
        this.query = query;
        this.datetime = datetime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
