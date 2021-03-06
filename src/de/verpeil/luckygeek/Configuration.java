/**********************************
 *Configuration.java
 *Part of the project "luckyGeek" from
 *ctvoigt (Christian Voigt), chripo2701  2011.
 *
 *
 *Email: luckygeek@verpeil.de
 *
 *
 *
 **********************************
 *
 *Configuration class.
 **********************************
 *
 *This program is free software; you can redistribute it
 *and/or modify it under the terms of the GNU General
 *Public License as published by the Free Software
 *Foundation; either version 2 of the License, or (at your
 *option) any later version.
 *This program is distributed in the hope that it will be
 *useful, but WITHOUT ANY WARRANTY; without even the implied
 *warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *PURPOSE. See the GNU General Public License for more details.
 *You should have received a copy of the GNU General Public
 *License along with this program; if not, write to the Free
 *Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *MA 02111-1307, USA.
 */

package de.verpeil.luckygeek;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * <b>Singleton</b> for read-access of configuration.
 */
final class Configuration {
    private static final String LOCAL_CONFIG_FOLDER = "luckyGeek";
    private static final String UNIX_GLOBAL_CONFIG = "/etc/luckyGeek.conf";
    private static final String CONF_FILENAME = "conf.properties";
    private static final Logger LOG = Logger.getLogger(Configuration.class
            .getCanonicalName());
    private static final Properties PROPERTIES = new Properties();

    private static boolean isLocalFolder = false;

    private Configuration() {
        // not required
    }

    static void load() {
        try {
            load(getConfigFile());
        } catch (Exception e) {
            LOG.severe("Error while loading default configuration."
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    static void load(String filePath) {
        PROPERTIES.clear();

        File file = new File(filePath);
        if (!file.exists()) {
            LOG.warning("Can not load configuration from file " + filePath);
            return;
        }
        InputStream ins = null;
        try {
            ins = new FileInputStream(filePath);
            PROPERTIES.load(ins);
        } catch (Exception e) {
            LOG.severe("Error while reading configuration file: "
                    + e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }

    static String getDownloadUrl() {
        return PROPERTIES.getProperty("download.url");
    }

    static String getAllFileName() {
        String propertyString = PROPERTIES.getProperty("file.all");
        return convertToAbsolutePath(propertyString);
    }

    private static String convertToAbsolutePath(String propertyString) {
        propertyString = appendPrefixForLocalFolders(propertyString);
        propertyString = replaceHomeChar(propertyString);
        return propertyString;
    }

    private static String replaceHomeChar(String propertyString) {
        return propertyString.replace("~", FileUtils.getUserDirectoryPath());
    }

    private static String appendPrefixForLocalFolders(String propertyString) {
        if (isLocalFolderAndNotAbsolutePath(propertyString)) {
            return FileUtils.getUserDirectoryPath() + "/" + LOCAL_CONFIG_FOLDER
                    + "/" + propertyString;
        }
        return propertyString;
    }

    private static boolean isLocalFolderAndNotAbsolutePath(String propertyString) {
        return isLocalFolder
                && !(propertyString.contains(":") || propertyString
                        .startsWith("/"));
    }

    static String getLastImageName() {
        String propertyString = PROPERTIES.getProperty("file.last.image");
        return convertToAbsolutePath(propertyString);
    }

    static File getLastImage() {
        return new File(getLastImageName());
    }

    static String getLastFileName() {
        String lastFile = PROPERTIES.getProperty("file.last");
        return convertToAbsolutePath(lastFile);
    }

    static File getLastFile() {
        return new File(getLastFileName());
    }

    static ConversionTypes getConversionType() {
        return ConversionTypes.parse(PROPERTIES.getProperty("type.conversion",
                ""));
    }

    static boolean isSilentPrintAllowed() {
        return Boolean.valueOf(
                PROPERTIES.getProperty("file.last.print.silent", "false"))
                .booleanValue();
    }

    static boolean isMergeAllowed() {
        return Boolean.valueOf(
                PROPERTIES.getProperty("file.all.merge", "false"))
                .booleanValue();
    }

    static boolean isOnlyImageDownload() {
        return Boolean.valueOf(
                PROPERTIES.getProperty("file.jpeg.only", "false"))
                .booleanValue();
    }

    static String getXpath() {
        return PROPERTIES.getProperty("xml.temp.xpath");
    }

    static boolean isNamepsaceContextAvailable() {
        return null != PROPERTIES.get("xml.temp.namespaces")
                && null != PROPERTIES.get("xml.temp.namespaces.prefixes");
    }

    static Map<String, String> getNamespacesAndPrefixes() {
        String[] namespaces = PROPERTIES.getProperty("xml.temp.namespaces", "")
                .split("[;]");
        String[] prefixes = PROPERTIES.getProperty(
                "xml.temp.namespaces.prefixes", "").split("[;]");
        int size = Math.min(namespaces.length, prefixes.length);
        Map<String, String> result = new HashMap<String, String>(size);
        for (int i = 0; i < size; i++) {
            result.put(prefixes[i], namespaces[i]);
        }
        return result;
    }

    static String getConfigFilePath() {
        File homePath = new File(FileUtils.getUserDirectoryPath() + "/"
                + LOCAL_CONFIG_FOLDER);
        if (homePath.exists()) {
            isLocalFolder = true;
            return homePath.getAbsolutePath() + "/" + CONF_FILENAME;
        }

        File globalConfig = new File(UNIX_GLOBAL_CONFIG);
        if (globalConfig.exists()) {
            return globalConfig.getAbsolutePath();
        }

        return new File("conf/" + CONF_FILENAME).getAbsolutePath();
    }

    private static String getConfigFile() throws Exception {
        String configPath = getConfigFilePath();
        File config = new File(configPath);
        if (config.exists()) {
            LOG.info("Used cinfig file: " + config);
            return config.getAbsolutePath();
        }
        throw new Exception("Config file '" + configPath + "' does not exist!");
    }

    static String getMemoryFilePath() {
        String propertyString = PROPERTIES.getProperty("file.memory");
        if (propertyString.equals(".") && isLocalFolder) {
            return new File(FileUtils.getUserDirectoryPath() + "/"
                    + LOCAL_CONFIG_FOLDER).getAbsolutePath();
        }
        return convertToAbsolutePath(propertyString);
    }
}
