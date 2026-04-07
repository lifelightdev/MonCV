package life.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CreatePDFTest {

    @Test
    @DisplayName("La génération complète du CV doit fonctionner avec un JSON valide")
    void shouldGenerateFullResume() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode cvJson = mapper.readTree( new File( "CV.json" ) );

        // On change le nom pour ne pas écraser le vrai PDF si on lançait les tests en local
        ((com.fasterxml.jackson.databind.node.ObjectNode) cvJson).put( "Nom", "TEST" );
        ((com.fasterxml.jackson.databind.node.ObjectNode) cvJson).put( "Prénom", "Test" );

        assertDoesNotThrow( () -> CreateResumePDF.createResume( cvJson ) );

        File generatedFile = new File( "TEST Test - CV.pdf" );
        if (generatedFile.exists()) {
            Files.delete( generatedFile.toPath() );
        }
    }

    @Test
    @DisplayName("Les méthodes de construction du document doivent fonctionner séparément")
    void shouldRunInternalMethods() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode cvJson = mapper.readTree( new File( "CV.json" ) );

        try (PDDocument document = new PDDocument()) {
            PDFBoxTools tools = new PDFBoxTools( document, null );
            assertDoesNotThrow( () -> CreateResumePDF.addHeader( cvJson, tools ) );
            assertDoesNotThrow( () -> CreateResumePDF.addSubHeader( cvJson, tools ) );
            assertDoesNotThrow( () -> CreateResumePDF.addBody( cvJson, tools ) );
        }
    }
}