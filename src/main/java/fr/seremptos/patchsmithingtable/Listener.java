package fr.seremptos.patchsmithingtable;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    List<Material> authorizedDiamond = Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.DIAMOND_AXE,
            Material.DIAMOND_HOE,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_HELMET);

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
            //Delayer l'ouverture du GUI (sinon ne fonctionne pas)
            Bukkit.getScheduler().runTaskLater(PatchSmithingTable.plugin, () -> player.openInventory(smithingTable.getInventory()), 1L);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.isShiftClick()) event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;
        if(inventory.getHolder() instanceof LegacySmithingTable smithingTable){
            if(inventory.getItem(0) == null || inventory.getItem(1) == null){
                inventory.setItem(2, null);
            }
            switch(event.getSlot()){
                case 0:
                    // N'autoriser que les outils/armure en diamant dans le premier slot
                    Material material = event.getCursor().getType();

                    if(!authorizedDiamond.contains(material) && material != Material.AIR){
                        event.setCancelled(true);
                    }
                    if (inventory.getItem(1) != null) {
                        ItemStack item2 = event.getCursor().clone();
                        item2.setType(Material.getMaterial(item2.getType().toString().replace("DIAMOND", "NETHERITE")));
                        inventory.setItem(2, item2);
                    }
                    break;
            case 1:
                // N'autoriser que la netherite dans le second slot
                if(event.getCursor().getType() != Material.NETHERITE_INGOT){
                    if(event.getCursor().getType() != Material.AIR){
                        event.setCancelled(true);
                    }else{
                        Bukkit.getScheduler().runTaskLater(PatchSmithingTable.plugin, () -> inventory.setItem(2, null), 1L);
                    }
                }
                if(inventory.getItem(0) != null){
                    ItemStack item = inventory.getItem(0).clone();
                    item.setType(Material.getMaterial(item.getType().toString().replace("DIAMOND", "NETHERITE")));
                    inventory.setItem(2, item);
                }
                break;
            case 2:
                if(event.getCurrentItem() != null){
                    inventory.setItem(0, inventory.getItem(0).add(-1));
                    inventory.setItem(1, inventory.getItem(1).add(-1));
                    player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_SMITHING_TABLE_USE, Sound.Source.BLOCK, 1, 0.9f));
                }else{
                    event.setCancelled(true);
                }
                break;
        }
    }
}

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof LegacySmithingTable smithingTable) {
            event.setCancelled(true);
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
