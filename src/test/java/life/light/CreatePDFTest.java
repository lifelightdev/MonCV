package life.light;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lowagie.text.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        cvJson.put("nom", "Jean Dupont");
        cvJson.put("titre", "Développeur Java");
        cvJson.put("email", "jean.dupont@example.com");

        ArrayNode experiences = cvJson.putArray("experience");
        experiences.add("Développeur Senior chez Google");
        experiences.add("Stagiaire chez Oracle");

        // WHEN : Appel de la méthode
        CreatePDF.creatPDF(cvJson);

        // THEN : Vérifications
        File pdfFile = new File(PDF_NAME);

        assertAll("Vérification du PDF",
                () -> assertTrue(pdfFile.exists(), "Le fichier PDF devrait être créé"),
                () -> assertTrue(pdfFile.length() > 0, "Le fichier PDF ne devrait pas être vide")
        );
    }

    @Test
    void addPhoto_ShouldAddImageToDocument_WhenFileExists() throws Exception {
        // GIVEN : Création d'une image factice (.png) pour le test
        File testImage = new File("ma_photo_test.png"); // Le code cherche ce nom précis
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(bufferedImage, "png", testImage);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);

        document.open();

        // WHEN
        // On s'assure que l'appel ne fait pas planter le test
        assertDoesNotThrow(() -> CreatePDF.addPhoto(document));

        document.close();

        // THEN
        // On vérifie que le flux de sortie contient des données (le PDF n'est pas vide)
        assertTrue(out.size() > 0, "Le PDF généré devrait contenir des données");

        // CLEANUP : Supprimer l'image créée pour le test
        if (testImage.exists()) {
            testImage.delete();
        }
    }

    @Test
    void addPhoto_ShouldNotThrowException_WhenFileIsMissing() {
        // GIVEN : Un document sans fichier image présent sur le disque
        Document document = new Document();
        document.open();

        // WHEN & THEN : La méthode doit logger l'erreur mais ne pas faire crash le test
        assertDoesNotThrow(() -> CreatePDF.addPhoto(document));

        if (document.isOpen()) {
            document.close();
        }
    }
}