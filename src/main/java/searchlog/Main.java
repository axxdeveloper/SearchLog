package searchlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(SearchServlet.class, "/search");

        server.start();
        server.join();
    }

    public static class SearchServlet extends HttpServlet {
        private static final long serialVersionUID = -482662427030753953L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String[] matches = req.getParameterValues("match");

            System.out.println(Arrays.toString(matches));
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            File folder = new File("logs");
            listAll(folder.toPath(), matches, resp.getOutputStream());
        }

        private static void listAll(Path p, final String[] containTxts, final OutputStream out) throws IOException {
            try (final OutputStreamWriter writer = new OutputStreamWriter(out);) {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println("visit:" + file);
                        boolean logFileFound = false;
                        try (BufferedReader reader = Files.newBufferedReader( file, Charset.forName("UTF8"))) {
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                if (containsAll(line, containTxts)) {
                                    if ( !logFileFound ) {
                                        logFileFound = true;
                                        writer.write("<p>match found: " + file + "</p>");
                                    }
                                    writer.write(line + "<br>");
                                }
                            }
                        }
                        return super.visitFile(file, attrs);
                    }
                });
            }
        }

        private static boolean containsAll(String line, String[] matches) {
            boolean result = false;
            for (String match : matches) {
                if (!line.contains(match)) {
                    result = false;
                    break;
                }
                if ( line.contains(match) ) {
                    result = true;
                }
            }
            return result;
        }

    }

}
