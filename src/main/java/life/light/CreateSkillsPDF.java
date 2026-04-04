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

            try {
                BackgroundEvent event = new BackgroundEvent("images/Fond.png");
                writer.setPageEvent(event);
            } catch (Exception e) {
                // Fond optionnel
            }

            document.open();

            addTitle(document);
            addGeneralInformation(document, skillsJson);
            addExpertise(document, skillsJson);
            addEmployer(document, skillsJson);

        } catch (Exception e) {
            logger.log(Level.ERROR, "Échec de la génération du dossier de compétence", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static void addTitle(Document document) {
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
    }

    private static void addGeneralInformation(Document document, JsonNode skillsJson) {
        Paragraph paragraphGeneralInformationTitle = new Paragraph();
        paragraphGeneralInformationTitle.add(new Chunk("I".toUpperCase(), subtitleFontBoldUp));
        paragraphGeneralInformationTitle.add(new Chunk("nformations générales".toUpperCase(), subtitleFontBold));
        paragraphGeneralInformationTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphGeneralInformationTitle);

        addEmptyLine(document);

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

        addTechExperience(paragraphGeneralInformation, skillsJson);

        paragraphGeneralInformation.add(new Chunk("D", normalFontBoldUp));
        paragraphGeneralInformation.add(new Chunk("isponibilité :".toUpperCase(), normalFontBold));
        paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Disponibilité").asText() + "\n", normalFontUp));

        paragraphGeneralInformation.setLeading(0, 1.5f);
        paragraphGeneralInformation.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphGeneralInformation);

        addEmptyLine(document);
    }

    private static void addTechExperience(Paragraph paragraph, JsonNode skillsJson) {
        LocalDate dateJava = LocalDate.of(skillsJson.get("Début Java").get("Année").asInt(),
                skillsJson.get("Début d'expérience").get("Mois").asInt(),
                skillsJson.get("Début d'expérience").get("Jour").asInt());
        Period diffJava = Period.between(dateJava, LocalDate.now());
        paragraph.add(new Chunk(" Java (" + diffJava.getYears() + " ans)", normalFont));

        LocalDate dateAngular = LocalDate.of(skillsJson.get("Début Angular").get("Année").asInt(),
                skillsJson.get("Début d'expérience").get("Mois").asInt(),
                skillsJson.get("Début d'expérience").get("Jour").asInt());
        Period diffAngular = Period.between(dateAngular, LocalDate.now());
        paragraph.add(new Chunk(" / Angular (" + diffAngular.getYears() + " ans)\n", normalFont));
    }

    private static void addExpertise(Document document, JsonNode skillsJson) {
        Paragraph paragraphExpertiseTitle = new Paragraph();
        paragraphExpertiseTitle.add(new Chunk("D".toUpperCase(), subtitleFontBoldUp));
        paragraphExpertiseTitle.add(new Chunk("omaine de compétences".toUpperCase(), subtitleFontBold));
        paragraphExpertiseTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphExpertiseTitle);

        addEmptyLine(document);

        Paragraph paragraphExpertiseSubTitle = new Paragraph();
        paragraphExpertiseSubTitle.add(new Chunk("T".toUpperCase(), subSubtitleFontBoldUp));
        paragraphExpertiseSubTitle.add(new Chunk("echniques".toUpperCase(), subSubtitleFontBold));
        paragraphExpertiseSubTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphExpertiseSubTitle);

        Paragraph paragraphExpertise = new Paragraph();
        String[] techniqueKeys = {
                "Langages", "Tests", "Frameworks", "Éditeurs de code",
                "Bases de données", "Intégration continue", "Versioning",
                "Bug tracker", "Autre outils", "Système d’exploitation",
                "Architectures", "Méthodologies"
        };

        for (String key : techniqueKeys) {
            techniques(paragraphExpertise, skillsJson, key);
        }

        paragraphExpertise.setLeading(0, 1.5f);
        paragraphExpertise.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphExpertise);

        addEmptyLine(document);
        addStaticSections(document);
    }

    private static void addStaticSections(Document document) {
        Paragraph paragraphFunctionSubTitle = new Paragraph();
        paragraphFunctionSubTitle.add(new Chunk("P".toUpperCase(), subSubtitleFontBoldUp));
        paragraphFunctionSubTitle.add(new Chunk("ostes".toUpperCase(), subSubtitleFontBold));
        paragraphFunctionSubTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphFunctionSubTitle);

        addEmptyLine(document);

        Paragraph paragraphFunctionalSubTitle = new Paragraph();
        paragraphFunctionalSubTitle.add(new Chunk("F".toUpperCase(), subSubtitleFontBoldUp));
        paragraphFunctionalSubTitle.add(new Chunk("onctionnel".toUpperCase(), subSubtitleFontBold));
        paragraphFunctionalSubTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphFunctionalSubTitle);

        addEmptyLine(document);
    }

    private static void addEmployer(Document document, JsonNode skillsJson) {
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

        JsonNode clients = skillsJson.get("Employeur").get("Clients");
        if (clients != null && clients.isArray()) {
            for (JsonNode client : clients) {
                addClient(document, client);
            }
        }
    }

    private static void addClient(Document document, JsonNode client) {
        addEmptyLine(document);
        Paragraph paragraphClient = new Paragraph();
        paragraphClient.add(new Chunk("C".toUpperCase(), subtitleFontBoldUp));
        paragraphClient.add(new Chunk("lient : ".toUpperCase(), subtitleFontBold));
        String clientName = client.get("Nom").asText();
        paragraphClient.add(new Chunk(clientName.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraphClient.add(new Chunk(clientName.substring(1).toUpperCase(), subtitleFontBold));
        paragraphClient.add(new Chunk(" (" + client.get("Domaine fonctionnel").asText().toUpperCase() + ")", subtitleFontBold));
        document.add(paragraphClient);

        Paragraph paragraphClientPoste = new Paragraph();
        paragraphClientPoste.add(new Chunk("P", normalFontBoldUp));
        paragraphClientPoste.add(new Chunk("oste : ".toUpperCase(), normalFontBold));
        paragraphClientPoste.add(new Chunk(client.get("Poste").asText() + "\n", normalFont));
        paragraphClientPoste.add(new Chunk("P", normalFontBoldUp));
        paragraphClientPoste.add(new Chunk("ériode : ".toUpperCase(), normalFontBold));
        JsonNode debut = client.get("Période").get("Début");
        JsonNode fin = client.get("Période").get("Fin");
        paragraphClientPoste.add(new Chunk(debut.get("Mois").asText() + "/" + debut.get("Année").asText() + " à " + fin.get("Mois").asText() + "/" + fin.get("Année").asText() + "\n", normalFont));
        document.add(paragraphClientPoste);

        addEmptyLine(document);
        addClientDetails(document, client);
    }

    private static void addClientDetails(Document document, JsonNode client) {
        addSectionTitle(document, "C", "ontexte : ");
        Paragraph paragraphContexte = new Paragraph();
        paragraphContexte.add(new Chunk(client.get("Contexte").asText(), normalFont));
        paragraphContexte.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(paragraphContexte);

        addEmptyLine(document);
        addSectionTitle(document, "R", "éalisation : ");
        Paragraph paragraphRealisation = new Paragraph();
        paragraphRealisation.add(new Chunk(client.get("Réalisation").asText(), normalFont));
        paragraphRealisation.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(paragraphRealisation);

        addEmptyLine(document);
        addSectionTitle(document, "E", "nvironnement technique : ");
        Paragraph paragraphExpertiseClient = new Paragraph();
        String[] techniqueKeys = {
                "Langages", "Tests", "Frameworks", "Éditeurs de code",
                "Bases de données", "Intégration continue", "Versioning",
                "Bug tracker", "Autre outils", "Système d’exploitation",
                "Architectures", "Méthodologies"
        };
        for (String key : techniqueKeys) {
            techniques(paragraphExpertiseClient, client, key);
        }
        document.add(paragraphExpertiseClient);
    }

    private static void addSectionTitle(Document document, String firstLetter, String rest) {
        Paragraph title = new Paragraph();
        title.add(new Chunk(firstLetter, normalFontBoldUp));
        title.add(new Chunk(rest.toUpperCase(), normalFontBold));
        document.add(title);
    }

    private static void addEmptyLine(Document document) {
        Paragraph p = new Paragraph();
        p.add(new Chunk("\n", emptyLineFont));
        document.add(p);
    }

    private static void techniques(Paragraph paragraphExpertise, JsonNode skillsJson, String technique) {
        JsonNode techniquesNode = skillsJson.get("Techniques");
        if (techniquesNode == null || techniquesNode.isMissingNode() || techniquesNode.isNull()) {
            return;
        }
        JsonNode techArray = techniquesNode.get(technique);
        if (techArray == null || techArray.isMissingNode() || !techArray.isArray() || techArray.isEmpty()) {
            return;
        }

        paragraphExpertise.add(new Chunk(technique.substring(0, 1).toUpperCase(), normalFontBoldUp));
        paragraphExpertise.add(new Chunk(technique.substring(1).toUpperCase() + " :", normalFontBold));

        for (int i = 0; i < techArray.size(); i++) {
            paragraphExpertise.add(new Chunk(" " + techArray.get(i).asText(), normalFont));
            if (i < techArray.size() - 1) {
                paragraphExpertise.add(new Chunk(",", normalFont));
            }
        }
        paragraphExpertise.add(new Chunk("\n", normalFont));
    }

}
