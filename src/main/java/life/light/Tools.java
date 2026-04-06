package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static com.lowagie.text.Font.BOLD;
import static com.lowagie.text.FontFactory.TIMES_ROMAN;
import static com.lowagie.text.FontFactory.getFont;
import static com.lowagie.text.PageSize.A4;
import static java.lang.System.Logger.Level.ERROR;
import static life.light.CreateResumePDF.FONT_SIZE_NORMAL;

public class Tools {

    static final java.lang.System.Logger logger = System.getLogger( Tools.class.getName() );
    static final String CUSTOMER = "Client";
    static final String POSITION_HELD = "Poste";
    static final String OCCUPIED_POSITIONS = "Postes";
    static final String PERIOD = "Période";
    static final String CONTEXT = "Contexte";
    static final String REALISATION = "Réalisation";
    static final String TECHNICAL_ENVIRONMENT = "Environnement technique";
    static final String NAME = "Nom";
    static final String FIRST_NAME = "Prénom";
    static final String FUNCTIONAL_DOMAIN = "Domaine fonctionnel";
    static final String FUNCTIONAL_DOMAINS = "Domaines fonctionnel";
    static final String YEAR_OF_EXPERIENCE = "Année d'expérience";
    static final String YEAR = "Année";
    static final String MONTH = "Mois";
    static final String DAY = "Jour";
    static final String TECHNIQUES = "Techniques";
    static final String AVAILABILITY = "Disponibilité";
    static final String[] TECHNICAL_KEYS = {"Langages", "Tests", "Frameworks", "Éditeurs de code", "Bases de données", "Intégration continue", "Versioning", "Bug tracker", "Autre outils", "Système d’exploitation", "Architectures", "Méthodologies"};
    static final String EMPLOYER = "Employeur";
    static Font titleFontUp = getFont( TIMES_ROMAN, 25, BOLD );
    static Font titleFont = getFont( TIMES_ROMAN, 20, BOLD );
    static Font subtitleFontBoldUp = getFont( TIMES_ROMAN, 20, BOLD );
    static Font subtitleFontBold = getFont( TIMES_ROMAN, 16, BOLD );
    static Font subSubtitleFontBoldUp = getFont( TIMES_ROMAN, 18, BOLD );
    static Font subSubtitleFontBold = getFont( TIMES_ROMAN, 14, BOLD );
    static Font normalFontUp = getFont( TIMES_ROMAN, FONT_SIZE_NORMAL + 2 );
    static Font normalFont = getFont( TIMES_ROMAN, FONT_SIZE_NORMAL + 1 );
    static Font normalFontBoldUp = getFont( TIMES_ROMAN, FONT_SIZE_NORMAL + 2, BOLD );
    static Font normalFontBold = getFont( TIMES_ROMAN, FONT_SIZE_NORMAL - 1, BOLD );
    static Font emptyLineFont = getFont( TIMES_ROMAN, 2 );

