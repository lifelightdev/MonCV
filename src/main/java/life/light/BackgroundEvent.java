package life.light;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.lang.System.Logger;

public class BackgroundEvent extends PdfPageEventHelper {

    private static final Logger logger = System.getLogger(BackgroundEvent.class.getName());

    private final Image img;

    public BackgroundEvent(String imagePath) throws Exception {
        this.img = Image.getInstance(imagePath);
        // Positionne l'image en bas à gauche (0,0)
        this.img.setAbsolutePosition(0, 0);
        // Ajuste l'image à la taille de la page (ex: A4)
        // Les dimensions A4 sont environ 595x842 points
        this.img.scaleAbsolute(595, 842);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // Le "DirectContentUnder" place l'élément sous le texte
        PdfContentByte canvas = writer.getDirectContentUnder();
        try {
            canvas.addImage(img);
        } catch (Exception e) {
            logger.log(Logger.Level.ERROR, "Erreur ", e.getMessage());
        }
    }
}
