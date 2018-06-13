package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.dataaccess.DataAccess;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class OrderListEndpoint extends Endpoint<List<Order>> implements RequestStreamHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        writeResponse(output, "200", getInjector().getInstance(DataAccess.class).getOrders(), null);
    }
}
