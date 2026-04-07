package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.time.LocalDate;
import java.time.Period;

import static life.light.Tools.AVAILABILITY;
import static life.light.Tools.CONTEXT;
import static life.light.Tools.CUSTOMER;
import static life.light.Tools.DAY;
import static life.light.Tools.EMPLOYER;
import static life.light.Tools.FIRST_NAME;
import static life.light.Tools.FUNCTIONAL_DOMAIN;
import static life.light.Tools.FUNCTIONAL_DOMAINS;
import static life.light.Tools.MONTH;
import static life.light.Tools.NAME;
import static life.light.Tools.OCCUPIED_POSITIONS;
import static life.light.Tools.PERIOD;
import static life.light.Tools.POSITION_HELD;
import static life.light.Tools.REALISATION;
import static life.light.Tools.TECHNICAL_ENVIRONMENT;
import static life.light.Tools.TECHNICAL_KEYS;
import static life.light.Tools.TECHNIQUES;
import static life.light.Tools.YEAR;
import static life.light.Tools.YEAR_OF_EXPERIENCE;
import static life.light.Tools.getNameOfTheMonth;

public class CreateSkillsPDF {

    private static final Logger logger = System.getLogger( CreateSkillsPDF.class.getName() );

    public static final PDFont FONT_PLAIN = new PDType1Font( Standard14Fonts.FontName.TIMES_ROMAN );
    public static final PDFont FONT_BOLD = new PDType1Font( Standard14Fonts.FontName.TIMES_BOLD );

    static String createSkills(JsonNode skillsJson) {
        String nameFileSkillsPDF = skillsJson.get( NAME ).asText() + " " + skillsJson.get( FIRST_NAME ).asText() + " - Dossier de compétence.pdf";
        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, "images/Fond.png" );

            addTitle( tools );
            addGeneralInformation( tools, skillsJson );
            // Domaine de compétences
            addExpertise( tools, skillsJson );
            tools.setCursorY( tools.getCursorY() - 10 );
            addEmployer( tools, skillsJson );

