package au.com.finder.api.coffee.data;

public class Response {
    private String statusCode;
    private String body;

    public Response(String statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public Response() {}

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
