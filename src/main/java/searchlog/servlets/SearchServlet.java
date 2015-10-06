package searchlog.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import searchlog.Configuration;

public class SearchServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);
    private static final long serialVersionUID = -482662427030753953L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] matches = req.getParameterValues("match");

        System.out.println(Arrays.toString(matches));
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        Configuration conf = Configuration.getInstance();
        logger.info("search folder:{}", conf.getLogsPath().toFile().getAbsolutePath());
        listAll(conf.getLogsPath(), matches, resp.getOutputStream());
    }

    private static void listAll(Path p, final String[] containTxts, final OutputStream out) throws IOException {
        try (final OutputStreamWriter writer = new OutputStreamWriter(out);) {
            Files.walkFileTree(p, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 10, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if ( !file.toFile().getName().endsWith(".log") ) {
                        return super.visitFile(file, attrs);
                    }
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