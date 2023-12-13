package net.sithey.minecrafttelegramlink.utils.configuration;


import com.github.smuddgge.squishyyaml.YamlConfiguration;
import net.sithey.minecrafttelegramlink.Main;

import java.io.File;
import java.util.List;

public class Config extends YamlConfiguration {

    /**
     * Create a new configuration file
     * @param folder Folder
     * @param fileName File name
     */
    public Config(File folder, String fileName) {
        super(folder, fileName);
    }

    /**
     * Create a new configuration file
     * @param fileName File name
     */

    public Config(String fileName) {
        this(Main.get().getDataFolder(), fileName);
    }

    /**
     * Save the configuration file
     */
    public Object addValue(String path, Object value) {
        if (get(path) == null) {
            return setValue(path, value);
        }
        return get(path);
    }

    /**
     * Save the configuration file
     */

    public Object setValue(String path, Object value) {
        set(path, value);
        save();
        return value;
    }

    /**
     * Save the configuration file
     */

    public Object getValue(String path) {
        return get(path);
    }

    /**
     * Save the configuration file
     */

    public List<String> getStringList(String path) {
        return getListString(path);
    }

}