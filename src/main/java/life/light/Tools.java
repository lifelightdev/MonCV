package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;

public class Tools {

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
    static final String TECHNICALS = "Techniques";
    static final String AVAILABILITY = "Disponibilité";
    static final String[] TECHNICAL_KEYS = {"Langages", "Tests", "Frameworks", "Éditeurs de code", "Bases de données", "Intégration continue", "Versioning", "Bug tracker", "Autre outils", "Système d’exploitation", "Architectures", "Méthodologies"};
    static final String EMPLOYER = "Employeur";
    static final int FONT_SIZE = 14;
    static final PDFont FONT_PLAIN = new PDType1Font( Standard14Fonts.FontName.TIMES_ROMAN );
    static final PDFont FONT_BOLD = new PDType1Font( Standard14Fonts.FontName.TIMES_BOLD );

    static String getNameOfTheMonth(JsonNode debut) {
        int moisDebut = Integer.parseInt( debut.get( "Mois" ).asText() );
        java.time.Month mois = java.time.Month.of( moisDebut );
        return mois.getDisplayName( java.time.format.TextStyle.FULL, java.util.Locale.FRANCE );
    }

    static String getBackgroundPath() {
        return "images/Fond.png";
    }

    public void ajouterNumerotation(PDDocument document) throws IOException {
        int totalPages = document.getNumberOfPages();
        float marginBottom = 30; // Distance du bas de la page

        for (int i = 0; i < totalPages; i++) {
            PDPage page = document.getPage( i );
            PDRectangle pageSize = page.getMediaBox();

            // Texte à afficher (ex: "Page 1 / 5")
            String text = String.format( "Page %d / %d", (i + 1), totalPages );

            // Calcul pour centrer le texte
            float textWidth = FONT_PLAIN.getStringWidth( text ) / 1000 * FONT_SIZE;
            float x = (pageSize.getWidth() - textWidth) / 2;

            // Ouvrir le stream en mode APPEND pour ne pas effacer le contenu existant
            // Le troisième paramètre 'true' active la compression, le quatrième 'true' préserve l'état graphique
            try (PDPageContentStream contentStream = new PDPageContentStream( document, page, PDPageContentStream.AppendMode.APPEND, true, true )) {
                contentStream.beginText();
                contentStream.setFont( FONT_PLAIN, FONT_SIZE );
                contentStream.newLineAtOffset( x, marginBottom );
                contentStream.showText( text );
                contentStream.endText();
            }
        }
    }
}