    static Document createDocument(String namePdfFile) {
        // Les valeurs sont en "points" (72 points = 1 pouce = 2,54 cm)
        // Ici, on met environ 1 cm de marge partout (28 points).
        float margeGauche = 28f;
        float margeDroite = 28f;
        float margeHaut = 20f;
        float margeBas = 20f;
        Document document = new Document( A4, margeGauche, margeDroite, margeHaut, margeBas );
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance( document, new FileOutputStream( namePdfFile ) );
        } catch (FileNotFoundException e) {
            logger.log( ERROR, "Échec de la création de l'image de fond du dossier de compétence", e );
        }
        try {
            BackgroundEvent event = new BackgroundEvent( "images/Fond.png" );
            if (writer != null) {
                writer.setPageEvent( event );
            }
        } catch (Exception e) {
            logger.log( ERROR, "Échec de la création de l'image de fond du dossier de compétence", e );
        }
        return document;
    }

    static void addTitle(com.lowagie.text.Document document) {
        Paragraph paragraph = new Paragraph();
        upperCasse( paragraph, "Dossier de compétence", false );
        paragraph.setAlignment( com.lowagie.text.Element.ALIGN_CENTER );
        document.add( paragraph );

        Paragraph paragraphSpace = new Paragraph();
        paragraphSpace.add( new Chunk( "\n", titleFont ) );
        paragraphSpace.setLeading( 0, 1.5f );
        paragraphSpace.setAlignment( com.lowagie.text.Element.ALIGN_CENTER );
        document.add( paragraphSpace );
    }

    static void upperCasse(Paragraph paragraph, String title, Boolean isUnderline) {
        Chunk chunkUp = new Chunk( title.substring( 0, 1 ).toUpperCase(), titleFontUp );
        if (isUnderline) {
            chunkUp.setUnderline( 1.5f, -2f );
        }
        paragraph.add( chunkUp );
        Chunk chunk = new Chunk( title.substring( 1 ).toUpperCase(), titleFont );
        if (isUnderline) {
            chunk.setUnderline( 1.5f, -2f );
        }
        paragraph.add( chunk );
    }

    static void addLabel(Paragraph paragraph, String label, Boolean isUnderline) {
        Chunk chunkUp = new Chunk( label.substring( 0, 1 ).toUpperCase(), normalFontUp );
        if (isUnderline) {
            chunkUp.setUnderline( 1f, -1.5f );
        }
        paragraph.add( chunkUp );
        Chunk chunk = new Chunk( label.substring( 1 ).toUpperCase(), normalFont );
        if (isUnderline) {
            chunk.setUnderline( 1f, -1.5f );
        }
        paragraph.add( chunk );
        paragraph.add( new Chunk( " : ", normalFont ) );
    }

    static void addSubtitle(com.lowagie.text.Document document, String title, Boolean isUnderline) {
        Paragraph paragraph = new Paragraph();
        Chunk chunkUp = new Chunk( title.substring( 0, 1 ).toUpperCase(), subtitleFontBoldUp );
        if (isUnderline) {
            chunkUp.setUnderline( 1.2f, -1.8f );
        }
        paragraph.add( chunkUp );
        Chunk chunk = new Chunk( title.substring( 1 ).toUpperCase() + " :", subtitleFontBold );
        if (isUnderline) {
            chunk.setUnderline( 1.2f, -1.8f );
        }
        paragraph.add( chunk );
        paragraph.setAlignment( com.lowagie.text.Element.ALIGN_LEFT );
        document.add( paragraph );
    }

    static String getNameOfTheMonth(com.fasterxml.jackson.databind.JsonNode debut) {
        int moisDebut = Integer.parseInt( debut.get( "Mois" ).asText() );
        java.time.Month mois = java.time.Month.of( moisDebut );
        return mois.getDisplayName( java.time.format.TextStyle.FULL, java.util.Locale.FRANCE );
    }

    static void addEmptyLine(com.lowagie.text.Document document) {
        Paragraph p = new Paragraph();
        p.add( new Chunk( "\n", emptyLineFont ) );
        document.add( p );
    }

    static void addList(Document document, Paragraph paragraphPostes, JsonNode postesNode) {
        if (postesNode != null && postesNode.isArray()) {
            for (int i = 0; i < postesNode.size(); i++) {
                String poste = postesNode.get( i ).asText();
                if (!poste.isEmpty()) {
                    paragraphPostes.add( new Chunk( poste, normalFont ) );
                    if (i < postesNode.size() - 1) {
                        paragraphPostes.add( new Chunk( ", ", normalFont ) );
                    }
                }
            }
        }
        document.add( paragraphPostes );
        addEmptyLine( document );
    }

    static void addParagraphLabel(Document document, JsonNode client, String context) {
        Paragraph paragraph = new Paragraph();
        addLabel( paragraph, context, true );
        paragraph.add( new Chunk( "\n", normalFont ) );
        paragraph.add( new Chunk( client.get( context ).asText(), normalFont ) );
        paragraph.setAlignment( Element.ALIGN_JUSTIFIED );
        document.add( paragraph );
    }

    static void addParagraphLabel(Paragraph paragraph, JsonNode client, String context) {
        addLabel( paragraph, context, true );
        paragraph.add( new Chunk( "\n" + client.get( context ).asText() + "\n", normalFont ) );
        paragraph.setAlignment( Element.ALIGN_JUSTIFIED );
    }
}
