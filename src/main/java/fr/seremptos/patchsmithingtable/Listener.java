package fr.seremptos.patchsmithingtable;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class Listener implements org.bukkit.event.Listener {


    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        //Ne marche que sur les 1.20+
        ViaAPI api = Via.getAPI();
        int version = api.getPlayerVersion(player);
        if(version < 763) return;
        //
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SMITHING_TABLE){
            LegacySmithingTable smithingTable = new LegacySmithingTable();
            Bukkit.getScheduler().runTaskLater(PatchSmithingTable.plugin, () -> player.openInventory(smithingTable.getInventory()), 1l);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;

        if(inventory.getItem(0) == null || inventory.getItem(1) == null){
            inventory.setItem(2, null);
        }

        if(inventory.getHolder() instanceof LegacySmithingTable smithingTable){

            // N'autoriser que la netherite dans le second slot
            if(event.getSlot() == 1 && event.getCursor().getType() != Material.NETHERITE_INGOT){
                // Autoriser à reprendre l'item (curseur vide)
                if(event.getCursor().getType() != Material.AIR){
                    event.setCancelled(true);
                }
            }

            switch(event.getSlot()){
                case 0:
                    if(inventory.getItem(1) != null) {
                        ItemStack item2 = event.getCursor().clone();
                        item2.setType(Material.getMaterial(item2.getType().toString().replace("DIAMOND", "NETHERITE")));
                        inventory.setItem(2, item2);
                    }
                case 1:
                    if(inventory.getItem(0) != null){
                        ItemStack item = inventory.getItem(0).clone();
                        item.setType(Material.getMaterial(item.getType().toString().replace("DIAMOND", "NETHERITE")));
                        inventory.setItem(2, item);
                    }
                    break;
                case 2:
                    if(event.getCurrentItem() != null){
                        inventory.setItem(0, null);
                        inventory.setItem(1, null);
                    }
                    return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        if(inventory.getHolder() instanceof LegacySmithingTable){
            for(int i = 0; i <= 1; i++){
                ItemStack item = inventory.getItem(i);
                if(item != null){
                    player.getInventory().addItem(item).values().forEach(itemStack -> player.getWorld().dropItem(event.getPlayer().getLocation(), itemStack));
                }
            }
        }
    }

}
