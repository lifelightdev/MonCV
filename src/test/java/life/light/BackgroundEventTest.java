package life.light;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BackgroundEventTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Devrait lever une exception si le fichier image n'existe pas")
    void shouldThrowExceptionWhenImageDoesNotExist() {
        assertThrows(Exception.class, () -> new BackgroundEvent("non_existent_image.png"));
    }

    @Test
    @DisplayName("Devrait s'instancier correctement si l'image existe")
    void shouldInstantiateWhenImageExists() throws Exception {
        Path imagePath = tempDir.resolve("test_bg.png");
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(bufferedImage, "png", imagePath.toFile());

        assertDoesNotThrow(() -> new BackgroundEvent(imagePath.toString()));
    }

    @Test
    @DisplayName("onEndPage devrait s'exécuter sans erreur")
    void onEndPageShouldRunWithoutError() throws Exception {
        Path imagePath = tempDir.resolve("test_bg.png");
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(bufferedImage, "png", imagePath.toFile());

        BackgroundEvent event = new BackgroundEvent(imagePath.toString());

        Document document = new Document();
        File pdfFile = tempDir.resolve("test.pdf").toFile();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        assertDoesNotThrow(() -> event.onEndPage(writer, document));
        document.close();
    }
}
