package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
            generator.generatePDFFromWord( "CV - Sans images.docx", nameFile + " - CV - Sans images.pdf", monJson );
            generator.generatePDFFromWord( "Dossier de compétences.docx", nameFile + " - Dossier de compétences.pdf", monJson );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        // 2. Créer les documents PDF
        try {
            PDFMergerUtility margeWithoutImages = new PDFMergerUtility();
            margeWithoutImages.addSource( nameFile + " - CV - Sans images.pdf" );
            margeWithoutImages.addSource( nameFile + " - Dossier de compétences.pdf" );
            String nameFilesWithoutImages = nameFile + " - CV et Dossier de compétence - Sans images.pdf";
            margeWithoutImages.setDestinationFileName( nameFilesWithoutImages );

            try (FileOutputStream fos = new FileOutputStream( nameFilesWithoutImages )) {
                margeWithoutImages.setDestinationStream( fos );
                margeWithoutImages.mergeDocuments( null );
            }

            PDFMergerUtility ut = new PDFMergerUtility();
            ut.addSource( nameFile + " - CV.pdf" );
            ut.addSource( nameFile + " - Dossier de compétences.pdf" );
            String nameFiles = nameFile + " - CV et Dossier de compétence.pdf";
            ut.setDestinationFileName( nameFiles );

            try (FileOutputStream fos = new FileOutputStream( nameFiles )) {
                ut.setDestinationStream( fos );
                ut.mergeDocuments( null );
            }

            XWPFDocument document = new XWPFDocument( new FileInputStream( "Dossier de compétences.docx" ) );
            XHTMLOptions options = XHTMLOptions.create();

            // Exportation vers un fichier HTML
            OutputStream out = new FileOutputStream( "Dossier de compétences.html" );
            XHTMLConverter.getInstance().convert( document, out, options );

            logger.log( Level.INFO, "Fin de la génération des PDF" );
        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
            System.exit( 1 );
        }
        System.exit( 0 );
    }

}
