package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class JsonToPdfResumeAndSkills {

    private static final Logger logger = System.getLogger( JsonToPdfResumeAndSkills.class.getName() );

    protected static int lastExitCode = 0;

    static void main() {
        lastExitCode = 0;

        logger.log( Level.INFO, "Début de la génération des PDF" );

        String nameFileResumeJson = "CV.json";
        String nameFileDossierCompetencesJson = "DossierCompetences.json";
        JsonNode resumeJson;
        JsonNode dossierCompetencesJson;
        try {
            resumeJson = ReadJson.getCvJson( nameFileResumeJson );
            // Validation du CV par rapport au schéma
            String schemaPath = "src/main/resources/CV.schema.json";
            ReadJson.validateJson( resumeJson, schemaPath );

            dossierCompetencesJson = ReadJson.getCvJson( nameFileDossierCompetencesJson );
            // Validation du Dossier de Compétences par rapport au schéma
            String schemaSkillsPath = "src/main/resources/DossierCompetences.schema.json";
            ReadJson.validateJson( dossierCompetencesJson, schemaSkillsPath );

        } catch (IOException e) {
            logger.log( Level.ERROR, "Erreur lors de la lecture des fichiers JSON ", e );
            lastExitCode = -1;
            return;
        }

        String nameFile = resumeJson.get( "Nom" ).asText() + " " + resumeJson.get( "Prénom" ).asText();

        // 1. Charger le JSON (Jackson)
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode monJson = mapper.readTree( new File( "important.json" ) );
            // 2. Appeler le générateur
            CVGenerator generator = new CVGenerator();
            generator.generatePDFFromWord( "CV.docx", nameFile + " - CV.pdf", monJson );
            generator.generatePDFFromWord( "Dossier de compétences.docx", nameFile + " - Dossier de compétences.pdf", monJson );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        // 2. Créer les documents PDF
        try {
            PDFMergerUtility ut = new PDFMergerUtility();
            ut.addSource( nameFile + " - CV.pdf" );
            ut.addSource( nameFile + " - Dossier de compétences.pdf" );
            String nameFiles = nameFile + " - CV et Dossier de compétence.pdf";
            ut.setDestinationFileName( nameFiles );

            try (FileOutputStream fos = new FileOutputStream( nameFiles )) {
                ut.setDestinationStream( fos );
                ut.mergeDocuments( null );
            }

            logger.log( Level.INFO, "Fin de la génération des PDF" );
        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
            System.exit( 1 );
        }
        System.exit( 0 );
    }

}
