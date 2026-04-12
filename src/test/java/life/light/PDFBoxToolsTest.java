package life.light;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFBoxToolsTest {

    @Test
    @DisplayName("calculateTextHeight devrait renvoyer une hauteur proportionnelle au nombre de lignes")
    void shouldCalculateTextHeightCorrectly() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, null );
            float fontSize = 12f;
            PDType1Font font = PDType1Font.HELVETICA;

            // Une seule ligne
            String shortText = "Petit texte";
            float height1 = tools.calculateTextHeight( shortText, fontSize, font );
            assertEquals( fontSize * 1.2f, height1, 0.01f );

            // Texte long qui doit wrapper (plus de 600 points de large sur une page A4 avec marges)
            StringBuilder longText = new StringBuilder();
            longText.repeat( "mot ", 100 );
            float heightLong = tools.calculateTextHeight( longText.toString(), fontSize, font );
            assertTrue( heightLong > height1, "Le texte long devrait être plus haut" );

            // Vérifier que c'est un multiple de (fontSize * 1.2)
            float ratio = heightLong / (fontSize * 1.2f);
            assertEquals( Math.round( ratio ), ratio, 0.01f );
        }
    }

    @Test
    @DisplayName("addNewPage devrait réinitialiser le curseur Y")
    void shouldResetCursorYOnNewPage() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, null );
            float initialY = tools.getCursorY();

            tools.setCursorY( 100f );
            assertEquals( 100f, tools.getCursorY() );

            tools.addNewPage();
            assertEquals( initialY, tools.getCursorY() );
            assertEquals( 2, document.getNumberOfPages() );
        }
    }
}
