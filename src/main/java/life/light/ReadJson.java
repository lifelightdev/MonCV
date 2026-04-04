package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadJson {

    static JsonNode getCvJson(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(new File(path));
    }

    static void validateJson(JsonNode jsonNode, String schemaPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode;
        try (InputStream schemaStream = new FileInputStream(schemaPath)) {
            schemaNode = mapper.readTree(schemaStream);
        }

        List<String> errors = new ArrayList<>();
        validate(jsonNode, schemaNode, "", errors);

        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder("Erreur de validation JSON :\n");
            for (String error : errors) {
                message.append("- ").append(error).append("\n");
            }
            throw new IOException(message.toString());
        }
    }

    private static void validate(JsonNode node, JsonNode schema, String path, List<String> errors) {
        if (schema.has("required") && node.isObject()) {
            JsonNode required = schema.get("required");
            for (JsonNode req : required) {
                String fieldName = req.asText();
                if (!node.has(fieldName) || node.get(fieldName).isNull()) {
                    errors.add("Le champ '" + path + fieldName + "' est obligatoire.");
                }
            }
        }

        if (schema.has("properties") && node.isObject()) {
            JsonNode properties = schema.get("properties");
            Iterator<String> fieldNames = properties.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (node.has(fieldName)) {
                    JsonNode propertySchema = properties.get(fieldName);
                    validate(node.get(fieldName), propertySchema, path + fieldName + ".", errors);
                }
            }
        }

        if (schema.has("items") && node.isArray()) {
            JsonNode itemsSchema = schema.get("items");
            for (int i = 0; i < node.size(); i++) {
                validate(node.get(i), itemsSchema, path + "[" + i + "].", errors);
            }
        }
    }

}
