package net.tfminecraft.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Plugins.TLibs.TLibs;

public class InventoryUtils {

    public static double getTotalAmount(Player p, String path) {
        double total = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            String itemPath = TLibs.getItemAPI().getChecker().getAsStringPath(item);
            if (itemPath == null) continue;

            if (itemPath.equalsIgnoreCase(path)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public static boolean hasEnough(Player p, String path, double requiredAmount) {
        return getTotalAmount(p, path) >= requiredAmount;
    }

    public static void removeItems(Player p, String path, double amountToRemove) {
        double remaining = amountToRemove;

        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            String itemPath = TLibs.getItemAPI().getChecker().getAsStringPath(item);
            if (itemPath == null) continue;

            if (!itemPath.equalsIgnoreCase(path)) continue;

            int stackAmount = item.getAmount();

            if (stackAmount <= remaining) {
                remaining -= stackAmount;
                item.setAmount(0);
            } else {
                item.setAmount((int) (stackAmount - remaining));
                remaining = 0;
            }

            if (remaining <= 0) break;
        }
    }
}

