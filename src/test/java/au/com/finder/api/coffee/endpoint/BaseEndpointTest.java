package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.dataaccess.DataAccess;
import au.com.finder.api.coffee.dataaccess.MySQLDataAccessMock;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OrderEndpointTest.class, OrderListEndpointTest.class, CoffeeEndpointTest.class, CoffeeListEndpointTest.class, SaveOrderEndpointTest.class})
public class BaseEndpointTest {
    private static DB database;
    static final ObjectMapper MAPPER = new ObjectMapper();
    static final Injector INJECTOR = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(DataAccess.class).to(MySQLDataAccessMock.class);
        }
    });

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            super.before();
            database = DB.newEmbeddedDB(3306);
            database.start();
            database.createDB("finderdb");
        }

        @Override
        protected void after() {
            super.after();
            try {
                database.stop();
            } catch (ManagedProcessException e) {
                e.printStackTrace();
            }
        }
    };
}
