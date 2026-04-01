package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
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

            //Création d'une table à 2 colonnes
            // On définit la largeur relative des colonnes (ex: 1/4 pour la photo, 3/4 pour le texte)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});
            headerTable.setSpacingAfter(20);

            Image photo = addPhoto();

            PdfPCell photoCell = new PdfPCell(photo);
            photoCell.setBorder(Rectangle.NO_BORDER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(photoCell);

            // --- COLONNE DROITE : NOM ET TITRE ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Phrase infoPhrase = new Phrase();
            infoPhrase.add(new Chunk(cvJson.get("nom").asText() + "\n", titleFont));
            infoPhrase.add(new Chunk(cvJson.get("titre").asText() + "\n", subTitleFont));
            infoPhrase.add(new Chunk("Email: " + cvJson.get("email").asText(), normalFont));

            PdfPCell textCell = new PdfPCell(infoPhrase);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            textCell.setPaddingLeft(10); // Petit espace entre la photo et le texte
            headerTable.addCell(textCell);

            // Ajouter la table au document
            document.add(headerTable);

            // --- RESTE DU DOCUMENT (Expériences) ---
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
                // Fermer le document
                document.close();
            }
        }
    }

    static Image addPhoto() {
        try {
            // Chemin vers ta photo (tu peux aussi le récupérer depuis ton cvJson)
            String imagePath = "ma_photo.png";
            Image photo = Image.getInstance(imagePath);

            // Ajuster la taille (ex: 100x100 pixels)
            photo.scaleToFit(100, 100);

            return photo;
        } catch (Exception e) {
            logger.log(Level.ERROR, "Photo non trouvée, génération du CV sans image", e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
