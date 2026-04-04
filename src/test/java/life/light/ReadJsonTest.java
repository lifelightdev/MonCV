package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadJsonTest {

    @TempDir
    Path tempDir;

    @Test
    void getCvJson_ShouldReturnValidJsonNode_WhenFileExists() throws IOException {
        Path filePath = tempDir.resolve("cv_test.json");
        String jsonContent = "{\"name\": \"John Doe\", \"role\": \"Developer\"}";
        Files.writeString(filePath, jsonContent);

        JsonNode result = ReadJson.getCvJson(filePath.toString());

        assertNotNull(result, "Le JsonNode ne devrait pas être null");
        assertTrue(result.has("name"), "Le JSON devrait contenir un champ 'name'");
        assertEquals("John Doe", result.get("name").asText());
    }

    @Test
    void validateJson_ShouldNotThrowException_WhenJsonIsValid() throws IOException {
        String jsonContent = "{\"$schema\": \"./schema.json\", \"Nom\": \"Test\"}";
        String schemaContent = "{\"$schema\": \"http://json-schema.org/draft-07/schema#\", \"type\": \"object\", \"properties\": {\"Nom\": {\"type\": \"string\"}}}";

        Path jsonPath = tempDir.resolve("valid.json");
        Path schemaPath = tempDir.resolve("schema.json");

        Files.writeString(jsonPath, jsonContent);
        Files.writeString(schemaPath, schemaContent);

        JsonNode node = ReadJson.getCvJson(jsonPath.toString());
        assertDoesNotThrow(() -> ReadJson.validateJson(node, schemaPath.toString()));
    }

    @Test
    void validateJson_ShouldThrowIOException_WhenRequiredFieldIsMissing() throws IOException {
        String jsonContent = "{\"Autre\": \"Test\"}";
        String schemaContent = "{\"type\": \"object\", \"required\": [\"Nom\"]}";

        Path jsonPath = tempDir.resolve("missing_field.json");
        Path schemaPath = tempDir.resolve("schema.json");

        Files.writeString(jsonPath, jsonContent);
        Files.writeString(schemaPath, schemaContent);

        JsonNode node = ReadJson.getCvJson(jsonPath.toString());
        IOException exception = Assertions.assertThrows(IOException.class, () -> ReadJson.validateJson(node, schemaPath.toString()));
        assertTrue(exception.getMessage().contains("est obligatoire"), "Le message d'erreur devrait indiquer que le champ est obligatoire. Message actuel : " + exception.getMessage());
        assertTrue(exception.getMessage().contains("'Nom'"), "Le message devrait mentionner le nom du champ. Message actuel : " + exception.getMessage());
    }

    @Test
    void validateJson_ShouldThrowIOException_WhenNestedRequiredFieldIsMissing() throws IOException {
        String jsonContent = "{\"Info\": {\"Age\": 30}}";
        String schemaContent = "{\"type\": \"object\", \"properties\": {\"Info\": {\"type\": \"object\", \"required\": [\"Nom\"]}}}";

        Path jsonPath = tempDir.resolve("nested_missing.json");
        Path schemaPath = tempDir.resolve("schema_nested.json");

        Files.writeString(jsonPath, jsonContent);
        Files.writeString(schemaPath, schemaContent);

        JsonNode node = ReadJson.getCvJson(jsonPath.toString());
        IOException exception = Assertions.assertThrows(IOException.class, () -> ReadJson.validateJson(node, schemaPath.toString()));
        assertTrue(exception.getMessage().contains("Info.Nom"), "Le message d'erreur devrait contenir le chemin complet 'Info.Nom'. Message actuel : " + exception.getMessage());
    }

    @Test
    void getCvJson_ShouldThrowIOException_WhenFileDoesNotExist() {
        assertThrows(IOException.class, () -> ReadJson.getCvJson("non_existent.json"));
    }

    @Test
    void validateJson_ShouldThrowIOException_WhenSchemaDoesNotExist() throws IOException {
        JsonNode node = new ObjectMapper().readTree("{}");
        assertThrows(IOException.class, () -> ReadJson.validateJson(node, "non_existent_schema.json"));
    }
}