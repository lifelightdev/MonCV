package life.light;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static life.light.CreateResumePDF.createResume;
import static life.light.CreateSkillsPDF.createSkills;

public class JsonToPdfResumeAndSkills {

    private static final Logger logger = System.getLogger(JsonToPdfResumeAndSkills.class.getName());

    static void main() {

        logger.log(Level.INFO, "Début de la génération du PDF");

        String nameFileResumeJson = "CV.json";
        String nameFileDossierCompetencesJson = "DossierCompetences.json";
        JsonNode resumeJson = null;
        JsonNode dossierCompetencesJson = null;
        try {
            resumeJson = ReadJson.getCvJson(nameFileResumeJson);
            dossierCompetencesJson = ReadJson.getCvJson(nameFileDossierCompetencesJson);

        } catch (IOException e) {
            logger.log(Level.ERROR, "Erreur lors de la lecture des fichiers JSON ", e);
            System.exit(-1);
        }

        // 2. Créer le document PDF
        createResume(resumeJson);
        createSkills(dossierCompetencesJson);
        logger.log(Level.INFO, "Fin de la génération du PDF");
    }

}
