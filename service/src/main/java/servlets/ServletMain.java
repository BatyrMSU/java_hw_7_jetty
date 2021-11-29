package servlets;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.QoSFilter;
import org.eclipse.jetty.util.resource.Resource;
import servers.DefaultServer;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;

public class ServletMain {

    public static void main(String[] args) throws Exception {
        final Server server = new DefaultServer().build();

        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.NO_SESSIONS
        );
        context.setContextPath("/");
        final URL resource = ServletMain.class.getResource("/static");
        context.setBaseResource(Resource.newResource(resource.toExternalForm()));
        context.setWelcomeFiles(new String[]{"/static/help.html"});
        context.addServlet(new ServletHolder("default", DefaultServlet.class), "/");
        context.addServlet(
                new ServletHolder("servletHolder1", new ServletHttp()),
                "/products"
        );

        final QoSFilter filter = new QoSFilter();
        final FilterHolder filterHolder = new FilterHolder(filter);
        filterHolder.setInitParameter("maxRequests", "1");
        context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        final String hashConfig = ServletMain.class.getResource("/hash_config").toExternalForm();
        final HashLoginService hashLoginService = new HashLoginService("login", hashConfig);
        final ConstraintSecurityHandler securityHandler = new SecurityHandlerBuilder().build(hashLoginService);
        server.addBean(hashLoginService);
        securityHandler.setHandler(context);
        server.setHandler(securityHandler);

        server.start();
    }

}
