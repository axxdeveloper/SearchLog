package searchlog;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final Configuration INSTANCE = new Configuration();
    
    private Path logsPath;
    
    public static Configuration getInstance() {
        return INSTANCE;
    }
    
    public Configuration() {
        File confFile = new File("conf/searchlog.properties");
        try {
            Properties conf = new Properties();
            System.out.println(confFile.getAbsolutePath());
            conf.load(new FileInputStream(confFile));
            logsPath = Paths.get(conf.getProperty("log.folder"));
            if (!logsPath.toFile().exists()) {
                Files.createDirectory(logsPath);
            }
            logger.info("Load conf:" + conf);
        } catch (Throwable ex) {
            logger.error("Load configuration fail. file:" + confFile, ex);
        }
    }
    
    public Path getLogsPath() {
        return logsPath;
    }
    
}
