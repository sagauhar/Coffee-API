package au.com.finder.api.coffee.dataaccess;

import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;

public class MySQLDataAccessMock extends MySQLDataAccess {
    public MySQLDataAccessMock() {
        setup();
    }

    private void setup() {
        try (DSLContext context = getContext()) {
            context.execute(getFileContent("sql/orders-table.sql"));
            context.execute(getFileContent("sql/coffees-table.sql"));
        }
    }

    private String getFileContent(String filename) {
        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected DSLContext getContext() {
        return DSL.using("jdbc:mysql://localhost/finderdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
    }
}
