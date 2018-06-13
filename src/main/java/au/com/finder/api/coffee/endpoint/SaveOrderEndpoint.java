package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.dataaccess.DataAccess;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveOrderEndpoint extends Endpoint<Order> implements RequestStreamHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        String statusCode = "200";
        String errorMessage = null;
        Order order = null;
        try {
            JsonNode node = MAPPER.readTree(input);
            order = MAPPER.readValue(node.get("body").asText(), Order.class);
            if (order != null) {
                order = getInjector().getInstance(DataAccess.class).saveOrder(order);
            }
        } catch (Exception ex) {
            statusCode = "400";
            errorMessage = ex.getMessage();
        }
        writeResponse(output, statusCode, order, errorMessage);
    }
}
