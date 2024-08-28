package com.bajajFinserv;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;

public class JsonProcessor {
	public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar JsonProcessor.jar <PRN_Number> <json_file_path>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            String destinationValue = getDestinationValue(jsonFilePath);
            String randomString = generateRandomString();
            String hashInput = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(hashInput);

            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.err.println("Error processing JSON file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String getDestinationValue(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
        return traverseJson(rootNode);
    }

    private static String traverseJson(JsonNode node) {
        if (node.isObject()) {
            if (node.has("destination")) {
                return node.get("destination").asText();
            }
            for (JsonNode childNode : node) {
                String result = traverseJson(childNode);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String result = traverseJson(arrayElement);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private static String generateMD5Hash(String input) {
        return DigestUtils.md5Hex(input);
    }
	

}
