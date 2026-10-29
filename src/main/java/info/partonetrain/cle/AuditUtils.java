package info.partonetrain.cle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AuditUtils {

    public static String writeLog(String logTitle, List<String> logLines){
        String logName = logTitle + ".txt";
        try{
            Path logPath = Paths.get(System.getProperty("user.dir"), "logs", logName);
            Files.write(logPath, logLines);
        } catch (IOException e) {
            return "failed to write to logs/ " + logName + ": " + e.getMessage();
        }
        return "wrote to logs/" + logName;
    }
}
