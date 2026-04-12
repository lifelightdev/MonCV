package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
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
        logger.log( Level.INFO, "Début" );
        String exec = " DOC";

        if (exec.contains( "DOC" )) {
            extractedWordToPdf();
        }
        if (exec.contains( "HTML" )) {
            logger.log( Level.INFO, "Début avec les fichiers html" );
            String nameFileResumeJson = "CV.json";
            JsonNode resumeJson = null;
            try {
                resumeJson = ReadJson.getCvJson( nameFileResumeJson );
                // Validation du CV par rapport au schéma
                String schemaPath = "src/main/resources/CV.schema.json";
                ReadJson.validateJson( resumeJson, schemaPath );
            } catch (IOException e) {
                logger.log( Level.ERROR, "Erreur lors de la lecture des fichiers JSON ", e );
                System.exit( -1 );
            }

            String nameFile = resumeJson.get( "Nom" ).asText() + " " + resumeJson.get( "Prénom" ).asText();
            try (OutputStream os1 = new FileOutputStream( nameFile + " - CV.html.pdf" )) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();

                // On pointe vers votre fichier source
                File htmlFile = new File( "CV.html" );
                builder.withFile( htmlFile );

                builder.toStream( os1 );
                builder.run();
                System.out.println( "PDF généré avec succès !" );
            } catch (Exception e) {
                logger.log( Level.ERROR, "Erreur lors de la génération des fichiers PDF à partir des fichiers HTML ", e );
                System.exit( -1 );
            }
            logger.log( Level.INFO, "Fin avec les fichiers html" );

        }

        logger.log( Level.INFO, "Fin de la génération des PDF" );
        System.exit( 0 );
    }

    private static void extractedWordToPdf() {
        logger.log( Level.INFO, "Début avec les fichiers word" );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode monJson = mapper.readTree( new File( "important.json" ) );
            // 2. Appeler le générateur
            CVGenerator generator = new CVGenerator();
            generator.generatePDFFromWord( "CV.docx", "CV.docx.pdf", monJson );
            generator.generatePDFFromWord( "Dossier de compétences.docx", "Dossier de compétences.docx.pdf", monJson );
        } catch (IOException e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
            System.exit( 1 );
        }

        try {
            PDFMergerUtility margeWithoutImages = new PDFMergerUtility();
            margeWithoutImages.addSource( "CV.docx.pdf" );
            margeWithoutImages.addSource( "Dossier de compétences.docx.pdf" );
            String nameFilesWithoutImages = "CV et Dossier de compétence.docx.pdf";
            margeWithoutImages.setDestinationFileName( nameFilesWithoutImages );

            try (FileOutputStream fos = new FileOutputStream( nameFilesWithoutImages )) {
                margeWithoutImages.setDestinationStream( fos );
                margeWithoutImages.mergeDocuments( null );
            }

        } catch (Exception e) {
            logger.log( Level.ERROR, "Échec de la génération du CV", e );
            System.exit( 1 );
        }
        logger.log( Level.INFO, "Fin avec les fichiers word" );
    }

}
