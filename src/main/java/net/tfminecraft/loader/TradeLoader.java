package net.tfminecraft.loader;

import net.tfminecraft.MarketBlock;
import net.tfminecraft.trade.Trade;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Logger;

public class TradeLoader {

    private static final HashMap<String, Trade> trades = new HashMap<>();

    public void loadTrades() {
        trades.clear();
        File tradeFolder = new File(MarketBlock.plugin.getDataFolder(), "trades");
        Logger log = MarketBlock.plugin.getLogger();

        if (!tradeFolder.exists() || !tradeFolder.isDirectory()) {
            log.warning("Trade folder does not exist: " + tradeFolder.getAbsolutePath());
            return;
        }

        File[] files = tradeFolder.listFiles();
        if (files == null) {
            log.warning("No files found in trade folder.");
            return;
        }

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json")) {
                continue;
            }

            try {
                String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
                    content = content.substring(1);
                }
                content = content.strip();
                if (content.isEmpty() || content.charAt(0) != '{') {
                    log.warning("Skipping invalid trade file: " + file.getName());
                    continue;
                }

                Trade trade = Trade.fromJson(new JSONObject(content));
                trades.put(trade.getId(), trade);
                log.info("Loaded trade: " + trade.getId());
            } catch (IOException e) {
                log.warning("Skipping unreadable trade file: " + file.getName() + " (" + e.getMessage() + ")");
            } catch (JSONException e) {
                log.warning("Skipping invalid trade file: " + file.getName() + " (" + e.getMessage() + ")");
            } catch (Exception e) {
                log.warning("Skipping trade file: " + file.getName() + " (" + e.getMessage() + ")");
            }
        }
    }

    public static void add(Trade t) {
        trades.put(t.getId(), t);
    }

    public static Trade getTradeById(String id) {
        return trades.get(id);
    }

    public static HashMap<String, Trade> getTrades() {
        return trades;
    }
}
