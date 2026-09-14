package net.tfminecraft.database;

import net.tfminecraft.trade.Trade;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class TradeDatabase {

    private static final File folder = new File("plugins/MarketBlock/trades");

    public static void saveAllTrades(Map<String, Trade> trades) {
        if (!folder.exists()) folder.mkdirs();

        for (Trade trade : trades.values()) {
            saveTrade(trade);
        }
    }

    public static void saveTrade(Trade trade) {
        try {
            JSONObject json = toJson(trade);
            File file = new File(folder, trade.getId() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString(4)); // 4 for pretty printing
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTrade(Trade trade) {
        File file = new File(folder, trade.getId() + ".json");
        if(file.exists()) file.delete();
    }

    private static JSONObject toJson(Trade trade) {
        JSONObject json = new JSONObject();
        json.put("id", trade.getId());
        json.put("category", trade.getCategory().getId());
        json.put("demand", trade.getDemand());
        json.put("demand limit", trade.getDemandLimit());
        json.put("price change", trade.getPriceChange());
        json.put("item", trade.getItemString());
        json.put("item restingPrice", trade.getItemRestingPrice());
        json.put("amount", trade.getAmount());
        json.put("group", trade.getGroup());
        return json;
    }
}
