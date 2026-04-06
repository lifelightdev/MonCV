package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;

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
import static life.light.Tools.addEmptyLine;
import static life.light.Tools.addLabel;
import static life.light.Tools.addList;
import static life.light.Tools.addParagraphLabel;
import static life.light.Tools.addSubtitle;
import static life.light.Tools.addTitle;
import static life.light.Tools.getNameOfTheMonth;
import static life.light.Tools.normalFont;
import static life.light.Tools.normalFontBold;
import static life.light.Tools.normalFontBoldUp;
import static life.light.Tools.normalFontUp;
import static life.light.Tools.subSubtitleFontBold;
import static life.light.Tools.subSubtitleFontBoldUp;
import static life.light.Tools.subtitleFontBold;
import static life.light.Tools.subtitleFontBoldUp;

public class CreateSkillsPDF {

    private static final Logger logger = System.getLogger( CreateSkillsPDF.class.getName() );

    static void createSkills(JsonNode skillsJson) {

        String nameFileSkillsPDF = skillsJson.get( NAME ).asText() + " " + skillsJson.get( FIRST_NAME ).asText() + " - Dossier de compétence.pdf";
        Document document = Tools.createDocument( nameFileSkillsPDF );
        try {
            document.open();
            addTitle( document );
            addGeneralInformation( document, skillsJson );
            // Domaine de compétences
            addExpertise( document, skillsJson );
            addEmptyLine( document );
            addEmployer( document, skillsJson );
        } catch (Exception e) {
            logger.log( Logger.Level.ERROR, "Échec de la génération du dossier de compétence", e );
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static void addGeneralInformation(Document document, JsonNode skillsJson) {
        addSubtitle( document, "Informations générales" );

        Paragraph paragraph = new Paragraph();
        addLabel( paragraph, NAME );
        paragraph.add( new Chunk( " " + skillsJson.get( NAME ).asText().toUpperCase() + "\n", normalFontUp ) );

        addLabel( paragraph, FIRST_NAME );
        paragraph.add( new Chunk( " " + skillsJson.get( FIRST_NAME ).asText() + "\n", normalFont ) );

        addLabel( paragraph, YEAR_OF_EXPERIENCE );
        String startExp = "Début d'expérience";
        LocalDate dateExp = LocalDate.of( skillsJson.get( startExp ).get( YEAR ).asInt(),
                skillsJson.get( startExp ).get( MONTH ).asInt(),
                skillsJson.get( startExp ).get( DAY ).asInt() );
        Period difference = Period.between( dateExp, LocalDate.now() );
        paragraph.add( new Chunk( " " + difference.getYears() + " ans\n", normalFont ) );

        addLabel( paragraph, POSITION_HELD );
        paragraph.add( new Chunk( " " + skillsJson.get( POSITION_HELD ).asText(), normalFont ) );

        addTechExperience( paragraph, skillsJson );

        addLabel( paragraph, AVAILABILITY );
        paragraph.add( new Chunk( " " + skillsJson.get( AVAILABILITY ).asText() + "\n", normalFontUp ) );

        paragraph.setLeading( 0, 1.5f );
        paragraph.setAlignment( Element.ALIGN_LEFT );
        document.add( paragraph );

        addEmptyLine( document );
    }

    private static void addTechExperience(Paragraph paragraph, JsonNode skillsJson) {
        LocalDate dateJava = LocalDate.of( skillsJson.get( "Début Java" ).get( "Année" ).asInt(), skillsJson.get( "Début d'expérience" ).get( "Mois" ).asInt(), skillsJson.get( "Début d'expérience" ).get( "Jour" ).asInt() );
        Period diffJava = Period.between( dateJava, LocalDate.now() );
        paragraph.add( new Chunk( " Java (" + diffJava.getYears() + " ans)", normalFont ) );

        LocalDate dateAngular = LocalDate.of( skillsJson.get( "Début Angular" ).get( "Année" ).asInt(), skillsJson.get( "Début d'expérience" ).get( "Mois" ).asInt(), skillsJson.get( "Début d'expérience" ).get( "Jour" ).asInt() );
        Period diffAngular = Period.between( dateAngular, LocalDate.now() );
        paragraph.add( new Chunk( " / Angular (" + diffAngular.getYears() + " ans)\n", normalFont ) );
    }

    private static void addExpertise(Document document, JsonNode skillsJson) {
        addSubtitle( document, "Domaine de compétences" );
        Paragraph paragraphTechniques = new Paragraph();
        addLabel( paragraphTechniques, TECHNIQUES );
        paragraphTechniques.add( new Chunk( "\n", normalFont ) );

        for (String key : TECHNICAL_KEYS) {
            techniques( paragraphTechniques, skillsJson, key );
        }

        paragraphTechniques.setLeading( 0, 1.5f );
        paragraphTechniques.setAlignment( Element.ALIGN_LEFT );
        document.add( paragraphTechniques );

        // Poste
        Paragraph paragraphOccupiedPositions = new Paragraph();
        paragraphOccupiedPositions.add( new Chunk( "\n", normalFont ) );
        addLabel( paragraphOccupiedPositions, OCCUPIED_POSITIONS );
        paragraphOccupiedPositions.add( new Chunk( "\n", normalFont ) );
        JsonNode occupiedPositionsNode = skillsJson.get( OCCUPIED_POSITIONS );
        addList( document, paragraphOccupiedPositions, occupiedPositionsNode );

        // Domaines fonctionnel
        Paragraph paragraphFunctionalDomain = new Paragraph();
        addLabel( paragraphFunctionalDomain, FUNCTIONAL_DOMAINS );
        paragraphFunctionalDomain.add( new Chunk( "\n", normalFont ) );
        JsonNode functionalDomainNode = skillsJson.get( FUNCTIONAL_DOMAINS );
        addList( document, paragraphFunctionalDomain, functionalDomainNode );
    }

    private static void addEmployer(Document document, JsonNode skillsJson) {
        JsonNode employerNode = skillsJson.get( EMPLOYER );
        if (employerNode != null && employerNode.isArray()) {
            for (JsonNode employer : employerNode) {
                Paragraph paragraph = new Paragraph();
                paragraph.setKeepTogether( true );
                paragraph.add( new Chunk( EMPLOYER.substring( 0, 1 ).toUpperCase(), subtitleFontBoldUp ) );
                paragraph.add( new Chunk( EMPLOYER.substring( 1 ).toUpperCase() + " : ", subtitleFontBold ) );
                String employerName = employer.get( NAME ).asText();
                paragraph.add( new Chunk( employerName.substring( 0, 1 ).toUpperCase(), subtitleFontBoldUp ) );
                paragraph.add( new Chunk( employerName.substring( 1 ).toUpperCase(), subtitleFontBold ) );
                String functionalDomain = employer.get( FUNCTIONAL_DOMAIN ).asText();
                paragraph.add( new Chunk( " (" + functionalDomain.substring( 0, 1 ).toUpperCase(), subSubtitleFontBoldUp ) );
                paragraph.add( new Chunk( functionalDomain.substring( 1 ).toUpperCase(), subSubtitleFontBold ) );
                paragraph.add( new Chunk( ") \n", subSubtitleFontBoldUp ) );
                paragraph.setAlignment( Element.ALIGN_LEFT );
                JsonNode customers = employer.get( "Clients" );
                if (customers != null && customers.isArray()) {
                    for (JsonNode customer : customers) {
                        addCustomer( paragraph, customer );
                    }
                } else {
                    addMission( paragraph, employer );
                    addMissionDetails( paragraph, employer );
                }
                document.add( paragraph );
            }
        }
    }

    private static void addCustomer(Paragraph paragraph, JsonNode customer) {
        if (!customer.get( NAME ).asText().isEmpty()) {
            addCutomerName( paragraph, customer );
        }
        addMission( paragraph, customer );
        addMissionDetails( paragraph, customer );
    }

    private static void addMission(Paragraph paragraph, JsonNode customer) {
        addPositionHeld( paragraph, customer );
        addPeriod( paragraph, customer );
    }

    private static void addPeriod(Paragraph paragraph, JsonNode customer) {
        paragraph.add( new Chunk( PERIOD.substring( 0, 1 ).toUpperCase(), normalFontBoldUp ) );
        paragraph.add( new Chunk( PERIOD.substring( 1 ).toUpperCase(), normalFontBold ) );
        JsonNode debut = customer.get( PERIOD ).get( "Début" );
        JsonNode fin = customer.get( PERIOD ).get( "Fin" );

        LocalDate dateDebut = LocalDate.of( debut.get( "Année" ).asInt(), debut.get( "Mois" ).asInt(), debut.get( "Jour" ).asInt() );
        LocalDate dateFin = LocalDate.of( fin.get( "Année" ).asInt(), fin.get( "Mois" ).asInt(), fin.get( "Jour" ).asInt() );
        Period difference = Period.between( dateDebut, dateFin );

        paragraph.add( new Chunk( " de " + getNameOfTheMonth( debut ) + " " + debut.get( "Année" ).asText() +
                " à " + getNameOfTheMonth( fin ) + " " + fin.get( "Année" ).asText() + " (" + (difference.getMonths() + 1) + " mois) \n", normalFont ) );
    }

    private static void addPositionHeld(Paragraph paragraph, JsonNode customer) {
        paragraph.add( new Chunk( POSITION_HELD.substring( 0, 1 ).toUpperCase(), normalFontBoldUp ) );
        paragraph.add( new Chunk( (POSITION_HELD + " : ").substring( 1 ).toUpperCase(), normalFontBold ) );
        paragraph.add( new Chunk( customer.get( POSITION_HELD ).asText() + "\n", normalFont ) );
    }

    private static void addCutomerName(Paragraph paragraph, JsonNode customer) {
        paragraph.add( new Chunk( CUSTOMER.substring( 0, 1 ).toUpperCase(), subSubtitleFontBoldUp ) );
        paragraph.add( new Chunk( (CUSTOMER + " : ").substring( 1 ).toUpperCase(), subSubtitleFontBold ) );
        String customerName = customer.get( NAME ).asText();
        paragraph.add( new Chunk( customerName.substring( 0, 1 ).toUpperCase(), subSubtitleFontBoldUp ) );
        paragraph.add( new Chunk( customerName.substring( 1 ).toUpperCase(), subSubtitleFontBold ) );
        String functionalDomain = customer.get( FUNCTIONAL_DOMAIN ).asText();
        paragraph.add( new Chunk( " (" + functionalDomain.substring( 0, 1 ).toUpperCase(), subSubtitleFontBoldUp ) );
        paragraph.add( new Chunk( functionalDomain.substring( 1 ).toUpperCase(), subSubtitleFontBold ) );
        paragraph.add( new Chunk( ") \n", subSubtitleFontBoldUp ) );
    }

    private static void addMissionDetails(Paragraph paragraph, JsonNode client) {
        addParagraphLabel( paragraph, client, CONTEXT );
        addParagraphLabel( paragraph, client, REALISATION );
        addLabel( paragraph, TECHNICAL_ENVIRONMENT );
        boolean first = true;
        for (String key : TECHNICAL_KEYS) {
            JsonNode techniquesNode = client.get( TECHNIQUES );
            if (techniquesNode != null && !techniquesNode.isMissingNode() && !techniquesNode.isNull()) {
                JsonNode techArray = techniquesNode.get( key );
                if (techArray != null && !techArray.isMissingNode() && techArray.isArray() && !techArray.isEmpty()) {
                    if (!first) {
                        paragraph.add( new Chunk( ", ", normalFont ) );
                    }
                    addAllTechniques( paragraph, client, key );
                    first = false;
                }
            }
        }
        paragraph.add( new Chunk( "\n" ) );
    }

    private static void addAllTechniques(Paragraph paragraphExpertise, JsonNode skillsJson, String technique) {
        JsonNode techniquesNode = skillsJson.get( TECHNIQUES );
        if (techniquesNode == null || techniquesNode.isMissingNode() || techniquesNode.isNull()) {
            return;
        }
        JsonNode techArray = techniquesNode.get( technique );
        if (techArray == null || techArray.isMissingNode() || !techArray.isArray() || techArray.isEmpty()) {
            return;
        }
        for (int i = 0; i < techArray.size(); i++) {
            paragraphExpertise.add( new Chunk( techArray.get( i ).asText(), normalFont ) );
            if (i < techArray.size() - 1) {
                paragraphExpertise.add( new Chunk( ", ", normalFont ) );
            }
        }
    }

    private static void techniques(Paragraph paragraphExpertise, JsonNode skillsJson, String technique) {
        JsonNode techniquesNode = skillsJson.get( TECHNIQUES );
        if (techniquesNode == null || techniquesNode.isMissingNode() || techniquesNode.isNull()) {
            return;
        }
        JsonNode techArray = techniquesNode.get( technique );
        if (techArray == null || techArray.isMissingNode() || !techArray.isArray() || techArray.isEmpty()) {
            return;
        }

        addLabel( paragraphExpertise, technique );

        for (int i = 0; i < techArray.size(); i++) {
            paragraphExpertise.add( new Chunk( " " + techArray.get( i ).asText(), normalFont ) );
            if (i < techArray.size() - 1) {
                paragraphExpertise.add( new Chunk( ",", normalFont ) );
            }
        }
        paragraphExpertise.add( new Chunk( "\n", normalFont ) );
    }

}
