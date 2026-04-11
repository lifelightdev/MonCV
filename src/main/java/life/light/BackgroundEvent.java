package life.light;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.lang.System.Logger;

public class BackgroundEvent extends PdfPageEventHelper {

    private static final Logger logger = System.getLogger( BackgroundEvent.class.getName() );
    private final Image img;
    private String employerReminder = "";

    public BackgroundEvent(String imagePath) throws Exception {
        this.img = Image.getInstance( imagePath );
        // Positionne l'image en bas à gauche (0,0)
        this.img.setAbsolutePosition( 0, 0 );
    }

    public void setEmployerReminder(String employerReminder) {
        this.employerReminder = employerReminder;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // Le "DirectContentUnder" place l'élément sous le texte
        PdfContentByte canvas = writer.getDirectContentUnder();
        try {
            canvas.addImage( img );
        } catch (Exception e) {
            logger.log( Logger.Level.ERROR, "Erreur ", e.getMessage() );
        }

        if (employerReminder != null && !employerReminder.isEmpty() && writer.getPageNumber() > 1) {
            PdfContentByte cb = writer.getDirectContent();
            cb.beginText();
            try {
                BaseFont bf = BaseFont.createFont( BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED );
                cb.setFontAndSize( bf, 10 );
                cb.showTextAligned( PdfContentByte.ALIGN_LEFT, "Employeur : " + employerReminder, document.left(), document.top() + 15, 0 );
            } catch (Exception e) {
                logger.log( Logger.Level.ERROR, "Erreur lors de l'ajout du rappel de l'employeur", e );
            }
            cb.endText();
        }
    }
}
