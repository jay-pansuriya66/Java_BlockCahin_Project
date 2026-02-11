package com.blockchain.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple JSON parser/writer tailored for this Blockchain project.
 * Updated to support list of objects for transactions.
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
    // Expects flat key-values, and strictly handles "transactions" list of objects
    public static Map<String, Object> parseBlockJson(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{"))
            json = json.substring(1);
        if (json.endsWith("}"))
            json = json.substring(0, json.length() - 1);

        // 1. Extract and parse "transactions": [...]
        List<Map<String, Object>> transactions = new ArrayList<>();
        int txStart = json.indexOf("\"transactions\":");
        String jsonWithoutTx = json;

        if (txStart != -1) {
            int listStart = json.indexOf("[", txStart);
            if (listStart != -1) {
                int listEnd = findMatchingBracket(json, listStart);

                if (listEnd != -1) {
                    String listContent = json.substring(listStart + 1, listEnd).trim();

                    if (!listContent.isEmpty()) {
                        // Parse list of objects: { ... }, { ... }
                        List<String> txObjects = splitObjects(listContent);
                        for (String txJson : txObjects) {
                            transactions.add(parseSimpleJson(txJson));
                        }
                    }

                    // Remove the list part to parse the rest
                    String part1 = json.substring(0, txStart);
                    String part2 = json.substring(listEnd + 1);
                    jsonWithoutTx = part1 + part2;
                }
            }
        }
        map.put("transactions", transactions);

        // 2. Parse remaining fields
        parseFields(jsonWithoutTx, map);

        return map;
    }

    private static int findMatchingBracket(String text, int start) {
        int count = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[')
                count++;
            else if (c == ']') {
                count--;
                if (count == 0)
                    return i;
            }
        }
        return -1;
    }

    private static List<String> splitObjects(String arrayContent) {
        List<String> result = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{')
                braceCount++;
            if (c == '}')
                braceCount--;

            if (c == ',' && braceCount == 0) {
                String s = current.toString().trim();
                if (!s.isEmpty())
                    result.add(s);
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        String last = current.toString().trim();
        if (!last.isEmpty())
            result.add(last);
        return result;
    }

    private static Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }

        parseFields(json, map);
        return map;
    }

    private static void parseFields(String json, Map<String, Object> map) {
        StringBuilder key = new StringBuilder();
        StringBuilder val = new StringBuilder();
        boolean inQuote = false;
        boolean parsingKey = true;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"')
                inQuote = !inQuote;

            if (c == ':' && !inQuote && parsingKey) {
                parsingKey = false;
                continue;
            }

            if (c == ',' && !inQuote) {
                saveEntry(key.toString(), val.toString(), map);
                key.setLength(0);
                val.setLength(0);
                parsingKey = true;
                continue;
            }

            if (parsingKey)
                key.append(c);
            else
                val.append(c);
        }
        saveEntry(key.toString(), val.toString(), map);
    }

    private static void saveEntry(String rawKey, String rawVal, Map<String, Object> map) {
        String key = rawKey.trim().replace("\"", "");
        String rawValTrimmed = rawVal.trim();

        if (key.isEmpty())
            return;

        if (rawValTrimmed.startsWith("\"") && rawValTrimmed.endsWith("\"")) {
            map.put(key, unescape(rawValTrimmed.substring(1, rawValTrimmed.length() - 1)));
        } else {
            try {
                if (key.equals("timestamp")) {
                    map.put(key, Long.parseLong(rawValTrimmed));
                } else if (key.equals("index")) {
                    map.put(key, Integer.parseInt(rawValTrimmed));
                } else if (key.equals("amount")) {
                    map.put(key, Double.parseDouble(rawValTrimmed));
                } else {
                    if (rawValTrimmed.contains("."))
                        map.put(key, Double.parseDouble(rawValTrimmed));
                    else
                        map.put(key, Long.parseLong(rawValTrimmed));
                }
            } catch (NumberFormatException e) {
                map.put(key, rawValTrimmed); // fallback
            }
        }
    }
}
