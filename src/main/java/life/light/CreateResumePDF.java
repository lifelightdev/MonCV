package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static life.light.PDFBoxTools.FONT_BOLD;
import static life.light.PDFBoxTools.FONT_PLAIN;

public class CreateResumePDF {

    private static final Logger logger = System.getLogger( CreateResumePDF.class.getName() );

    static void createResume(JsonNode resumeJson) {
        String nameFileCVPDF = resumeJson.get( "Nom" ).asText() + " " + resumeJson.get( "Prénom" ).asText() + " - CV.pdf";
        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, "images/Fond.png" );

            addHeader( resumeJson, tools );
            addSubHeader( resumeJson, tools );
            addBody( resumeJson, tools );

            tools.close();
            document.save( nameFileCVPDF );
        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
        }
    }

    static void addHeader(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        // Photo à gauche (approximate coordinate)
        try {
            PDImageXObject photo = PDImageXObject.createFromFile( "Ma_Photo.jpg", tools.getDocument() );
            float photoSize = 70;
            tools.getContentStream().drawImage( photo, 28, tools.getCursorY() - photoSize, photoSize, photoSize );
        } catch (Exception e) {
            logger.log( Level.ERROR, "La photo du CV n'a pas été trouvée" );
        }

        // Texte à droite de la photo
        float textStartX = 110;
        float originalY = tools.getCursorY();
        tools.setCursorY( originalY - 10 );
        tools.addText( resumeJson.get( "Prénom" ).asText() + " " + resumeJson.get( "Nom" ).asText(), 24, FONT_BOLD, textStartX, 400 );

        JsonNode titre = resumeJson.get( "Titre" );
        if (titre != null && titre.isArray() && !titre.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < titre.size(); i++) {
                sb.append( titre.get( i ).asText() );
                if (i < titre.size() - 1) sb.append( " " );
            }
            tools.addText( sb.toString(), 14, FONT_BOLD, textStartX, 400 );
        }

        tools.setCursorY( originalY - 80 ); // Descendre après le header
    }

    static void addSubHeader(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        float leftColWidth = 150;
        float startY = tools.getCursorY();

        // Colonne gauche : Contact
        tools.addText( "Téléphone: " + (resumeJson.has( "Téléphone" ) ? resumeJson.get( "Téléphone" ).asText() : ""), 10, FONT_PLAIN, 28, leftColWidth );
        tools.addText( "Email: " + (resumeJson.has( "Email" ) ? resumeJson.get( "Email" ).asText() : ""), 10, FONT_PLAIN, 28, leftColWidth );
        tools.addText( "GitHub: " + (resumeJson.has( "GitHub" ) ? resumeJson.get( "GitHub" ).asText() : ""), 10, FONT_PLAIN, 28, leftColWidth );

        // Colonne droite : Présentation
        float rightColX = 180;
        tools.setCursorY( startY );
        tools.addText( resumeJson.has( "Présentation" ) ? resumeJson.get( "Présentation" ).asText() : "", 12, FONT_PLAIN, rightColX, 380 );

        tools.setCursorY( tools.getCursorY() - 10 );
    }

    static void addBody(JsonNode resumeJson, PDFBoxTools tools) throws IOException {
        float startY = tools.getCursorY();
        float leftColX = 28;
        float leftColWidth = 180;
        float rightColX = 220;
        float rightColWidth = 340;

        // Colonne Gauche: Compétences, Langues, Formations
        tools.addText( "COMPÉTENCES", 14, FONT_BOLD, leftColX, leftColWidth );
        addCompetences( resumeJson, tools, leftColX, leftColWidth );

        tools.setCursorY( tools.getCursorY() - 10 );
        tools.addText( "LANGUES", 14, FONT_BOLD, leftColX, leftColWidth );
        addLangues( resumeJson, tools, leftColX, leftColWidth );

        tools.setCursorY( tools.getCursorY() - 10 );
        tools.addText( "FORMATIONS", 14, FONT_BOLD, leftColX, leftColWidth );
        addFormations( resumeJson, tools, leftColX, leftColWidth );

        float leftColEndY = tools.getCursorY();

        // Colonne Droite: Expériences
        tools.setCursorY( startY );
        tools.addText( "EXPÉRIENCES", 14, FONT_BOLD, rightColX, rightColWidth );
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
                    tools.addText( "- " + sb, 10, FONT_PLAIN, x, width );
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
                tools.addText( "- " + nom + " : " + niveau, 10, FONT_PLAIN, x, width );
            }
        }
    }

    private static void addFormations(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode formations = resumeJson.get( "Formations" );
        if (formations != null && formations.isArray()) {
            for (JsonNode formation : formations) {
                String titre = formation.has( "Titre" ) ? formation.get( "Titre" ).asText() : "";
                String annee = formation.has( "Année" ) ? formation.get( "Année" ).asText() : "";
                tools.addText( "- " + titre + " (" + annee + ")", 10, FONT_PLAIN, x, width );
            }
        }
    }

    private static void addExperiences(JsonNode resumeJson, PDFBoxTools tools, float x, float width) throws IOException {
        JsonNode experiences = resumeJson.get( "Experiences" );
        if (experiences != null && experiences.isArray()) {
            for (JsonNode exp : experiences) {
                String annee = exp.has( "Année" ) ? exp.get( "Année" ).asText() : "";
                String entreprise = exp.has( "Entreprise" ) ? exp.get( "Entreprise" ).asText() : "";
                tools.addText( annee + " - " + entreprise, 12, FONT_BOLD, x, width );
                JsonNode fonctions = exp.get( "Fonctions" );
                if (fonctions != null && fonctions.isArray()) {
                    for (JsonNode fonction : fonctions) {
                        String titre = fonction.has( "Titre" ) ? fonction.get( "Titre" ).asText() : "";
                        tools.addText( "  " + titre, 10, FONT_BOLD, x, width );
                        JsonNode taches = fonction.get( "Tâches" );
                        if (taches != null && taches.isArray()) {
                            for (JsonNode tache : taches) {
                                if (tache.has( "Description" )) {
                                    tools.addText( "    . " + tache.get( "Description" ).asText(), 10, FONT_PLAIN, x, width );
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
