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

    public static final String CUSTOMER = "Client";
    public static final String POSITION_HELD = "Poste";
    public static final String PERIOD = "Période";
    public static final String CONTEXT = "Contexte";
    public static final String REALISATION = "Réalisation";
    public static final String TECHNICAL_ENVIRONMENT = "Environnement technique";
    private static final Logger logger = System.getLogger(CreateSkillsPDF.class.getName());
    public static final String DOMAINE_FONCTIONNEL = "Domaine fonctionnel";
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
                logger.log(Level.ERROR, "Échec de la création de l'image de fond du dossier de compétence", e);
            }

            document.open();

            addTitle(document, "Dossier de compétence");
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

    private static void addTitle(Document document, String title) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(title.substring(0, 1).toUpperCase(), titleFontUp));
        paragraph.add(new Chunk(title.substring(1).toUpperCase() + " :", titleFont));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        Paragraph paragraphSpace = new Paragraph();
        paragraphSpace.add(new Chunk("\n", titleFont));
        paragraphSpace.setLeading(0, 1.5f);
        paragraphSpace.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphSpace);
    }

    private static void addGeneralInformation(Document document, JsonNode skillsJson) {
        addSubtitle(document, "Informations générales");

        Paragraph paragraphGeneralInformation = new Paragraph();
        addLabel(paragraphGeneralInformation, "Nom");
        paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Nom").asText().toUpperCase() + "\n", normalFontUp));

        addLabel(paragraphGeneralInformation, "Prénom");
        paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Prénom").asText() + "\n", normalFont));

        paragraphGeneralInformation.add(new Chunk("A", normalFontBoldUp));
        paragraphGeneralInformation.add(new Chunk("nnée d'expérience :".toUpperCase(), normalFontBold));
        LocalDate dateExp = LocalDate.of(skillsJson.get("Début d'expérience").get("Année").asInt(),
                skillsJson.get("Début d'expérience").get("Mois").asInt(),
                skillsJson.get("Début d'expérience").get("Jour").asInt());
        Period difference = Period.between(dateExp, LocalDate.now());
        paragraphGeneralInformation.add(new Chunk(" " + difference.getYears() + " ans\n", normalFont));

        addLabel(paragraphGeneralInformation, "Poste");
        paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Poste").asText(), normalFont));

        addTechExperience(paragraphGeneralInformation, skillsJson);

        addLabel(paragraphGeneralInformation, "Disponibilité");
        paragraphGeneralInformation.add(new Chunk(" " + skillsJson.get("Disponibilité").asText() + "\n", normalFontUp));

        paragraphGeneralInformation.setLeading(0, 1.5f);
        paragraphGeneralInformation.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphGeneralInformation);

        addEmptyLine(document);
    }

    private static void addLabel(Paragraph paragraph, String label) {
        paragraph.add(new Chunk(label.substring(0, 1).toUpperCase(), normalFontBoldUp));
        paragraph.add(new Chunk(label.substring(1).toUpperCase() + " :", normalFontBold));
    }

    private static void addSubtitle(Document document, String title) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(title.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraph.add(new Chunk(title.substring(1).toUpperCase() + " :", subtitleFontBold));
        paragraph.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph);
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
        addSubtitle(document, "Domaine de compétences");
        addSubSubtitle(document, "Techniques");
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
        addStaticSections(document, skillsJson);
    }

    private static void addSubSubtitle(Document document, String subtitle) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(subtitle.substring(0, 1).toUpperCase(), subSubtitleFontBoldUp));
        paragraph.add(new Chunk(subtitle.substring(1).toUpperCase() + " :", subSubtitleFontBold));
        paragraph.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph);
    }

    private static void addStaticSections(Document document, JsonNode skillsJson) {
        addSubSubtitle(document, "Postes");
        Paragraph paragraphPostes = new Paragraph();
        JsonNode postesNode = skillsJson.get("Postes");
        if (postesNode != null && postesNode.isArray()) {
            for (int i = 0; i < postesNode.size(); i++) {
                String poste = postesNode.get(i).asText();
                if (!poste.isEmpty()) {
                    paragraphPostes.add(new Chunk(poste, normalFont));
                    if (i < postesNode.size() - 1) {
                        paragraphPostes.add(new Chunk(", ", normalFont));
                    }
                }
            }
        }
        document.add(paragraphPostes);
        addEmptyLine(document);

        addSubSubtitle(document, "Fonctionnel");
        Paragraph paragraphFonctionnel = new Paragraph();
        JsonNode fonctionnelNode = skillsJson.get("Fonctionnel");
        if (fonctionnelNode != null && fonctionnelNode.isArray()) {
            for (int i = 0; i < fonctionnelNode.size(); i++) {
                String fonctionnel = fonctionnelNode.get(i).asText();
                if (!fonctionnel.isEmpty()) {
                    paragraphFonctionnel.add(new Chunk(fonctionnel, normalFont));
                    if (i < fonctionnelNode.size() - 1) {
                        paragraphFonctionnel.add(new Chunk(", ", normalFont));
                    }
                }
            }
        }
        document.add(paragraphFonctionnel);
        addEmptyLine(document);
    }

    private static void addEmployer(Document document, JsonNode skillsJson) {
        document.newPage();

        Paragraph paragraphEmployer = new Paragraph();
        String employer = "Employeur";
        paragraphEmployer.add(new Chunk(employer.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraphEmployer.add(new Chunk(employer.substring(1).toUpperCase() + " :", subtitleFontBold));
        String employerName = skillsJson.get(employer).get("Nom").asText();
        paragraphEmployer.add(new Chunk(employerName.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraphEmployer.add(new Chunk(employerName.substring(1).toUpperCase(), subtitleFontBold));
        paragraphEmployer.add(new Chunk(" (" + skillsJson.get(employer).get(DOMAINE_FONCTIONNEL).asText().toUpperCase() + ")", subtitleFontBold));
        paragraphEmployer.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphEmployer);

        JsonNode customers = skillsJson.get(employer).get("Clients");
        if (customers != null && customers.isArray()) {
            for (JsonNode customer : customers) {
                addCustomer(document, customer);
            }
        }
    }

    private static void addCustomer(Document document, JsonNode customer) {
        addEmptyLine(document);
        addCutomerName(document, customer);

        Paragraph paragraphClientPoste = new Paragraph();
        paragraphClientPoste.add(new Chunk(POSITION_HELD.substring(0, 1).toUpperCase(), normalFontBoldUp));
        paragraphClientPoste.add(new Chunk((POSITION_HELD + " : ").substring(1).toUpperCase(), normalFontBold));
        paragraphClientPoste.add(new Chunk(customer.get(POSITION_HELD).asText() + "\n", normalFont));
        paragraphClientPoste.add(new Chunk(PERIOD.substring(0, 1).toUpperCase(), normalFontBoldUp));
        paragraphClientPoste.add(new Chunk(PERIOD.substring(1).toUpperCase(), normalFontBold));
        JsonNode debut = customer.get(PERIOD).get("Début");
        JsonNode fin = customer.get(PERIOD).get("Fin");

        LocalDate dateDebut = LocalDate.of(debut.get("Année").asInt(),
                debut.get("Mois").asInt(),
                debut.get("Jour").asInt());
        LocalDate dateFin = LocalDate.of(fin.get("Année").asInt(),
                fin.get("Mois").asInt(),
                fin.get("Jour").asInt());
        Period difference = Period.between(dateDebut, dateFin);

        paragraphClientPoste.add(new Chunk(" de " + getNameOfTheMonth(debut) + " " + debut.get("Année").asText()
                + " à " + getNameOfTheMonth(fin) + " " + fin.get("Année").asText()
                + " (" + (difference.getMonths() + 1) + " mois)\n", normalFont));
        document.add(paragraphClientPoste);

        addEmptyLine(document);
        addClientDetails(document, customer);

    }

    @javax.annotation.Nonnull
    private static String getNameOfTheMonth(com.fasterxml.jackson.databind.JsonNode debut) {
        int moisDebut = Integer.parseInt(debut.get("Mois").asText());
        java.time.Month mois = java.time.Month.of(moisDebut);
        return mois.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRANCE);
    }

    private static void addCutomerName(com.lowagie.text.Document document, com.fasterxml.jackson.databind.JsonNode customer) {
        com.lowagie.text.Paragraph paragraph = new com.lowagie.text.Paragraph();
        paragraph.add(new com.lowagie.text.Chunk(CUSTOMER.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraph.add(new com.lowagie.text.Chunk((CUSTOMER + " : ").substring(1).toUpperCase(), subtitleFontBold));
        String customerName = customer.get("Nom").asText();
        paragraph.add(new com.lowagie.text.Chunk(customerName.substring(0, 1).toUpperCase(), subtitleFontBoldUp));
        paragraph.add(new com.lowagie.text.Chunk(customerName.substring(1).toUpperCase(), subtitleFontBold));
        paragraph.add(new com.lowagie.text.Chunk(" (" + customer.get(DOMAINE_FONCTIONNEL).asText().toUpperCase() + ")", subtitleFontBold));
        document.add(paragraph);
    }

    private static void addClientDetails(Document document, JsonNode client) {

        Paragraph paragraph = new Paragraph();
        addLabel(paragraph, CONTEXT);
        paragraph.add(new Chunk("\n", normalFont));
        paragraph.add(new Chunk(client.get(CONTEXT).asText(), normalFont));
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(paragraph);

        addEmptyLine(document);

        Paragraph paragraphRealisation = new Paragraph();
        addLabel(paragraphRealisation, REALISATION);
        paragraphRealisation.add(new Chunk("\n", normalFont));
        paragraphRealisation.add(new Chunk(client.get(REALISATION).asText(), normalFont));
        paragraphRealisation.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(paragraphRealisation);

        addEmptyLine(document);
        Paragraph paragraphExpertiseClient = new Paragraph();
        addLabel(paragraphExpertiseClient, TECHNICAL_ENVIRONMENT);
        paragraphExpertiseClient.add(new Chunk("\n", normalFont));
        String[] techniqueKeys = {
                "Langages", "Tests", "Frameworks", "Éditeurs de code",
                "Bases de données", "Intégration continue", "Versioning",
                "Bug tracker", "Autre outils", "Système d’exploitation",
                "Architectures", "Méthodologies"
        };
        boolean first = true;
        for (String key : techniqueKeys) {
            JsonNode techniquesNode = client.get("Techniques");
            if (techniquesNode != null && !techniquesNode.isMissingNode() && !techniquesNode.isNull()) {
                JsonNode techArray = techniquesNode.get(key);
                if (techArray != null && !techArray.isMissingNode() && techArray.isArray() && !techArray.isEmpty()) {
                    if (!first) {
                        paragraphExpertiseClient.add(new Chunk(", ", normalFont));
                    }
                    addAllTechniques(paragraphExpertiseClient, client, key);
                    first = false;
                }
            }
        }
        document.add(paragraphExpertiseClient);
    }

    private static void addAllTechniques(Paragraph paragraphExpertise, JsonNode skillsJson, String technique) {
        JsonNode techniquesNode = skillsJson.get("Techniques");
        if (techniquesNode == null || techniquesNode.isMissingNode() || techniquesNode.isNull()) {
            return;
        }
        JsonNode techArray = techniquesNode.get(technique);
        if (techArray == null || techArray.isMissingNode() || !techArray.isArray() || techArray.isEmpty()) {
            return;
        }
        for (int i = 0; i < techArray.size(); i++) {
            paragraphExpertise.add(new Chunk(techArray.get(i).asText(), normalFont));
            if (i < techArray.size() - 1) {
                paragraphExpertise.add(new Chunk(", ", normalFont));
            }
        }
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
