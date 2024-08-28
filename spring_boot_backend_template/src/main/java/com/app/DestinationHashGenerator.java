package com.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <JSON File Path>");
            return;
        }

        String prn = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));
           
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the provided JSON file.");
                return;
            }
         
            String randomString = generateRandomString(8);
            String inputToHash = prn + destinationValue + randomString;
            String md5Hash = generateMD5Hash(inputToHash);
            
            System.out.println(md5Hash + ";" + randomString);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestinationValue(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                String result = findDestinationValue(arrayItem);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
