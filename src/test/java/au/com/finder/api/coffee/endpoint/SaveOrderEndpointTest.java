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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.INJECTOR;
import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.MAPPER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SaveOrderEndpointTest {
    private static final SaveOrderEndpoint saveOrderEndpoint = new SaveOrderEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    @Test
    public void testSaveOrder() throws IOException {
        Order returnedOrder;
        Order order = new Order();
        order.setStatus("preparing");
        order.setCoffees(IntStream.range(0, 3).mapToObj(i -> {
            Coffee coffee = new Coffee();
            coffee.setType("flatwhite");
            return coffee;
        }).collect(Collectors.toList()));
        String body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(order));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
            returnedOrder = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Order.class);
            assertNotNull("Order should not be null", returnedOrder);
            assertTrue("Id should be there in order", returnedOrder.getId() > 0);
            assertNotNull("Coffees should not be null", returnedOrder.getCoffees());
            assertEquals("There should be three coffees", 3, returnedOrder.getCoffees().size());
            assertNotNull("Order should have created date", returnedOrder.getCreated());
        }

        returnedOrder.setStatus("served");
        returnedOrder.getCoffees().add(new Coffee(0, "longblack", null, null));
        body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(returnedOrder));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
            returnedOrder = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Order.class);
            assertNotNull("Order should not be null", returnedOrder);
            assertTrue("Id should be there in order", returnedOrder.getId() > 0);
            assertNotNull("Coffees should not be null", returnedOrder.getCoffees());
            assertEquals("Three coffees should have updated", 3, returnedOrder.getCoffees().stream().filter(c -> c.getUpdated() != null).count());
            assertEquals("There should be three coffees", 4, returnedOrder.getCoffees().size());
            assertNotNull("Order should have created date", returnedOrder.getCreated());
            assertNotNull("Order should have updated date", returnedOrder.getUpdated());
            assertEquals("Order's status should be served", "served", returnedOrder.getStatus());
        }
    }

    @Test
    public void testSaveInvalidOrder() throws IOException {
        Order order = new Order();
        order.setStatus("preparing");
        order.setCoffees(IntStream.range(0, 3).mapToObj(i -> {
            Coffee coffee = new Coffee();
            coffee.setType("flatwhite1");
            return coffee;
        }).collect(Collectors.toList()));
        String body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(order));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
            String statusCode = MAPPER.readValue(output.toByteArray(), Response.class).getStatusCode();
            assertNotNull("Status code should not be null", statusCode);
            assertEquals("Status code should be 500", "400", statusCode);
        }
    }
}
