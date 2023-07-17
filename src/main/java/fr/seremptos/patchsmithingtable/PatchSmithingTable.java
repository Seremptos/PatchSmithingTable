package fr.seremptos.patchsmithingtable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PatchSmithingTable extends JavaPlugin {

    public static PatchSmithingTable plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
