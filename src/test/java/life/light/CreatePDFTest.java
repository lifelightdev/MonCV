package life.light;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatePDFTest {

    private final String PDF_NAME = "Mon_CV.pdf";

    @AfterEach
    void cleanUp() throws Exception {
        // Supprime le fichier après le test pour garder le projet propre
        Files.deleteIfExists(Paths.get(PDF_NAME));
    }

    @Test
    void creatPDF_ShouldGenerateNonEmptyFile() {
        // GIVEN : Construction d'un JsonNode factice
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cvJson = mapper.createObjectNode();
        cvJson.put("Nom", "Jean Dupont");
        cvJson.put("Titre", "Développeur Java");
        cvJson.put("Tmail", "jean.dupont@example.com");

        ArrayNode experiences = cvJson.putArray("experience");
        experiences.add("Développeur Senior chez Google");
        experiences.add("Stagiaire chez Oracle");

        // WHEN : Appel de la méthode
        CreateCVPDF.creatPDF(cvJson);

        // THEN : Vérifications
        File pdfFile = new File(PDF_NAME);

        assertAll("Vérification du PDF",
                () -> assertTrue(pdfFile.exists(), "Le fichier PDF devrait être créé"),
                () -> assertTrue(pdfFile.length() > 0, "Le fichier PDF ne devrait pas être vide")
        );
    }

    @Test
    @DisplayName("L'image doit être ajoutée au document si le fichier existe")
    void shouldAddPhotoToDocumentWhenFileExists() throws java.io.IOException {
        // GIVEN : Création d'une image factice (.png) pour le test
        File testImage = new File("ma_photo_test.png"); // Le code cherche ce nom précis
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(bufferedImage, "png", testImage);
        assertDoesNotThrow(() -> CreateCVPDF.addImage("ma_photo_test.png", 100));
        // CLEANUP : Supprimer l'image créée pour le test
        if (testImage.exists()) {
            testImage.delete();
        }
    }

}