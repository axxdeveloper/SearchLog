package searchlog;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import searchlog.servlets.SearchServlet;

public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(SearchServlet.class, "/search");

        server.start();
        server.join();
    }

}
