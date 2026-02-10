package com.blockchain.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A very simple JSON parser/writer tailored for this Blockchain project.
 * NOTE: This is NOT a full JSON parser. It assumes a specific structure.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\"", "\\\"");
    }

    public static String unescape(String s) {
        if (s == null)
            return "";
        return s.replace("\\\"", "\"");
    }

    // Parses a simple JSON object string into a Map
    // Expects flat key-values, except for "transactions" which is a list of strings
    public static Map<String, Object> parseBlockJson(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{"))
            json = json.substring(1);
        if (json.endsWith("}"))
            json = json.substring(0, json.length() - 1);

        // Split by fields, but be careful about the list
        // Strategy: Extract "transactions": [...] manually first

        List<String> transactions = new ArrayList<>();
        int txStart = json.indexOf("\"transactions\":");
        if (txStart != -1) {
            int listStart = json.indexOf("[", txStart);
            int listEnd = json.indexOf("]", listStart);
            if (listStart != -1 && listEnd != -1) {
                String listContent = json.substring(listStart + 1, listEnd);
                // Parse array items (quoted strings)
                String[] items = listContent.split(",");
                for (String item : items) {
                    item = item.trim();
                    if (item.startsWith("\"") && item.endsWith("\"")) {
                        transactions.add(unescape(item.substring(1, item.length() - 1)));
                    }
                }

                // Remove the list from json string to parse rest easily (hacky but works for
                // this)
                String part1 = json.substring(0, txStart);
                String part2 = json.substring(listEnd + 1);
                json = part1 + part2;
            }
        }
        map.put("transactions", transactions);

        // Parse remaining primitive fields
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length >= 2) {
                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    map.put(key, unescape(value.substring(1, value.length() - 1)));
                } else {
                    // Try parsing number
                    try {
                        if (key.equals("timestamp")) {
                            map.put(key, Long.parseLong(value));
                        } else if (key.equals("index")) {
                            map.put(key, Integer.parseInt(value));
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }

        return map;
    }
}
