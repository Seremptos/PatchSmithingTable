package fr.seremptos.patchsmithingtable;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LegacySmithingTable implements InventoryHolder {

    private final Inventory smithingTable;

    public LegacySmithingTable() {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        smithingTable = Bukkit.createInventory(this, InventoryType.ANVIL, Component.text("Table de forgeron"));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.smithingTable;
    }

}
