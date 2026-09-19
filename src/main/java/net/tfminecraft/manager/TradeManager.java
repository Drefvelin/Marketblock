package net.tfminecraft.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import net.tfminecraft.Cache;
import net.tfminecraft.MarketBlock;
import net.tfminecraft.DenarEconomy.DenarEconomy;
import net.tfminecraft.events.MarketSaleEvent;
import net.tfminecraft.inventory.holder.MBGUI;
import net.tfminecraft.inventory.holder.MBHolder;
import net.tfminecraft.loader.CategoryLoader;
import net.tfminecraft.loader.TradeLoader;
import net.tfminecraft.trade.Category;
import net.tfminecraft.trade.Trade;
import net.tfminecraft.util.InventoryUtils;
import net.tfminecraft.util.PriceCalculator;

public class TradeManager implements Listener {
    InventoryManager inv = new InventoryManager();

    public void update() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getOpenInventory().getTopInventory() == null) continue;
            Inventory i = p.getOpenInventory().getTopInventory();
            if(!(i.getHolder() instanceof MBHolder)) continue;
            MBHolder h = (MBHolder) i.getHolder();
            if(h.getType().equals(MBGUI.TRADE)) {
                inv.tradeView(i, p, CategoryLoader.getByString(h.getId()));
            }
        }
    }

    private String getItemId(MBGUI type, ItemStack i) {
        NamespacedKey key;
        switch (type) {
            case CATEGORY:
                key = new NamespacedKey(MarketBlock.plugin, "category_id");
                break;
            case TRADE:
                key = new NamespacedKey(MarketBlock.plugin, "trade_id");
                break;
            default:
                key = new NamespacedKey(MarketBlock.plugin, "none");
                break;
        }
        return i.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public void start() {
        demandCycle();
    }

    public void demandCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Trade t : TradeLoader.getTrades().values()) {
                    t.demand();
                }
                update();
            }
        }.runTaskTimer(MarketBlock.plugin, 0, 60*60*20L);
    }
    
    @EventHandler
    public void openMarket(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!Cache.blockIsMarketBlock(e.getClickedBlock())) return;
        e.setCancelled(true);
        inv.categoryView(null, e.getPlayer());
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if(!(e.getView().getTopInventory().getHolder() instanceof MBHolder)) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        MBHolder h = (MBHolder) e.getView().getTopInventory().getHolder();
        ItemStack i = e.getCurrentItem();
        if(i == null) return;
        switch (h.getType()) {
            case CATEGORY:
                categoryClick(p, h, i, e);
                break;
            case TRADE:
                tradeClick(p, h, i, e);
                break;
            default:
                break;
        }
    }

    public void categoryClick(Player p, MBHolder h, ItemStack i, InventoryClickEvent e) {
        String id = getItemId(h.getType(), i);
        if(id == null) return;
        Category cat = CategoryLoader.getByString(id);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
        inv.tradeView(null, p, cat);
    }

    public void tradeClick(Player p, MBHolder h, ItemStack i, InventoryClickEvent e) {
        if (i.getType().equals(Material.BARRIER)) {
            inv.categoryView(null, p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
            return;
        }

        String id = getItemId(h.getType(), i);
        if (id == null) return;

        Trade trade = TradeLoader.getTradeById(id);
        if (trade == null) {
            p.sendMessage("§cCould not find this trade.");
            return;
        }

        String tradePath = trade.getItemString();
        double requiredAmount = trade.getAmount();

        if (!InventoryUtils.hasEnough(p, tradePath, requiredAmount)) {
            p.sendMessage("§cYou don't have enough items for this trade.");
            return;
        }
        InventoryUtils.removeItems(p, tradePath, requiredAmount);
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        double price = PriceCalculator.calculatePrice(trade);
        DenarEconomy.getMoneyManager().addMoney(p, price, false, true);
        trade.sell();
        Bukkit.getPluginManager().callEvent(new MarketSaleEvent(p, trade, price, requiredAmount));
        update();
    }
}
