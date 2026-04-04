package life.light;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonToPdfResumeAndSkillsTest {

    @Test
    @DisplayName("Main devrait s'exécuter avec succès si les fichiers sont valides")
    void mainShouldSucceed() throws IOException {
        JsonToPdfResumeAndSkills.main();
        assertEquals(0, JsonToPdfResumeAndSkills.lastExitCode);

        // Cleanup generated files
        Files.deleteIfExists(new File("PRUT Christelle - CV.pdf").toPath());
        Files.deleteIfExists(new File("PRUT Christelle - Dossier de compétence.pdf").toPath());
    }

    @Test
    @DisplayName("Main devrait échouer si un fichier JSON est invalide")
    void mainShouldFailIfJsonInvalid() throws IOException {
        File cvFile = new File("CV.json");
        byte[] originalContent = Files.readAllBytes(cvFile.toPath());

        try {
            // Rendre le JSON invalide par rapport au schéma (ex : type incorrect pour Nom)
            String invalidJson = new String(originalContent).replace("\"Nom\": \"Test\"", "\"Nom\": 123");
            Files.writeString(cvFile.toPath(), invalidJson);

            JsonToPdfResumeAndSkills.main();
            assertEquals(-1, JsonToPdfResumeAndSkills.lastExitCode);
        } finally {
            // Restaurer le fichier original
            Files.write(cvFile.toPath(), originalContent);
        }
    }

    @Test
    @DisplayName("Main devrait échouer si un fichier est manquant")
    void mainShouldFailIfFileMissing() throws IOException {
        File cvFile = new File("CV.json");
        File tempCvFile = new File("CV.json.bak");
        Files.move(cvFile.toPath(), tempCvFile.toPath());

        try {
            JsonToPdfResumeAndSkills.main();
            assertEquals(-1, JsonToPdfResumeAndSkills.lastExitCode);
        } finally {
            Files.move(tempCvFile.toPath(), cvFile.toPath());
        }
    }
}
