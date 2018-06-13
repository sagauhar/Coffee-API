package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.dataaccess.DataAccess;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;

public class OrderEndpoint extends Endpoint<Order> implements RequestStreamHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        String statusCode = "200";
        String errorMessage = null;
        Order order = null;
        try {
            JsonNode node = MAPPER.readTree(input).get("pathParameters");
            if (node != null && node.get("id") != null) {
                int id = node.get("id").asInt();
                order = getInjector().getInstance(DataAccess.class).getOrderById(id);
            }
        } catch (Exception ex) {
            statusCode = "400";
            errorMessage = ex.getMessage();
        }
        writeResponse(output, statusCode, order, errorMessage);
    }
}
