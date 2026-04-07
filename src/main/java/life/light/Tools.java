package life.light;

import com.fasterxml.jackson.databind.JsonNode;

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
    static final String TECHNIQUES = "Techniques";
    static final String AVAILABILITY = "Disponibilité";
    static final String[] TECHNICAL_KEYS = {"Langages", "Tests", "Frameworks", "Éditeurs de code", "Bases de données", "Intégration continue", "Versioning", "Bug tracker", "Autre outils", "Système d’exploitation", "Architectures", "Méthodologies"};
    static final String EMPLOYER = "Employeur";

    static String getNameOfTheMonth(JsonNode debut) {
        int moisDebut = Integer.parseInt( debut.get( "Mois" ).asText() );
        java.time.Month mois = java.time.Month.of( moisDebut );
        return mois.getDisplayName( java.time.format.TextStyle.FULL, java.util.Locale.FRANCE );
    }

    static String getBackgroundPath() {
        return "images/Fond.png";
    }
}
