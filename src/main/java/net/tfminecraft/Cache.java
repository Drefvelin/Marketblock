package net.tfminecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;

import me.Plugins.TLibs.TLibs;

public class Cache {
    public static List<Integer> slots = new ArrayList<>();
    public static String marketBlock;

    public static boolean blockIsMarketBlock(Block b) {
        return TLibs.getBlockAPI().getChecker().checkBlock(b, marketBlock);
    }
}
