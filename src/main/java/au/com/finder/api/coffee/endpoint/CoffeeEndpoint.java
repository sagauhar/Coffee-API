package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.dataaccess.DataAccess;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CoffeeEndpoint extends Endpoint<Coffee> implements RequestStreamHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        String statusCode = "200";
        String errorMessage = null;
        Coffee coffee = null;
        try {
            JsonNode node = MAPPER.readTree(input).get("pathParameters");
            if (node != null && node.get("id") != null) {
                int id = node.get("id").asInt();
                coffee = getInjector().getInstance(DataAccess.class).getCoffeeById(id);
            }
        } catch (Exception ex) {
            statusCode = "400";
            errorMessage = ex.getMessage();
        }
        writeResponse(output, statusCode, coffee, errorMessage);
    }
}
