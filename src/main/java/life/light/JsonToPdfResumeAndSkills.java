package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static life.light.CreateResumePDF.createResume;
import static life.light.CreateSkillsPDF.createSkills;

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

        // 2. Créer les documents PDF
        try {
            PDFMergerUtility ut = new PDFMergerUtility();
            ut.addSource( createResume( resumeJson ) );
            ut.addSource( createSkills( dossierCompetencesJson ) );
            String nameFileResumePDF = resumeJson.get( "Nom" ).asText() + " " + resumeJson.get( "Prénom" ).asText() + " - CV et Dossier de compétence.pdf";
            ut.setDestinationFileName( nameFileResumePDF );
            ut.mergeDocuments( null );
            logger.log( Level.INFO, "Fin de la génération des PDF" );
        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
            System.exit( 1 );
        }
    }

}
