package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CreateSkillsPDFTest {

    @Test
    @DisplayName("La génération complète du dossier de compétences doit fonctionner avec un JSON valide")
    void shouldGenerateFullSkills() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode skillsJson = mapper.readTree(new File("DossierCompetences.json"));

        // On change le nom pour ne pas écraser le vrai PDF
        ((com.fasterxml.jackson.databind.node.ObjectNode) skillsJson).put("Nom", "TEST_SKILLS");
        ((com.fasterxml.jackson.databind.node.ObjectNode) skillsJson).put("Prénom", "Test");

        assertDoesNotThrow(() -> CreateSkillsPDF.createSkills(skillsJson));

        File generatedFile = new File("TEST_SKILLS Test - Dossier de compétence.pdf");
        if (generatedFile.exists()) {
            Files.delete(generatedFile.toPath());
        }
    }
}
