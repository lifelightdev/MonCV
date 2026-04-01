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

import java.io.File;
import java.io.FileOutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class CreatePDF {

    private static final Logger logger = System.getLogger(CreatePDF.class.getName());
    public static final int FONT_SIZE_NORMAL = 12;

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

            Image photo = addImage("ma_photo.png", 100);

            PdfPCell photoCell = new PdfPCell(photo);
            photoCell.setBorder(Rectangle.NO_BORDER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(photoCell);

            // --- COLONNE DROITE : NOM ET TITRE ---
            Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 24);
            Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL);

            Phrase infoPhrase = new Phrase();
            infoPhrase.add(new Chunk(cvJson.get("nom").asText() + "\n", titleFont));
            infoPhrase.add(new Chunk("\n" + cvJson.get("titre").asText() + "\n" + "\n", normalFont));

            JsonNode sousTitre = cvJson.get("sous titre");
            if (sousTitre != null && sousTitre.isArray()) {
                for (int i = 0; i < sousTitre.size(); i++) {
                    JsonNode exp = sousTitre.get(i);
                    String label = exp.asText();
                    Image icone = addImage("images" + File.separator + label + ".png", FONT_SIZE_NORMAL);
                    if (icone != null) {
                        infoPhrase.add(new Chunk(icone, -2, -2, true));
                    }
                    infoPhrase.add(new Chunk(" " + label, normalFont));
                    if (i < sousTitre.size() - 1) {
                        infoPhrase.add(new Chunk(" /  ", normalFont));
                    }
                }
            }

            PdfPCell textCell = new PdfPCell(infoPhrase);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
            textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            textCell.setPaddingLeft(10); // Petit espace entre la photo et le texte
            headerTable.addCell(textCell);

            // Ajouter la table au document
            document.add(headerTable);

            // --- RESTE DU DOCUMENT (Expériences) ---
            document.add(new Paragraph("Email: " + cvJson.get("email").asText(), normalFont));
            document.add(new Paragraph("Expériences Professionnelles :", normalFont));
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

    static Image addImage(String imagePath, float size) {
        try {
            Image photo = Image.getInstance(imagePath);
            // Ajuster la taille (ex: 100x100 pixels)
            photo.scaleToFit(size, size);
            return photo;
        } catch (Exception e) {
            logger.log(Level.ERROR, "L'image " + imagePath + " n'a pas été trouvée, génération du CV sans cette image", e.getMessage());
        }
        return null;
    }
}
