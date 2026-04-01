package life.light;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static life.light.CreatePDF.creatPDF;

public class JsonToPdfCV {

    private static final Logger logger = System.getLogger(JsonToPdfCV.class.getName());

    static void main() {

        logger.log(Level.INFO, "Début de la génération du PDF");

        String nameFileCVJson = "cv.json";
        JsonNode cvJson = null;
        try {
            cvJson = ReadJson.getCvJson(nameFileCVJson);
        } catch (IOException e) {
            logger.log(Level.ERROR, "Erreur lors de la lecture du JSON", e.getMessage());
            System.exit(-1);
        }

        // 2. Créer le document PDF
        creatPDF(cvJson);
        logger.log(Level.INFO, "Fin de la génération du PDF");
    }

}
