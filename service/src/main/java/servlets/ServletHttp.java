package servlets;

import db_setup.JDBCCredentials;
import generated.tables.records.ProductRecord;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static generated.tables.Product.PRODUCT;

public class ServletHttp extends HttpServlet {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        try (Connection connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password())) {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);

            try (final PrintWriter out = resp.getWriter()) {
                context.fetch(PRODUCT).forEach(out::println);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final var name = req.getParameter("name");
        final var amount = req.getParameter("amount");

        if ((name == null) || (amount == null)) {
            resp.getWriter().println("Bad Request");
            resp.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        if ((name.isEmpty()) || (amount.isEmpty())) {
            resp.getWriter().println("Bad Request");
            resp.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        try (Connection connection = DriverManager.getConnection(CREDS.url(), CREDS.login(), CREDS.password())) {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);

            final ProductRecord productRecord = context.newRecord(PRODUCT);
            productRecord
                    .setName(name)
                    .setAmount(Integer.parseInt(amount));
            productRecord.store();

            try (final PrintWriter out = resp.getWriter()) {
                out.println("New Product added");
            }

        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }


}
