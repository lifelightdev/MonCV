package life.light;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.lang.System.Logger;

public class PDFBoxBackground {

    private static final Logger logger = System.getLogger( PDFBoxBackground.class.getName() );
    private final String imagePath;

    public PDFBoxBackground(String imagePath) {
        this.imagePath = imagePath;
    }

    public void addBackground(PDDocument document, PDPage page) {
        try {
            PDImageXObject pdImage = PDImageXObject.createFromFile( imagePath, document );
            try (PDPageContentStream contentStream = new PDPageContentStream( document, page, PDPageContentStream.AppendMode.PREPEND, true, true )) {
                PDRectangle mediaBox = page.getMediaBox();
                contentStream.drawImage( pdImage, 0, 0, mediaBox.getWidth(), mediaBox.getHeight() );
            }
        } catch (IOException e) {
            logger.log( Logger.Level.ERROR, "Impossible d'ajouter l'image de fond: " + imagePath, e );
        }
    }
}
