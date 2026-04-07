package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static life.light.PDFBoxTools.FONT_BOLD;
import static life.light.PDFBoxTools.FONT_PLAIN;
import static life.light.Tools.getBackgroundPath;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

public class CreateResumePDF {

    private static final Logger logger = System.getLogger( CreateResumePDF.class.getName() );
    public static final int FONT_SIZE = 14;
    public static final int MARGIN_X = 20;

    static String createResume(JsonNode resumeJson) {
        String nameFileResumePDF = resumeJson.get( "Nom" ).asText() + " " + resumeJson.get( "Prénom" ).asText() + " - CV.pdf";
        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, getBackgroundPath() );

            addHeader( resumeJson, tools );
            addSubHeader( resumeJson, tools );
            addBody( resumeJson, tools );

            tools.close();

            try (FileOutputStream fos = new FileOutputStream( nameFileResumePDF )) {
                document.save( fos );
            }
        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
        }
        return nameFileResumePDF;
    }

    static void addHeader(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        // Photo à gauche (approximate coordinate)
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "Ma_Photo.jpg", tools.getDocument() );
            float photoSize = 70;
            tools.getContentStream().drawImage( photo, 60, tools.getCursorY() - photoSize, photoSize, photoSize );
        } catch (Exception e) {
            logger.log( Level.ERROR, "La photo du CV n'a pas été trouvée" );
        }

        // Texte à droite de la photo
        float textStartX = 180;
        float originalY = tools.getCursorY();
        tools.setCursorY( originalY - 20 );
        tools.addCenteredText( resumeJson.get( "Prénom" ).asText() + " " + resumeJson.get( "Nom" ).asText(), 25, FONT_BOLD, A4.getWidth() + textStartX );

        JsonNode title = resumeJson.get( "Titre" );
        if (title != null && title.isArray() && !title.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < title.size(); i++) {
                sb.append( title.get( i ).asText() );
                if (i < title.size() - 1) sb.append( " / " );
            }
            tools.addCenteredText( sb.toString(), 14, FONT_BOLD, A4.getWidth() + textStartX );
        }

        JsonNode subTitle = resumeJson.get( "Sous titre" );
        if (subTitle != null && subTitle.isArray() && !subTitle.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < subTitle.size(); i++) {
                sb.append( subTitle.get( i ).asText() );
                if (i < subTitle.size() - 1) sb.append( " / " );
            }
            tools.addCenteredText( sb.toString(), 14, FONT_BOLD, A4.getWidth() + textStartX );
        }

        tools.setCursorY( originalY - 90 ); // Descendre après le header
    }

    static void addSubHeader(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        float leftColWidth = 180;
        float startY = tools.getCursorY();

        // Colonne gauche : Contact
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "images/Téléphone.png", tools.getDocument() );
            float photoSize = 10;
            tools.getContentStream().drawImage( photo, MARGIN_X, tools.getCursorY(), photoSize, photoSize );
            tools.addText( (resumeJson.has( "Téléphone" ) ? resumeJson.get( "Téléphone" ).asText() : ""), FONT_SIZE, FONT_PLAIN, MARGIN_X + 5 + photoSize + 2, leftColWidth );
        } catch (Exception e) {
            logger.log( Level.ERROR, "L'icône du téléphone n'a pas été trouvée" );
        }
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "images/Email.png", tools.getDocument() );
            float photoSize = 10;
            tools.getContentStream().drawImage( photo, MARGIN_X, tools.getCursorY() - 2, photoSize, photoSize );
            tools.addText( (resumeJson.has( "Email" ) ? resumeJson.get( "Email" ).asText() : ""), FONT_SIZE, FONT_PLAIN, MARGIN_X + 5 + photoSize + 2, leftColWidth );
        } catch (Exception e) {
            logger.log( Level.ERROR, "L'icône de l'email n'a pas été trouvée" );
        }
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "images/GitHub.png", tools.getDocument() );
            float photoSize = 10;
            tools.getContentStream().drawImage( photo, MARGIN_X, tools.getCursorY() - 2, photoSize, photoSize );
            tools.addText( (resumeJson.has( "GitHub" ) ? resumeJson.get( "GitHub" ).asText() : ""), FONT_SIZE, FONT_PLAIN, MARGIN_X + 5 + photoSize + 2, leftColWidth );
        } catch (Exception e) {
            logger.log( Level.ERROR, "L'icône de GitHub n'a pas été trouvée" );
        }
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "images/LinkedIn.png", tools.getDocument() );
            float photoSize = 10;
            tools.getContentStream().drawImage( photo, MARGIN_X, tools.getCursorY() - 2, photoSize, photoSize );
            tools.addText( (resumeJson.has( "LinkedIn" ) ? resumeJson.get( "LinkedIn" ).asText() : ""), FONT_SIZE, FONT_PLAIN, MARGIN_X + 5 + photoSize + 2, leftColWidth );
        } catch (Exception e) {
            logger.log( Level.ERROR, "L'icône de LinkedIn n'a pas été trouvée" );
        }

        // Colonne droite : Présentation
        float rightColX = 220;
        tools.setCursorY( startY );
        tools.addText( resumeJson.has( "Présentation" ) ? resumeJson.get( "Présentation" ).asText() : "", FONT_SIZE + 2, FONT_PLAIN, rightColX, 340 );

        tools.setCursorY( tools.getCursorY() - 10 );
    }

    static void addBody(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        float startY = tools.getCursorY();
        float leftColX = MARGIN_X;
        float leftColWidth = 180;
        float rightColX = 220;
        float rightColWidth = 340;

        // Colonne Gauche : Compétences, Langues, Formations
        tools.addText( "Compétences", FONT_SIZE + 6, FONT_BOLD, leftColX, leftColWidth );
        addCompetences( resumeJson, tools, leftColX, leftColWidth );

        tools.setCursorY( tools.getCursorY() - 10 );
        tools.addText( "Langues", FONT_SIZE + 6, FONT_BOLD, leftColX, leftColWidth );
        addLangues( resumeJson, tools, leftColX, leftColWidth );

        tools.setCursorY( tools.getCursorY() - 10 );
        tools.addText( "Formations", FONT_SIZE + 6, FONT_BOLD, leftColX, leftColWidth );
        addFormations( resumeJson, tools, leftColX, leftColWidth );

        float leftColEndY = tools.getCursorY();

        // Colonne Droite : Expériences
        tools.setCursorY( startY );
        tools.addText( "Expériences", FONT_SIZE + 6, FONT_BOLD, rightColX, rightColWidth );
        addExperiences( resumeJson, tools, rightColX, rightColWidth );

        // On s'assure que le curseur est bien en dessous de la colonne la plus longue
        tools.setCursorY( Math.min( leftColEndY, tools.getCursorY() ) );
    }

    private static void addCompetences(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode competences = resumeJson.get( "Compétences" );
        if (competences != null && competences.isArray()) {
            for (JsonNode group : competences) {
                StringBuilder sb = new StringBuilder();
                if (group.isArray()) {
                    for (int i = 0; i < group.size(); i++) {
                        JsonNode desc = group.get( i ).get( "Description" );
                        if (desc != null) {
                            sb.append( desc.asText() );
                        }
                        if (i < group.size() - 1) sb.append( ", " );
                    }
                    tools.addText( "- " + sb, FONT_SIZE, FONT_PLAIN, x, width );
                }
            }
        }
    }

    private static void addLangues(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode langues = resumeJson.get( "Langues" );
        if (langues != null && langues.isArray()) {
            for (JsonNode langue : langues) {
                String nom = langue.has( "Nom" ) ? langue.get( "Nom" ).asText() : "";
                String niveau = langue.has( "Niveau" ) ? langue.get( "Niveau" ).asText() : "";
                tools.addText( "- " + nom + " : " + niveau, FONT_SIZE, FONT_PLAIN, x, width );
            }
        }
    }

    private static void addFormations(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode formations = resumeJson.get( "Formations" );
        if (formations != null && formations.isArray()) {
            for (JsonNode formation : formations) {
                String titre = formation.has( "Titre" ) ? formation.get( "Titre" ).asText() : "";
                String annee = formation.has( "Année" ) ? formation.get( "Année" ).asText() : "";
                tools.addText( "- " + titre + " (" + annee + ")", FONT_SIZE, FONT_PLAIN, x, width );
            }
        }
    }

    private static void addExperiences(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode experiences = resumeJson.get( "Experiences" );
        if (experiences != null && experiences.isArray()) {
            for (JsonNode exp : experiences) {
                String annee = exp.has( "Année" ) ? exp.get( "Année" ).asText() : "";
                String entreprise = exp.has( "Entreprise" ) ? exp.get( "Entreprise" ).asText() : "";
                tools.addText( annee + " - " + entreprise, FONT_SIZE + 2, FONT_BOLD, x, width );
                JsonNode fonctions = exp.get( "Fonctions" );
                if (fonctions != null && fonctions.isArray()) {
                    for (JsonNode fonction : fonctions) {
                        String titre = fonction.has( "Titre" ) ? fonction.get( "Titre" ).asText() : "";
                        tools.addText( "  " + titre, FONT_SIZE + 1, FONT_BOLD, x, width );
                        JsonNode taches = fonction.get( "Tâches" );
                        if (taches != null && taches.isArray()) {
                            for (JsonNode tache : taches) {
                                if (tache.has( "Description" )) {
                                    tools.addText( " - " + tache.get( "Description" ).asText(), FONT_SIZE, FONT_PLAIN, x, width );
                                }
                            }
                        }
                    }
                }
                tools.setCursorY( tools.getCursorY() - 5 );
            }
        }
    }
}
