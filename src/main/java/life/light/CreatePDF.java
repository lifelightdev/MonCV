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
            Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 24);
            Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL);

            Phrase infoPhrase = new Phrase();
            infoPhrase.add(new Chunk(cvJson.get("nom").asText() + "\n", titleFont));
            infoPhrase.add(new Chunk("\n", normalFont));
            JsonNode titre = cvJson.get("titre");
            if (titre != null && titre.isArray()) {
                for (int i = 0; i < titre.size(); i++) {
                    JsonNode exp = titre.get(i);
                    infoPhrase.add(new Chunk(" " + exp.asText(), normalFont));
                    if (i < titre.size() - 1) {
                        infoPhrase.add(new Chunk("  / ", normalFont));
                    }
                }
            }
            infoPhrase.add(new Chunk("\n" + cvJson.get("titre").asText() + "\n", normalFont));

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
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            textCell.setPaddingLeft(10); // Petit espace entre la photo et le texte
            headerTable.addCell(textCell);

            // Ajouter la table au document
            document.add(headerTable);

            // --- RESTE DU DOCUMENT (Expériences) ---

            // Assemblage du paragraphe du téléphone
            Paragraph pTel = new Paragraph();
            Image iconeTel = addImage("images" + File.separator + "Téléphone.png", FONT_SIZE_NORMAL);
            if (iconeTel != null) {
                pTel.add(new Chunk(iconeTel, 0, 0, true));
            }
            pTel.add(new Chunk(" " + cvJson.get("Téléphone").asText(), normalFont));
            document.add(pTel);

            // Assemblage du paragraphe du mail
            Paragraph pEmail = new Paragraph();
            Image iconeEmail = addImage("images" + File.separator + "Email.png", FONT_SIZE_NORMAL);
            if (iconeEmail != null) {
                pEmail.add(new Chunk(iconeEmail, 0, 0, true));
            }
            pEmail.add(new Chunk(" " + cvJson.get("Email").asText(), normalFont));
            document.add(pEmail);

            // Assemblage du paragraphe de GitHub
            Paragraph pGitHub = new Paragraph();
            Image iconeGitHub = addImage("images" + File.separator + "GitHub.png", FONT_SIZE_NORMAL);
            if (iconeGitHub != null) {
                pGitHub.add(new Chunk(iconeGitHub, 0, 0, true));
            }
            pGitHub.add(new Chunk(" " + cvJson.get("GitHub").asText(), normalFont));
            document.add(pGitHub);


            // Assemblage du paragraphe de LinkedIn
            Paragraph pLinkedIn = new Paragraph();
            Image iconeLinkedIn = addImage("images" + File.separator + "LinkedIn.png", FONT_SIZE_NORMAL);
            if (iconeLinkedIn != null) {
                pLinkedIn.add(new Chunk(iconeLinkedIn, 0, 0, true));
            }
            pLinkedIn.add(new Chunk(" " + cvJson.get("LinkedIn").asText(), normalFont));
            document.add(pLinkedIn);


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
