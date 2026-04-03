package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.time.LocalDate;
import java.time.Period;

import static life.light.CreateResumePDF.FONT_SIZE_NORMAL;

public class CreateSkillsPDF {

    private static final Logger logger = System.getLogger(CreateSkillsPDF.class.getName());
    static Font titleFontUp = FontFactory.getFont(com.lowagie.text.FontFactory.TIMES_ROMAN, 25, Font.BOLD);
    static Font titleFont = FontFactory.getFont(com.lowagie.text.FontFactory.TIMES_ROMAN, 20, Font.BOLD);
    static Font subtitleFontBoldUp = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
    static Font subtitleFontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
    static Font subSubtitleFontBoldUp = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
    static Font subSubtitleFontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
    static Font normalFontUp = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL + 2);
    static Font normalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL + 1);
    static Font normalFontBoldUp = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL + 2, Font.BOLD);
    static Font normalFontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, FONT_SIZE_NORMAL - 1, Font.BOLD);
    static Font emptyLineFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 2);

    static void createSkills(JsonNode skillsJson) {
        // Les valeurs sont en "points" (72 points = 1 pouce = 2,54 cm)
        // Ici, on met environ 1 cm de marge partout (28 points).
        float margeGauche = 28f;
        float margeDroite = 28f;
        float margeHaut = 20f;
        float margeBas = 20f;

        Document document = new Document(PageSize.A4, margeGauche, margeDroite, margeHaut, margeBas);


        try {
            String nameFileSkillsPDF = skillsJson.get("Nom").asText() + " " + skillsJson.get("Prénom").asText() + " - Dossier de compétence.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nameFileSkillsPDF));

            // On attache l'image de fond
            BackgroundEvent event = new BackgroundEvent("images/Fond.png");
            writer.setPageEvent(event);

            document.open();

            Paragraph paragraphTitle = new Paragraph();
            paragraphTitle.add(new Chunk("D".toUpperCase(), titleFontUp));
            paragraphTitle.add(new Chunk("ossier de compétence".toUpperCase(), titleFont));
            paragraphTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraphTitle);
            Paragraph paragraphSpaceTitle = new Paragraph();
            paragraphSpaceTitle.add(new Chunk("\n", titleFont));
            paragraphSpaceTitle.setLeading(0, 1.5f);
            paragraphSpaceTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraphSpaceTitle);


            Paragraph paragraphGeneralInformationTitle = new Paragraph();
            paragraphGeneralInformationTitle.add(new Chunk("I".toUpperCase(), subtitleFontBoldUp));
            paragraphGeneralInformationTitle.add(new Chunk("nformations générales".toUpperCase(), subtitleFontBold));
            paragraphGeneralInformationTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphGeneralInformationTitle);
            Paragraph paragraphSpaceSubTitle = new Paragraph();
            paragraphSpaceSubTitle.add(new Chunk("\n", emptyLineFont));
            paragraphSpaceSubTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraphSpaceSubTitle);
            Paragraph paragraphGeneralInformation = new Paragraph();
            paragraphGeneralInformation.add(new Chunk("N", normalFontBoldUp));
            paragraphGeneralInformation.add(new Chunk("om :".toUpperCase(), normalFontBold));
            paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Nom").asText().toUpperCase() + "\n", normalFontUp));
            paragraphGeneralInformation.add(new Chunk("P", normalFontBoldUp));
            paragraphGeneralInformation.add(new Chunk("rénom :".toUpperCase(), normalFontBold));
            paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Prénom").asText() + "\n", normalFont));
            paragraphGeneralInformation.add(new Chunk("A", normalFontBoldUp));
            paragraphGeneralInformation.add(new Chunk("nnée d'expérience :".toUpperCase(), normalFontBold));
            LocalDate dateExp = LocalDate.of(skillsJson.get("Début d'expérience").get("Année").asInt(),
                    skillsJson.get("Début d'expérience").get("Mois").asInt(),
                    skillsJson.get("Début d'expérience").get("Jour").asInt());
            Period difference = Period.between(dateExp, LocalDate.now());
            paragraphGeneralInformation.add(new Chunk(" " + difference.getYears() + " ans\n", normalFont));
            paragraphGeneralInformation.add(new Chunk("P", normalFontBoldUp));
            paragraphGeneralInformation.add(new Chunk("oste :".toUpperCase(), normalFontBold));
            paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Poste").asText(), normalFont));
            LocalDate dateJava = LocalDate.of(skillsJson.get("Début Java").get("Année").asInt(),
                    skillsJson.get("Début d'expérience").get("Mois").asInt(),
                    skillsJson.get("Début d'expérience").get("Jour").asInt());
            difference = Period.between(dateJava, LocalDate.now());
            paragraphGeneralInformation.add(new Chunk(" Java (" + difference.getYears() + " ans)", normalFont));
            LocalDate dateAngular = LocalDate.of(skillsJson.get("Début Angular").get("Année").asInt(),
                    skillsJson.get("Début d'expérience").get("Mois").asInt(),
                    skillsJson.get("Début d'expérience").get("Jour").asInt());
            difference = Period.between(dateAngular, LocalDate.now());
            paragraphGeneralInformation.add(new Chunk(" / Angular (" + difference.getYears() + " ans)\n", normalFont));

            paragraphGeneralInformation.add(new Chunk("D", normalFontBoldUp));
            paragraphGeneralInformation.add(new Chunk("isponibilité :".toUpperCase(), normalFontBold));
            paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Disponibilité").asText() + "\n", normalFontUp));

            paragraphGeneralInformation.setLeading(0, 1.5f);
            paragraphGeneralInformation.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphGeneralInformation);

            document.add(paragraphSpaceSubTitle);

            Paragraph paragraphExpertiseTitle = new Paragraph();
            paragraphExpertiseTitle.add(new Chunk("D".toUpperCase(), subtitleFontBoldUp));
            paragraphExpertiseTitle.add(new Chunk("omaine de compétences".toUpperCase(), subtitleFontBold));
            paragraphExpertiseTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphExpertiseTitle);
            document.add(paragraphSpaceSubTitle);
            Paragraph paragraphExpertiseSubTitle = new Paragraph();
            paragraphExpertiseSubTitle.add(new Chunk("T".toUpperCase(), subSubtitleFontBoldUp));
            paragraphExpertiseSubTitle.add(new Chunk("echniques".toUpperCase(), subSubtitleFontBold));
            paragraphExpertiseSubTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphExpertiseSubTitle);

            Paragraph paragraphExpertise = new Paragraph();
            techniques(paragraphExpertise, skillsJson, "Langages");
            techniques(paragraphExpertise, skillsJson, "Tests");
            techniques(paragraphExpertise, skillsJson, "Frameworks");
            techniques(paragraphExpertise, skillsJson, "Éditeurs de code");
            techniques(paragraphExpertise, skillsJson, "Bases de données");
            techniques(paragraphExpertise, skillsJson, "Intégration continue");
            techniques(paragraphExpertise, skillsJson, "Versioning");
            techniques(paragraphExpertise, skillsJson, "Bug tracker");
            techniques(paragraphExpertise, skillsJson, "Autre outils");
            techniques(paragraphExpertise, skillsJson, "Système d’exploitation");
            techniques(paragraphExpertise, skillsJson, "Architectures");
            techniques(paragraphExpertise, skillsJson, "Méthodologies");

            paragraphExpertise.setLeading(0, 1.5f);
            paragraphExpertise.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphExpertise);

            document.add(paragraphSpaceSubTitle);
            Paragraph paragraphFunctionSubTitle = new Paragraph();
            paragraphFunctionSubTitle.add(new Chunk("P".toUpperCase(), subSubtitleFontBoldUp));
            paragraphFunctionSubTitle.add(new Chunk("ostes".toUpperCase(), subSubtitleFontBold));
            paragraphFunctionSubTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphFunctionSubTitle);

            document.add(paragraphSpaceSubTitle);
            Paragraph paragraphFunctionalSubTitle = new Paragraph();
            paragraphFunctionalSubTitle.add(new Chunk("F".toUpperCase(), subSubtitleFontBoldUp));
            paragraphFunctionalSubTitle.add(new Chunk("onctionnel".toUpperCase(), subSubtitleFontBold));
            paragraphFunctionalSubTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphFunctionalSubTitle);

            document.add(paragraphSpaceSubTitle);

            document.newPage();

            Paragraph paragraphEmployer = new Paragraph();
            paragraphEmployer.add(new Chunk("E".toUpperCase(), subtitleFontBoldUp));
            paragraphEmployer.add(new Chunk("mployeur : ".toUpperCase(), subtitleFontBold));
            String employerName = skillsJson.get("Employeur").get("Nom").asText();
            paragraphEmployer.add(new Chunk(employerName.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
            paragraphEmployer.add(new Chunk(employerName.substring(1).toUpperCase(), subtitleFontBold));
            paragraphEmployer.add(new Chunk(" (" + skillsJson.get("Employeur").get("Domaine fonctionnel").asText().toUpperCase() + ")", subtitleFontBold));
            paragraphEmployer.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphEmployer);


        } catch (Exception e) {
            logger.log(Level.ERROR, "Échec de la génération du dossier de compétence", e.getMessage());
        } finally {
            if (document.isOpen()) {
                // Fermer le document
                document.close();
            }
        }
    }

    private static void techniques(Paragraph paragraphExpertise, JsonNode skillsJson, String technique) {
        paragraphExpertise.add(new Chunk(technique.substring(0, 1).toUpperCase(), normalFontBoldUp));
        paragraphExpertise.add(new Chunk(technique.substring(1).toUpperCase() + " :", normalFontBold));
        JsonNode tech = skillsJson.get("Techniques").get(technique);
        if (tech != null && tech.isArray()) {
            for (int i = 0; i < tech.size(); i++) {
                JsonNode exp = tech.get(i);
                paragraphExpertise.add(new Chunk(" " + exp.asText(), normalFont));
                if (i < tech.size() - 1) {
                    paragraphExpertise.add(new Chunk(", ", normalFont));
                }
            }
        }
        paragraphExpertise.add(new Chunk("\n", normalFont));
    }

}