            tools.close();
            try (FileOutputStream fos = new FileOutputStream( nameFileSkillsPDF )) {
                document.save( fos );
            }
        } catch (Exception e) {
            logger.log( Logger.Level.ERROR, "Échec de la génération du dossier de compétence", e );
        }
        return nameFileSkillsPDF;
    }

    private static void addTitle(PDFBoxTools tools) throws IOException {
        tools.addCenteredText( "Dossier de compétence", 25, FONT_BOLD );
        tools.setCursorY( tools.getCursorY() - 20 );
    }

    private static void addSubtitle(PDFBoxTools tools, String title) throws IOException {
        if (title != null) {
            tools.addText( title.toUpperCase(), 16, FONT_BOLD );
            tools.setCursorY( tools.getCursorY() - 5 );
        }
    }

    private static void addLabelValue(PDFBoxTools tools, String label, String value, PDFont valueFont, float fontSize) throws IOException {
        if (label != null && value != null) {
            tools.addText( label.toUpperCase() + " : " + value, fontSize, valueFont );
        }
    }

    private static void addGeneralInformation(PDFBoxTools tools, JsonNode skillsJson) throws IOException {
        addSubtitle( tools, "Informations générales" );

        addLabelValue( tools, NAME, skillsJson.has( NAME ) ? skillsJson.get( NAME ).asText().toUpperCase() : "", FONT_BOLD, 12 );
        addLabelValue( tools, FIRST_NAME, skillsJson.has( FIRST_NAME ) ? skillsJson.get( FIRST_NAME ).asText() : "", FONT_BOLD, 12 );

        String startExp = "Début d'expérience";
        if (skillsJson.has( startExp )) {
            JsonNode startExpNode = skillsJson.get( startExp );
            LocalDate dateExp = LocalDate.of( startExpNode.get( YEAR ).asInt(), startExpNode.get( MONTH ).asInt(), startExpNode.get( DAY ).asInt() );
            Period difference = Period.between( dateExp, LocalDate.now() );
            addLabelValue( tools, YEAR_OF_EXPERIENCE, difference.getYears() + " ans", FONT_BOLD, 12 );
        }

        addLabelValue( tools, POSITION_HELD, skillsJson.has( POSITION_HELD ) ? skillsJson.get( POSITION_HELD ).asText() : "", FONT_BOLD, 12 );

        addTechExperience( tools, skillsJson );

        addLabelValue( tools, AVAILABILITY, skillsJson.has( AVAILABILITY ) ? skillsJson.get( AVAILABILITY ).asText() : "", FONT_BOLD, 12 );

        tools.setCursorY( tools.getCursorY() - 10 );
    }

    private static void addTechExperience(PDFBoxTools tools, JsonNode skillsJson) throws IOException {
        if (skillsJson.has( "Début Java" ) && skillsJson.has( "Début d'expérience" )) {
            JsonNode startJava = skillsJson.get( "Début Java" );
            JsonNode startExp = skillsJson.get( "Début d'expérience" );
            LocalDate dateJava = LocalDate.of( startJava.get( "Année" ).asInt(), startExp.get( "Mois" ).asInt(), startExp.get( "Jour" ).asInt() );
            Period diffJava = Period.between( dateJava, LocalDate.now() );

            if (skillsJson.has( "Début Angular" )) {
                JsonNode startAngular = skillsJson.get( "Début Angular" );
                LocalDate dateAngular = LocalDate.of( startAngular.get( "Année" ).asInt(), startExp.get( "Mois" ).asInt(), startExp.get( "Jour" ).asInt() );
                Period diffAngular = Period.between( dateAngular, LocalDate.now() );
                tools.addText( "Java (" + diffJava.getYears() + " ans) / Angular (" + diffAngular.getYears() + " ans)", 12, FONT_PLAIN );
            } else {
                tools.addText( "Java (" + diffJava.getYears() + " ans)", 12, FONT_PLAIN );
            }
        }
    }

    private static void addExpertise(PDFBoxTools tools, JsonNode skillsJson) throws IOException {
        addSubtitle( tools, "Domaine de compétences" );

        tools.addText( TECHNIQUES.toUpperCase() + " :", 12, FONT_BOLD );

        for (String key : TECHNICAL_KEYS) {
            techniques( tools, skillsJson, key );
        }

        tools.setCursorY( tools.getCursorY() - 10 );

        // Poste
        tools.addText( OCCUPIED_POSITIONS.toUpperCase(), 12, FONT_BOLD );
        JsonNode occupiedPositionsNode = skillsJson.get( OCCUPIED_POSITIONS );
        addList( tools, occupiedPositionsNode );

        // Domaines fonctionnels
        tools.addText( FUNCTIONAL_DOMAINS.toUpperCase() + " :", 12, FONT_BOLD );
        JsonNode functionalDomainNode = skillsJson.get( FUNCTIONAL_DOMAINS );
        addList( tools, functionalDomainNode );
    }

    private static void addList(PDFBoxTools tools, JsonNode listNode) throws IOException {
        if (listNode != null && listNode.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listNode.size(); i++) {
                String item = listNode.get( i ).asText();
                if (!item.isEmpty()) {
                    sb.append( item );
                    if (i < listNode.size() - 1) {
                        sb.append( ", " );
                    }
                }
            }
            tools.addText( sb.toString(), 12, FONT_PLAIN );
        }
        tools.setCursorY( tools.getCursorY() - 5 );
    }

    private static void addEmployer(PDFBoxTools tools, JsonNode skillsJson) throws IOException {
        JsonNode employerNode = skillsJson.get( EMPLOYER );
        if (employerNode != null && employerNode.isArray()) {
            tools.addNewPage();
            for (JsonNode employer : employerNode) {
                final String employerHeader = getEmployerHeaderText( employer );
                tools.setOnNewPage( () -> {
                    try {
                        tools.addText( employerHeader + " (suite)", 14, FONT_BOLD );
                        tools.setCursorY( tools.getCursorY() - 5 );
                    } catch (IOException e) {
                        logger.log( Logger.Level.ERROR, "Erreur lors de l'ajout du rappel de l'employeur", e );
                    }
                } );

                float employerHeaderHeight = calculateEmployerHeaderHeight( tools, employer );
                if (tools.getCursorY() - employerHeaderHeight < tools.getMarginBottom()) {
                    tools.addNewPage();
                }
                addEmployerInfo( tools, employer );
                JsonNode customers = employer.get( "Clients" );
                if (customers != null && customers.isArray()) {
                    for (JsonNode customer : customers) {
                        addCustomer( tools, customer );
                    }
                } else {
                    float missionHeight = calculateMissionHeight( tools, employer );
                    if (tools.getCursorY() - missionHeight < tools.getMarginBottom()) {
                        tools.addNewPage();
                    }
                    addMission( tools, employer );
                }
            }
            tools.setOnNewPage( null );
        }
    }

    private static void addEmployerInfo(PDFBoxTools tools, JsonNode employer) throws IOException {
        tools.addText( getEmployerHeaderText( employer ), 14, FONT_BOLD );
    }

    private static String getEmployerHeaderText(JsonNode employer) {
        String employerName = employer.has( NAME ) ? employer.get( NAME ).asText() : "";
        String functionalDomain = employer.has( FUNCTIONAL_DOMAIN ) ? employer.get( FUNCTIONAL_DOMAIN ).asText() : "";
        String text = EMPLOYER.toUpperCase() + " : " + employerName.toUpperCase();
        if (functionalDomain != null && !functionalDomain.isEmpty()) {
            text += " (" + functionalDomain.toUpperCase() + ")";
        }
        return text;
    }

    private static float calculateEmployerHeaderHeight(PDFBoxTools tools, JsonNode employer) throws IOException {
        return tools.calculateTextHeight( getEmployerHeaderText( employer ), 14, FONT_BOLD );
    }

    private static void addCustomer(PDFBoxTools tools, JsonNode customer) throws IOException {
        float estimatedHeight = 0;
        if (customer.has( NAME ) && !customer.get( NAME ).asText().isEmpty()) {
            estimatedHeight += tools.calculateTextHeight( CUSTOMER.toUpperCase() + " : " + customer.get( NAME ).asText().toUpperCase(), 12, FONT_BOLD );
        }
        estimatedHeight += calculateMissionHeight( tools, customer );

        if (tools.getCursorY() - estimatedHeight < tools.getMarginBottom()) {
            tools.addNewPage();
        }

        if (customer.has( NAME ) && !customer.get( NAME ).asText().isEmpty()) {
            tools.addText( CUSTOMER.toUpperCase() + " : " + customer.get( NAME ).asText().toUpperCase(), 12, FONT_BOLD );
        }
        addMission( tools, customer );
    }

    private static void addMission(PDFBoxTools tools, JsonNode node) throws IOException {
        addPeriod( tools, node );
        addLabelValue( tools, POSITION_HELD, node.has( POSITION_HELD ) ? node.get( POSITION_HELD ).asText() : "", FONT_BOLD, 12 );
        addLabelValue( tools, CONTEXT, node.has( CONTEXT ) ? node.get( CONTEXT ).asText() : "", FONT_BOLD, 12 );
        addLabelValue( tools, REALISATION, node.has( REALISATION ) ? node.get( REALISATION ).asText() : "", FONT_BOLD, 12 );
        addLabelValue( tools, TECHNICAL_ENVIRONMENT, node.has( TECHNICAL_ENVIRONMENT ) ? node.get( TECHNICAL_ENVIRONMENT ).asText() : "", FONT_BOLD, 12 );
        tools.setCursorY( tools.getCursorY() - 10 );
    }

    private static float calculateMissionHeight(PDFBoxTools tools, JsonNode node) throws IOException {
        float height = 0;
        // Période
        if (node.has( PERIOD )) {
            JsonNode periodNode = node.get( PERIOD );
            if (periodNode.has( "Début" ) && periodNode.has( "Fin" )) {
                JsonNode debut = periodNode.get( "Début" );
                JsonNode fin = periodNode.get( "Fin" );
                LocalDate dateDebut = LocalDate.of( debut.get( "Année" ).asInt(), debut.get( "Mois" ).asInt(), debut.get( "Jour" ).asInt() );
                LocalDate dateFin = LocalDate.of( fin.get( "Année" ).asInt(), fin.get( "Mois" ).asInt(), fin.get( "Jour" ).asInt() );
                Period differencePeriod = Period.between( dateDebut, dateFin );
                String difference = getDifference( differencePeriod );
                String periodText = PERIOD.toUpperCase() + " : de " + getNameOfTheMonth( debut ) + " " + debut.get( "Année" ).asText()
                        + " à " + getNameOfTheMonth( fin ) + " " + fin.get( "Année" ).asText()
                        + " (" + difference + ")";
                height += tools.calculateTextHeight( periodText, 12, FONT_PLAIN );
            }
        }
        // Labels
        height += calculateLabelValueHeight( tools, POSITION_HELD, node.has( POSITION_HELD ) ? node.get( POSITION_HELD ).asText() : "", FONT_BOLD, 12 );
        height += calculateLabelValueHeight( tools, CONTEXT, node.has( CONTEXT ) ? node.get( CONTEXT ).asText() : "", FONT_BOLD, 12 );
        height += calculateLabelValueHeight( tools, REALISATION, node.has( REALISATION ) ? node.get( REALISATION ).asText() : "", FONT_BOLD, 12 );
        height += calculateLabelValueHeight( tools, TECHNICAL_ENVIRONMENT, node.has( TECHNICAL_ENVIRONMENT ) ? node.get( TECHNICAL_ENVIRONMENT ).asText() : "", FONT_BOLD, 12 );
        height += 10; // Espacement final
        return height;
    }

    private static float calculateLabelValueHeight(PDFBoxTools tools, String label, String value, PDFont valueFont, float fontSize) throws IOException {
        if (label != null && value != null) {
            return tools.calculateTextHeight( label.toUpperCase() + " : " + value, fontSize, valueFont );
        }
        return 0;
    }

    private static void addPeriod(PDFBoxTools tools, JsonNode node) throws IOException {
        if (node.has( PERIOD )) {
            JsonNode periodNode = node.get( PERIOD );
            if (periodNode.has( "Début" ) && periodNode.has( "Fin" )) {
                JsonNode debut = periodNode.get( "Début" );
                JsonNode fin = periodNode.get( "Fin" );

                LocalDate dateDebut = LocalDate.of( debut.get( "Année" ).asInt(), debut.get( "Mois" ).asInt(), debut.get( "Jour" ).asInt() );
                LocalDate dateFin = LocalDate.of( fin.get( "Année" ).asInt(), fin.get( "Mois" ).asInt(), fin.get( "Jour" ).asInt() );
                Period differencePeriod = Period.between( dateDebut, dateFin );
                String difference = getDifference( differencePeriod );

                String periodText = PERIOD.toUpperCase() + " : de " + getNameOfTheMonth( debut ) + " " + debut.get( "Année" ).asText()
                        + " à " + getNameOfTheMonth( fin ) + " " + fin.get( "Année" ).asText()
                        + " (" + difference + ")";
                tools.addText( periodText, 12, FONT_PLAIN );
            }
        }
    }

    private static String getDifference(Period differencePeriod) {
        int differenceYear = differencePeriod.getYears();
        int differenceMonth = differencePeriod.getMonths();
        String difference = "";
        if (differenceYear > 0) {
            difference = differenceYear + " an";
            if (differenceYear > 1) {
                difference = differenceYear + "s";
            }
            if (differenceMonth > 0) {
                difference = difference + " et ";
            }
        }
        difference = difference + (differenceMonth + 1) + " mois";
        return difference;
    }

    private static void techniques(PDFBoxTools tools, JsonNode skillsJson, String technique) throws IOException {
        JsonNode techniquesNode = skillsJson.get( TECHNIQUES );
        if (techniquesNode.get( technique ) != null && !techniquesNode.get( technique ).isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append( technique ).append( " : " );
            JsonNode node = techniquesNode.get( technique );
            for (int i = 0; i < node.size(); i++) {
                sb.append( node.get( i ).asText() );
                if (i < node.size() - 1) {
                    sb.append( ", " );
                }
            }
            tools.addText( sb.toString(), 12, FONT_PLAIN );
        }
    }
}
