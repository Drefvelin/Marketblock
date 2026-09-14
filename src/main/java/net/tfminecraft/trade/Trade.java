package net.tfminecraft.trade;

import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import me.Plugins.TLibs.TLibs;
import net.tfminecraft.loader.CategoryLoader;
import net.tfminecraft.manager.commands.MarketblockConversation;

public class Trade {
    private String id;
    private Category category;
    private double demand;
    private double demandLimit;
    private double priceChange;
    private String item;
    private double itemRestingPrice;
    private double amount;
    private int group;

    // Constructor
    public Trade(String id, Category category, double demand, double demandLimit, double priceChange, String item, double itemRestingPrice, double amount, int group) {
        this.id = id;
        this.category = category;
        category.addTrade(this);
        this.demand = demand;
        this.demandLimit = demandLimit;
        this.priceChange = priceChange;
        this.item = item;
        this.itemRestingPrice = itemRestingPrice;
        this.amount = amount;
        this.group = group;
    }

    public Trade(MarketblockConversation convo) {
        this.id = convo.getId();
        this.category = convo.getCategory();
        category.addTrade(this);
        this.demandLimit = Math.max(1, convo.getDemandLimit());
        this.demand = this.demandLimit/2;
        this.item = TLibs.getItemAPI().getChecker().getAsStringPath(convo.getItem());
        this.itemRestingPrice = convo.getRestingPrice();
        this.priceChange = convo.getPriceChange();
        this.amount = convo.getItem().getAmount();
        this.group = convo.getGroup();
    }

    public void resetDemand() {
        demand = demandLimit/2;
    }

    public void demand() {
        demand += Math.max(1, Math.random()*7);
        if(demand > demandLimit) demand = demandLimit;
    }

    public void sell() {
        demand -= Math.min(1, Math.random()*priceChange);
        if(demand < 1) demand = 1;
    }

    // Getters (setters omitted for immutability; add if needed)
    public String getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public double getDemand() {
        return demand;
    }

    public double getDemandLimit() {
        return demandLimit;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public ItemStack getItem() {
        return TLibs.getItemAPI().getCreator().getItemFromPath(item);
    }

    public String getItemString() {
        return item;
    }

    public double getItemRestingPrice() {
        return itemRestingPrice;
    }

    public double getAmount() {
        return amount;
    }

    public int getGroup() {
        return group;
    }

    // Factory method to load from JSON object
    public static Trade fromJson(JSONObject json) {
        String id = json.getString("id");
        Category category = CategoryLoader.getByString(json.getString("category"));
        double demand = json.getDouble("demand");
        double demandLimit = json.getDouble("demand limit");
        double priceChange = json.getDouble("price change");
        String item = json.getString("item");
        double itemRestingPrice = json.getDouble("item restingPrice");
        double amount = json.getDouble("amount");
        int group = json.has("group") ? json.getInt("group") : 0;

        return new Trade(id, category, demand, demandLimit, priceChange, item, itemRestingPrice, amount, group);
    }
}

