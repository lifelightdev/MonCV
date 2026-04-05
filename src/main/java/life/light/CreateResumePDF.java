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
import com.lowagie.text.PageSize;
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

public class CreateResumePDF {

    private static final Logger logger = System.getLogger(CreateResumePDF.class.getName());
    public static final int FONT_SIZE_NORMAL = 12;

    static Font titleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 24, Font.BOLD);
    static Font subtitleFontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    static Font subtitleFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14);
    static Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL);
    static Font normalFontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL, Font.BOLD);
    static Font normalFontUnderline = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL, Font.UNDERLINE);
    static Font emptyLineFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
    static Image icone;

    static {
        try {
            icone = addImage("images" + java.io.File.separator + "Puce1.png", 8);
        } catch (Exception e) {
            // En test, l'image peut ne pas exister, on ignore
            icone = null;
        }
    }

    static void createResume(JsonNode resumeJson) {
        // Les valeurs sont en "points" (72 points = 1 pouce = 2,54 cm)
        // Ici, on met environ 1 cm de marge partout (28 points).
        float margeGauche = 28f;
        float margeDroite = 28f;
        float margeHaut = 20f;
        float margeBas = 20f;

        Document document = new Document(PageSize.A4, margeGauche, margeDroite, margeHaut, margeBas);

        try {
            String nameFileCVPDF = resumeJson.get("Nom").asText() + " " + resumeJson.get("Prénom").asText() + " - CV.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nameFileCVPDF));

            // On attache l'image de fond
            try {
                BackgroundEvent event = new BackgroundEvent("images/Fond.png");
                writer.setPageEvent(event);
            } catch (Exception e) {
                // Fond optionnel pour les tests
            }

            document.open();
            addHeader(resumeJson, document);
            addSubHeader(resumeJson, document);
            addBody(resumeJson, document);
        } catch (Exception e) {
            logger.log(Level.ERROR, "Échec de la génération du CV", e.getMessage());
        } finally {
            if (document.isOpen()) {
                // Fermer le document
                document.close();
            }
        }
    }

    static void addBody(JsonNode resumeJson, Document document) {
        //Création d'une table à 2 colonnes
        PdfPTable bodyTable = getPdfPTable();

        Paragraph infoPhraseLeft = new Paragraph();
        infoPhraseLeft.add(addCompetences(resumeJson));
        infoPhraseLeft.add(addLangues(resumeJson));
        infoPhraseLeft.add(addFormations(resumeJson));
        PdfPCell textCellLeft = new PdfPCell(infoPhraseLeft);
        textCellLeft.setBorder(Rectangle.NO_BORDER);
        textCellLeft.setVerticalAlignment(Element.ALIGN_TOP);
        textCellLeft.setLeading(0, 1.2f);
        bodyTable.addCell(textCellLeft);

        Phrase infoPhraseRight = new Phrase();
        infoPhraseRight.add(addExperiences(resumeJson));
        PdfPCell textCellRight = new PdfPCell(infoPhraseRight);
        textCellRight.setBorder(Rectangle.NO_BORDER);
        textCellRight.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        textCellRight.setVerticalAlignment(Element.ALIGN_TOP);
        textCellRight.setLeading(0, 1.2f);
        bodyTable.addCell(textCellRight);

        // Ajouter la table au document
        document.add(bodyTable);
    }

    private static PdfPTable getPdfPTable() {
        // On définit la largeur relative des colonnes (ex : 1/3 pour la photo, 2/3 pour le texte).
        PdfPTable bodyTable = new PdfPTable(2);
        bodyTable.setWidthPercentage(100);
        bodyTable.setWidths(new float[]{1, 2});
        return bodyTable;
    }

    static void addHeader(JsonNode resumeJson, Document document) {
        PdfPTable headerTable = getPdfPTable();

        Image photo = null;
        try {
            photo = addImage("Ma_Photo.jpg", 70);
        } catch (Exception e) {
            // Photo optionnelle pour les tests
            logger.log(Level.ERROR, "La photo du CV n'a pas été trouvée", e.getMessage());
        }

        PdfPCell photoCell = new PdfPCell(photo);
        photoCell.setBorder(Rectangle.NO_BORDER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(photoCell);

        Phrase infoPhrase = new Phrase();
        infoPhrase.add(new Chunk(resumeJson.get("Prénom").asText() + " " + resumeJson.get("Nom").asText() + "\n ", titleFont));
        JsonNode titre = resumeJson.get("Titre");
        if (titre != null && titre.isArray()) {
            for (int i = 0; i < titre.size(); i++) {
                JsonNode exp = titre.get(i);
                infoPhrase.add(new Chunk(" " + exp.asText(), subtitleFontBold));
                if (i < titre.size() - 1) {
                    infoPhrase.add(new Chunk("  / ", subtitleFontBold));
                }
            }
        }
        infoPhrase.add(new Chunk("\n" + Objects.requireNonNull(titre).asText(), subtitleFontBold));
        JsonNode sousTitre = resumeJson.get("Sous titre");
        if (sousTitre != null && sousTitre.isArray()) {
            for (int i = 0; i < sousTitre.size(); i++) {
                JsonNode exp = sousTitre.get(i);
                String label = exp.asText();
                Image icone = addImage("images" + File.separator + label + ".png", FONT_SIZE_NORMAL);
                if (icone != null) {
                    infoPhrase.add(new Chunk(icone, -2, -2, true));
                }
                infoPhrase.add(new Chunk(" " + label, subtitleFontBold));
                if (i < sousTitre.size() - 1) {
                    infoPhrase.add(new Chunk(" /  ", subtitleFontBold));
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
    }

    static void addSubHeader(JsonNode resumeJson, Document document) {
        //Création d'une table à 2 colonnes
        // On définit la largeur relative des colonnes (ex : 1/3 pour la photo, 2/3 pour le texte)
        com.lowagie.text.pdf.PdfPTable subHeaderTable = getPdfPTable();

        Phrase infoPhraseLeft = new Phrase();
        infoPhraseLeft.add(addIconeFirst("Téléphone", resumeJson));
        infoPhraseLeft.add(new Chunk("\n ", emptyLineFont));
        infoPhraseLeft.add(addIconeFirst("Email", resumeJson));
        infoPhraseLeft.add(new Chunk("\n ", emptyLineFont));
        infoPhraseLeft.add(addIconeFirst("GitHub", resumeJson));
        infoPhraseLeft.add(new Chunk("\n ", emptyLineFont));
        infoPhraseLeft.add(addIconeFirst("LinkedIn", resumeJson));
        infoPhraseLeft.add(new Chunk("\n ", emptyLineFont));
        PdfPCell textCellLeft = new PdfPCell(infoPhraseLeft);
        textCellLeft.setBorder(Rectangle.NO_BORDER);
        textCellLeft.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        textCellLeft.setVerticalAlignment(Element.ALIGN_TOP);
        textCellLeft.setLeading(0, 1.5f);
        subHeaderTable.addCell(textCellLeft);

        Paragraph paragraph = new Paragraph(resumeJson.get("Présentation").asText(), subtitleFont);
        PdfPCell textCellRight = new PdfPCell(paragraph);
        textCellRight.setBorder(Rectangle.NO_BORDER);
        textCellRight.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        textCellRight.setVerticalAlignment(Element.ALIGN_TOP);
        textCellRight.setLeading(0, 1.3f);
        subHeaderTable.addCell(textCellRight);

        document.add(subHeaderTable);

    }

    static List addCompetences(JsonNode cvJson) {
        List listCompetences = new List(List.UNORDERED);
        listCompetences.setListSymbol(new Chunk(""));
        ListItem item = new ListItem();
        item.add(new Chunk("Compétences".toUpperCase() + " ", subtitleFontBold));
        item.add(new Chunk("\n \n ", emptyLineFont));
        JsonNode competences = cvJson.get("Compétences");
        if (competences != null && competences.isArray()) {
            for (int i = 0; i < competences.size(); i++) {
                JsonNode competence = competences.get(i);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Chunk(icone, 0, 0, true));
                for (int j = 0; j < competence.size(); j++) {
                    JsonNode exp = competence.get(j);
                    Font font = normalFont;
                    if (exp.get("EnGras").asBoolean()) {
                        font = normalFontBold;
                    }
                    paragraph.add(new Chunk(" " + exp.get("Description").asText(), font));
                    if (j < competence.size() - 1) {
                        paragraph.add(new Chunk(",", font));
                    }
                }
                item.add(paragraph);
                item.add(new Chunk("\n ", emptyLineFont));
            }
        }
        listCompetences.add(item);
        return listCompetences;
    }

    static List addFormations(JsonNode resumeJson) {
        List listFormations = new List(List.UNORDERED);
        listFormations.setListSymbol(new Chunk(""));
        ListItem item = new ListItem();
        item.add(new Chunk("Formations".toUpperCase(), subtitleFontBold));
        item.add(new Chunk("\n \n ", emptyLineFont));
        JsonNode competences = resumeJson.get("Formations");
        if (competences != null && competences.isArray()) {
            for (int i = 0; i < competences.size(); i++) {
                JsonNode formation = competences.get(i);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Chunk(icone, 0, 0, true));
                paragraph.add(new Chunk(" " + formation.get("Titre").asText(), normalFont));
                paragraph.add(new Chunk(" \n ", normalFont));
                paragraph.add(new Chunk("   " + formation.get("Année").asText(), normalFont));
                item.add(paragraph);
                item.add(new Chunk("\n ", emptyLineFont));
            }
        }
        listFormations.add(item);
        return listFormations;
    }

    static List addLangues(JsonNode resumeJson) {
        List listLangues = new List(List.UNORDERED);
        listLangues.setListSymbol(new Chunk(""));
        ListItem item = new ListItem();
        item.add(new Chunk("Langues".toUpperCase(), subtitleFontBold));
        item.add(new Chunk("\n \n ", emptyLineFont));
        JsonNode langues = resumeJson.get("Langues");
        if (langues != null && langues.isArray()) {
            for (int i = 0; i < langues.size(); i++) {
                JsonNode langue = langues.get(i);
                Paragraph paragraph = new Paragraph();
                Image drapeau = addImage("images" + java.io.File.separator + langue.get("Nom").asText() + ".png", 16);
                paragraph.add(new Chunk(drapeau, 0, 0, true));
                paragraph.add(new Chunk(" " + langue.get("Niveau").asText(), normalFont));
                item.add(paragraph);
                item.add(new Chunk("\n ", emptyLineFont));
            }
        }
        listLangues.add(item);
        return listLangues;
    }

    static List addExperiences(JsonNode resumeJson) {
        List listExperiences = new List(List.UNORDERED);
        listExperiences.setListSymbol(new Chunk(""));
        ListItem item = new ListItem();
        item.add(new Chunk("Expériences".toUpperCase(), subtitleFontBold));
        item.add(new Chunk("\n ", emptyLineFont));
        JsonNode experiences = resumeJson.get("Experiences");
        if (experiences != null && experiences.isArray()) {
            for (int i = 0; i < experiences.size(); i++) {
                JsonNode experience = experiences.get(i);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Chunk("\n ", emptyLineFont));
                paragraph.add(new Chunk(experience.get("Année").asText() + " ", subtitleFont));
                Image logo = addImage("images" + java.io.File.separator + experience.get("Entreprise").asText() + ".png", experience.get("Size").floatValue());
                paragraph.add(new Chunk(logo, 0, experience.get("Hauteur").floatValue(), false));
                paragraph.add(new Chunk(" (" + experience.get("Domaine").asText() + ")", normalFont));
                item.add(paragraph);
                item.add(new Chunk("\n ", emptyLineFont));
                List listFonctions = new List(List.UNORDERED);
                listFonctions.setListSymbol(new Chunk(""));
                ListItem itemFonctions = new ListItem();
                JsonNode fonctions = experience.get("Fonctions");
                if (fonctions != null && fonctions.isArray()) {
                    for (int j = 0; j < fonctions.size(); j++) {
                        JsonNode fonction = fonctions.get(j);
                        Paragraph paragraphFonction = new Paragraph();
                        paragraphFonction.add(new Chunk(icone, 0, 0, true));
                        paragraphFonction.add(new Chunk(" " + fonction.get("Titre").asText(), normalFontUnderline));
                        if (!fonction.get("Sous titre").asText().isEmpty()) {
                            paragraphFonction.add(new Chunk(" - " + fonction.get("Sous titre").asText(), normalFont));
                        }
                        itemFonctions.add(paragraphFonction);
                        itemFonctions.add(new Chunk("\n", emptyLineFont));

                        List listTaches = new List(List.UNORDERED);
                        listTaches.setListSymbol(new Chunk(""));
                        ListItem itemTaches = new ListItem();
                        JsonNode taches = fonction.get("Tâches");
                        if (taches != null && taches.isArray()) {
                            for (int k = 0; k < taches.size(); k++) {
                                JsonNode tache = taches.get(k);
                                Paragraph paragraphTache = new Paragraph();
                                Font font = normalFont;
                                if (tache.get("EnGras").asBoolean()) {
                                    font = normalFontBold;
                                }
                                paragraphTache.add(new Chunk("    - " + tache.get("Description").asText(), font));
                                itemTaches.add(paragraphTache);
                            }
                        }
                        listTaches.add(itemTaches);
                        itemFonctions.add(listTaches);
                    }
                }
                listFonctions.add(itemFonctions);
                item.add(listFonctions);
            }
        }
        listExperiences.add(item);
        return listExperiences;
    }

    static Paragraph addIconeFirst(String image, JsonNode resumeJson) {
        Paragraph paragraph = new Paragraph();
        Image icone = addImage("images" + File.separator + image + ".png", FONT_SIZE_NORMAL);
        if (icone != null) {
            paragraph.add(new Chunk(icone, 0, 0, true));
        }
        paragraph.add(new Chunk(" " + resumeJson.get(image).asText(), normalFont));
        return paragraph;
    }

    static Image addImage(String imagePath, float size) {
        try {
            Image image = Image.getInstance(imagePath);
            // Ajuster la taille (ex: 100x100 pixels)
            image.scaleAbsolute(size, size);
            image.scaleToFit(image.getScaledWidth(), image.getScaledHeight());

            return image;
        } catch (Exception e) {
            logger.log(Level.ERROR, "L'image " + imagePath + " n'a pas été trouvée, génération du CV sans cette image", e.getMessage());
        }
        return null;
    }
}
