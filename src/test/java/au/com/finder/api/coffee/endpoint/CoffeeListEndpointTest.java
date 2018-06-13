package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.data.Response;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.INJECTOR;
import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.MAPPER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CoffeeListEndpointTest {
    private static final SaveOrderEndpoint saveOrderEndpoint = new SaveOrderEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    private static final CoffeeListEndpoint coffeeListEndpoint = new CoffeeListEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    @Test
    public void testFetchCoffeeList() throws IOException {
        Order order = new Order();
        order.setStatus("preparing");
        order.setCoffees(IntStream.range(0, 10).mapToObj(i -> {
            Coffee coffee = new Coffee();
            coffee.setType("piccolo");
            return coffee;
        }).collect(Collectors.toList()));
        String body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(order));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
        }

        try (ByteArrayInputStream input = new ByteArrayInputStream("".getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            coffeeListEndpoint.handleRequest(input, output, null);
            List coffees = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), List.class);
            assertNotNull("Orders should not be null", coffees);
            assertTrue("There should be more than zero orders", coffees.size() > 0);
        }
    }
}
