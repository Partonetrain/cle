package info.partonetrain.cle.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import info.partonetrain.cle.Cle;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SlightlyLessJankEarlyConfigReader {
    //read from config file directly since config is not set up by the time we need these values
    //... even though the config type is startup it's not early enough
    //this is like the 5th mod I'm doing this shit for a mod T_T
    //but this time I'm actually going to use the TOML parser, hence "slightly less jank"

    public static boolean configRead = false;

    public static float minBand = -1;
    public static int maxCoreSpread = -1;
    public static float minGentle2 = -1;
    public static float minCentreWeighted = -1;
    public static float minConnected = -1;

    public static void readConfigsEarly(){
        if(configRead){
            return;
        }

        Path configFilePath = Paths.get(System.getProperty("user.dir"), "config", "cle-startup.toml");

        try {
            BufferedReader reader = Files.newBufferedReader(configFilePath);
            Config config = new TomlParser().parse(reader);

            minBand = getNumberFromConfig(config, "Min Band").floatValue();
            maxCoreSpread = getNumberFromConfig(config, "Max Core Spread").intValue();
            minGentle2 = getNumberFromConfig(config, "Min Gentle2").floatValue();
            minCentreWeighted = getNumberFromConfig(config, "Min Centre Weighted").floatValue();
            minConnected = getNumberFromConfig(config, "Min Connected").floatValue();

            Cle.LOGGER.info("SlightlyLessJankEarlyConfigReader: Config parsed early. " + minBand + ", " + maxCoreSpread + ", " + minGentle2 + ", " + minCentreWeighted + ", " + minConnected);
            configRead = true;

        } catch (IOException e) {
            Cle.LOGGER.error("Early config error (file likely just doesn't exist yet):" + e.getMessage());
            configRead = true;
        }
    }

    private static Number getNumberFromConfig(Config config, String key) {
        return config.get(List.of("World", "Site Settings (Experimental)", key));
    }
}
