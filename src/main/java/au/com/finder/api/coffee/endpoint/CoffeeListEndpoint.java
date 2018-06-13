package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.dataaccess.DataAccess;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CoffeeListEndpoint extends Endpoint<List<Coffee>> implements RequestStreamHandler {
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        writeResponse(output,"200", getInjector().getInstance(DataAccess.class).getCoffees(), null);
    }
}
