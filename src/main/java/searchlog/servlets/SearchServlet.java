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
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

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
        String match = req.getParameter("match");

        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        if ( isBlank(match) ) {
            return;
        }
        
        Configuration conf = Configuration.getInstance();
        logger.info("search folder:{}", conf.getLogsPath().toFile().getAbsolutePath());
        listAll(conf.getLogsPath(), match, resp.getOutputStream());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }
    
    private static void listAll(Path p, final String match, final OutputStream out) throws IOException {
        final AtomicLong dataCnt = new AtomicLong();
        try (final OutputStreamWriter writer = new OutputStreamWriter(out);) {
            Files.walkFileTree(p, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 10, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if ( !file.toString().endsWith(".log") ) {
                        return FileVisitResult.CONTINUE;
                    }
                    logger.info("visit:" + file);
                    boolean logFileFound = false;
                    try (BufferedReader reader = Files.newBufferedReader( file, Charset.forName("UTF8"))) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(match)) {
                                dataCnt.incrementAndGet();
                                if ( !logFileFound ) {
                                    logFileFound = true;
                                    writer.write("<table><tr><th>");
                                    writer.write("match found: " + file + "");
                                    writer.write("</th></tr>");
                                }
                                writer.write("<tr><td>");
                                writer.write(line.replace(match, "<mark>" + match + "</mark>") + "<br>");
                                writer.write("</td></tr>");
                            }
                            if ( dataCnt.longValue() > 10000 ) {
                                return FileVisitResult.TERMINATE;
                            }
                        }
                        if ( logFileFound ) {
                            writer.write("</table>");
                        }
                    } catch (Throwable ex) {
                        logger.error("", ex);
                    }
                    return super.visitFile(file, attrs);
                }
            });
        }
    }

    public static void main(String[] params) {
        String s = "match1 match2 qqqq match3";
        System.out.println(s.replace("match", "<mark>match</mark>"));
    }
    
}