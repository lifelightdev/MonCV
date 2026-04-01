package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
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
import java.util.Objects;

public class CreatePDF {

    private static final Logger logger = System.getLogger(CreatePDF.class.getName());
    public static final int FONT_SIZE_NORMAL = 12;
    static Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 24, Font.BOLD);
    static Font subtitleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    static Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL);
    static Font emptyLineFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);

    static void creatPDF(JsonNode cvJson) {
        Document document = new Document();

        try {
            String nameFileCVPDF = "Mon_CV.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(nameFileCVPDF));

            document.open();

            //Création d'une table à 2 colonnes
            // On définit la largeur relative des colonnes (ex: 1/3 pour la photo, 2/3 pour le texte)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});

            headerTable.setSpacingAfter(20);

            Image photo = addImage("ma_photo.png", 100);

            PdfPCell photoCell = new PdfPCell(photo);
            photoCell.setBorder(Rectangle.NO_BORDER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(photoCell);

            Phrase infoPhrase = new Phrase();
            infoPhrase.add(new Chunk(cvJson.get("Nom").asText() + "\n", titleFont));
            infoPhrase.add(new Chunk("\n", emptyLineFont));
            JsonNode titre = cvJson.get("Titre");
            if (titre != null && titre.isArray()) {
                for (int i = 0; i < titre.size(); i++) {
                    JsonNode exp = titre.get(i);
                    infoPhrase.add(new Chunk(" " + exp.asText(), subtitleFont));
                    if (i < titre.size() - 1) {
                        infoPhrase.add(new Chunk("  / ", subtitleFont));
                    }
                }
            }
            infoPhrase.add(new Chunk("\n" + Objects.requireNonNull(titre).asText(), subtitleFont));
            infoPhrase.add(new Chunk("\n", emptyLineFont));

            JsonNode sousTitre = cvJson.get("Sous titre");
            if (sousTitre != null && sousTitre.isArray()) {
                for (int i = 0; i < sousTitre.size(); i++) {
                    JsonNode exp = sousTitre.get(i);
                    String label = exp.asText();
                    Image icone = addImage("images" + File.separator + label + ".png", FONT_SIZE_NORMAL);
                    if (icone != null) {
                        infoPhrase.add(new Chunk(icone, -2, -2, true));
                    }
                    infoPhrase.add(new Chunk(" " + label, subtitleFont));
                    if (i < sousTitre.size() - 1) {
                        infoPhrase.add(new Chunk(" /  ", subtitleFont));
                    }
                }
            }

            PdfPCell textCell = new PdfPCell(infoPhrase);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            textCell.setPaddingLeft(10); // Petit espace entre la photo et le texte
            headerTable.addCell(textCell);

            // Ajouter la table au document
            document.add(headerTable);

            addIconeFirst("Téléphone", cvJson, document);
            addIconeFirst("Email", cvJson, document);
            addIconeFirst("GitHub", cvJson, document);
            addIconeFirst("LinkedIn", cvJson, document);

            addBulletedList("Compétences", cvJson, document);
            addBulletedList("experience", cvJson, document);


        } catch (Exception e) {
            logger.log(Level.ERROR, "Échec de la génération du CV", e.getMessage());
        } finally {
            if (document.isOpen()) {
                // Fermer le document
                document.close();
            }
        }
    }

    static void addBulletedList(String name, JsonNode cvJson, Document document) {
        Image icone = addImage("images" + java.io.File.separator + "Puce.png", 8);
        document.add(new com.lowagie.text.Paragraph(name + " :", subtitleFont));
        com.fasterxml.jackson.databind.JsonNode competences = cvJson.get(name);
        if (competences != null && competences.isArray()) {
            com.lowagie.text.List list = new com.lowagie.text.List();
            for (int i = 0; i < competences.size(); i++) {
                com.fasterxml.jackson.databind.JsonNode exp = competences.get(i);
                String label = exp.asText();
                com.lowagie.text.Paragraph paragraph = new com.lowagie.text.Paragraph();
                paragraph.add(new com.lowagie.text.Chunk(icone, 0, 0, true));
                paragraph.add(new com.lowagie.text.Chunk(" " + label, normalFont));
                document.add(paragraph);
            }
            document.add(list);
        }
    }

    static void addIconeFirst(String image, JsonNode cvJson, Document document) {
        Paragraph paragraph = new Paragraph();
        Image icone = addImage("images" + java.io.File.separator + image + ".png", FONT_SIZE_NORMAL);
        if (icone != null) {
            paragraph.add(new com.lowagie.text.Chunk(icone, 0, 0, true));
        }
        paragraph.add(new com.lowagie.text.Chunk(" " + cvJson.get(image).asText(), normalFont));
        document.add(paragraph);
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
