package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadJsonTest {

    @TempDir
    Path tempDir; // Crée un dossier temporaire automatique pour les tests

    @Test
    void getCvJson_ShouldReturnValidJsonNode_WhenFileExists() throws IOException {
        // GIVEN : Préparation d'un fichier JSON temporaire
        Path filePath = tempDir.resolve("cv_test.json");
        String jsonContent = "{\"name\": \"John Doe\", \"role\": \"Developer\"}";
        Files.writeString(filePath, jsonContent);

        // WHEN : Appel de ta méthode statique
        // Remplace "YourClassName" par le nom réel de ta classe
        JsonNode result = ReadJson.getCvJson(filePath.toString());

        // THEN : Assertions standard JUnit 5
        assertNotNull(result, "Le JsonNode ne devrait pas être null");
        assertTrue(result.has("name"), "Le JSON devrait contenir un champ 'name'");
        assertEquals("John Doe", result.get("name").asText());
    }
}