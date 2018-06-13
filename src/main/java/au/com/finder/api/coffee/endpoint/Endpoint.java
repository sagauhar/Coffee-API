package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;

import java.io.IOException;
import java.io.OutputStream;

public abstract class Endpoint<T> {
    static final ObjectMapper MAPPER = new ObjectMapper();
    private Injector injector = Guice.createInjector();

    void writeResponse(OutputStream output, String statusCode, T responseObject, String errorMessage) throws IOException {
        MAPPER.writeValue(output, new Response(statusCode, errorMessage != null ? errorMessage : MAPPER.writeValueAsString(responseObject)));
    }

    protected Injector getInjector() {
        return injector;
    }
}
