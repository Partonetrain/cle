package info.partonetrain.cle;

import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.millenaire.content.ContentDirectoryManager;
import org.slf4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DeploymentHelper {
    
    final static String MODID = Cle.MODID;
    final static Logger LOGGER = Cle.LOGGER;

    public static final Map<String, Integer> DEPLOYED_VERSIONS = new HashMap<>();

    static {
        DEPLOYED_VERSIONS.put("cle_millagers_convert_to_alternatives", 1);
    }

    public static void deployCustomFolder(MinecraftServer server, String folderToDeploy) {
        LOGGER.info("Checking /millenaire-custom/ deployed content...");
        ContentDirectoryManager.init(server);
        Path customDir = ContentDirectoryManager.getCustomDir();
        Path folderPath = customDir.resolve(folderToDeploy);
        Path versionFile = folderPath.resolve("version.txt");

        boolean doesNotExistYet = false;

        //first check if it's already there and up to date.
        int currentlyDeployedVersion = 0;
        if (Files.exists(versionFile)) {
            try (Stream<@NotNull String> lines = Files.lines(versionFile)) {
                Optional<String> firstLine = lines.findFirst();
                if (firstLine.isPresent()) {
                    currentlyDeployedVersion = Integer.parseInt(firstLine.get());
                } else {
                    LOGGER.error("Found /millenaire-custom/" + folderToDeploy + "/version.txt, but it was empty! Assuming version " + currentlyDeployedVersion);
                }
            } catch (IOException e) {
                LOGGER.error("Error while attempting to read /millenaire-custom/" + folderToDeploy + "/version.txt: " + e);
            }
        }
        else{
            doesNotExistYet = true;
        }

        final int VERSION_IN_JAR = DEPLOYED_VERSIONS.get(folderToDeploy);

        if (!doesNotExistYet && currentlyDeployedVersion == -1) {
            LOGGER.info("Deployed /millenaire-custom/" + folderToDeploy + "/version.txt is set to -1, ignoring");
            return;
        }
        if (!doesNotExistYet && currentlyDeployedVersion == VERSION_IN_JAR) {
            LOGGER.info("Deployed /millenaire-custom/" + folderToDeploy + " is up-to-date.");
            return;
        } else if (currentlyDeployedVersion < VERSION_IN_JAR) {
            //now actually deploy.
            IModFile thisJar = ModList.get().getModFileById(MODID).getFile();
            Path customToDeploy = thisJar.findResource("deployable", "millenaire-custom", folderToDeploy);
            try {
                if(!doesNotExistYet) {
                    LOGGER.info("Deployed /millenaire-custom/" + folderToDeploy + " is out-of-date. Deleting old data.");
                }
                nukeOldDeployed(folderPath); //deletes regardless of if text file exists. safely does nothing if no folder.
                copyFromTo(customToDeploy, folderPath);
                writeVersionFile(versionFile, VERSION_IN_JAR);
            } catch (Exception e) {
                LOGGER.error("Error while attempting to deploy to /millenaire-custom/" + folderToDeploy + " :" + e);
            }
        }
    }

    private static void nukeOldDeployed(Path path) throws IOException {
        FileUtils.deleteDirectory(path.toFile()); //returns safely if directory does not exist.
    }

    private static void copyFromTo(Path source, Path target) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath).toString());

                if (Files.isDirectory(sourcePath)) {
                    if (!Files.exists(targetPath)) {
                        Files.createDirectories(targetPath);
                    }
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy: " + sourcePath, e);
            }
        });
    }

    private static void writeVersionFile(Path path, int version) throws IOException {
        try {
            Files.writeString(path,
                    version + System.lineSeparator() +
                            "#This file is used by " + MODID + " to determine version. Please do not edit it, unless want to prevent deployment (set it to -1)." + System.lineSeparator()
            );
        } catch (IOException e) {
            LOGGER.error("Error while attempting to write version.txt to /millenaire-custom/: " + e);
        }
    }
}
