package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class CreatePDF {

    private static final Logger logger = System.getLogger(CreatePDF.class.getName());

    static void creatPDF(JsonNode cvJson) {
        Document document = new Document();

        try {
            String nameFileCVPDF = "Mon_CV.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(nameFileCVPDF));

            document.open();

            // 3. Ajouter du contenu stylisé
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Nom et Titre
            document.add(new Paragraph(cvJson.get("nom").asText(), titleFont));
            document.add(new Paragraph(cvJson.get("titre").asText(), subTitleFont));
            document.add(new Paragraph("Email: " + cvJson.get("email").asText(), normalFont));
            document.add(new Chunk("\n")); // Espacement

            // Section Expériences
            document.add(new Paragraph("Expériences Professionnelles :", subTitleFont));
            JsonNode experiences = cvJson.get("experience");
            if (experiences.isArray()) {
                List list = new List(List.UNORDERED);
                for (JsonNode exp : experiences) {
                    list.add(new ListItem(exp.asText(), normalFont));
                }
                document.add(list);
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, "Échec de la génération du CV", e.getMessage());
        } finally {
            if (document.isOpen()) {
                // 4. Fermer le document
                document.close();
            }
        }
    }
}
