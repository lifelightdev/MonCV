package life.light;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.TIMES_BOLD;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.TIMES_ROMAN;

public class PDFBoxTools {

    public static final PDFont FONT_PLAIN = new PDType1Font( TIMES_ROMAN );
    public static final PDFont FONT_BOLD = new PDType1Font( TIMES_BOLD );

    private final PDDocument document;
    private PDPageContentStream contentStream;
    private float cursorY;
    private final float marginLR = 28f;
    private final float marginBottom = 20f;
    private final float width;
    private final PDFBoxBackground background;
    private Runnable onNewPage;

    public PDFBoxTools(PDDocument document, String backgroundPath) throws IOException {
        this.document = document;
        this.width = A4.getWidth();
        this.background = (backgroundPath != null && new java.io.File( backgroundPath ).exists()) ? new PDFBoxBackground( backgroundPath ) : null;
        addNewPage();
    }

    public void addNewPage() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
        PDPage currentPage = new PDPage( A4 );
        document.addPage( currentPage );
        if (background != null) {
            background.addBackground( document, currentPage );
        }
        contentStream = new PDPageContentStream( document, currentPage, APPEND, true, true );
        float marginTop = 20f;
        cursorY = A4.getHeight() - marginTop;
        if (onNewPage != null) {
            onNewPage.run();
        }
    }

    public void setOnNewPage(Runnable onNewPage) {
        this.onNewPage = onNewPage;
    }

    public void close() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
    }

    public void addText(String text, float fontSize, PDFont font) throws IOException {
        addText( text, fontSize, font, marginLR, width - 2 * marginLR );
    }

    public void addText(String text, float fontSize, PDFont font, float startX, float maxWidth) throws IOException {
        List<String> lines = wrapText( text, fontSize, font, maxWidth );
        for (String line : lines) {
            if (cursorY < marginBottom + fontSize) {
                addNewPage();
            }
            contentStream.beginText();
            contentStream.setFont( font, fontSize );
            contentStream.newLineAtOffset( startX, cursorY );
            contentStream.showText( line );
            contentStream.endText();
            cursorY -= (fontSize * 1.2f);
        }
    }

    public void addCenteredText(String text, float fontSize, PDFont font) throws IOException {
        float textWidth = font.getStringWidth( text ) / 1000 * fontSize;
        float startX = (width - textWidth) / 2;
        addText( text, fontSize, font, startX, width - 2 * marginLR );
    }

    private List<String> wrapText(String text, float fontSize, PDFont font, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split( "\\s+" );
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            float textWidth = font.getStringWidth( testLine ) / 1000 * fontSize;
            if (textWidth > maxWidth) {
                lines.add( currentLine.toString() );
                currentLine = new StringBuilder( word );
            } else {
                currentLine = new StringBuilder( testLine );
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add( currentLine.toString() );
        }
        return lines;
    }

    public float getCursorY() {
        return cursorY;
    }

    public void setCursorY(float cursorY) {
        this.cursorY = cursorY;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public PDDocument getDocument() {
        return document;
    }

    public float calculateTextHeight(String text, float fontSize, PDFont font) throws IOException {
        return calculateTextHeight( text, fontSize, font, width - 2 * marginLR );
    }

    public float calculateTextHeight(String text, float fontSize, PDFont font, float maxWidth) throws IOException {
        List<String> lines = wrapText( text, fontSize, font, maxWidth );
        return lines.size() * (fontSize * 1.2f);
    }

    public float getMarginBottom() {
        return marginBottom;
    }

}
