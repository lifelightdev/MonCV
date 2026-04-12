package life.light;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.System.Logger.Level.ERROR;

public class CVGenerator {

    private static final System.Logger logger = System.getLogger( CVGenerator.class.getName() );
    //Map<String, List<String>> skillsName = new HashMap<>();

    /*public CVGenerator() {
        skillsName.put( "Langages", new ArrayList<>() );
        skillsName.put( "Test", new ArrayList<>() );
        skillsName.put( "Frameworks", new ArrayList<>() );
        skillsName.put( "Éditeur de code", new ArrayList<>() );
        skillsName.put( "Base de données", new ArrayList<>() );
        skillsName.put( "Intégration continue", new ArrayList<>() );
        skillsName.put( "Versioning", new ArrayList<>() );
        skillsName.put( "Bug tracker", new ArrayList<>() );
        skillsName.put( "Autre outils", new ArrayList<>() );
        skillsName.put( "Systèmes d'exploitation", new ArrayList<>() );
        skillsName.put( "Architecture", new ArrayList<>() );
        skillsName.put( "Méthodologie", new ArrayList<>() );
        skillsName.put( "Postes ", new ArrayList<>() );
        skillsName.put( "Domaine fonctionnel ", new ArrayList<>() );
    }*/

    /**
     * @param templatePath Chemin du fichier Word existant (ex: "mon_cv.docx")
     * @param outputPath   Chemin du PDF à créer (ex: "resultat.pdf")
     * @param rootNode     Ton objet JSON chargé via Jackson
     */
    public void generatePDFFromWord(String templatePath, String outputPath, JsonNode rootNode) throws IOException {

        // 1. Ouvrir le document Word
        try (FileInputStream fis = new FileInputStream( templatePath ); XWPFDocument document = new XWPFDocument( fis )) {

            // 1. Traiter les paragraphes standards (hors tableaux)
            for (XWPFParagraph p : document.getParagraphs()) {
                processParagraph( p, rootNode );
            }

            // 2. Traiter les tableaux (Colonnes, encadrés, etc.)
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        // Une cellule contient elle-même des paragraphes
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            processParagraph( p, rootNode );
                        }
                    }
                }
            }

            /*for (Map.Entry<String, List<String>> entry : skillsName.entrySet()) {
                StringBuilder sb = new StringBuilder();
                String key = entry.getKey();
                List<String> details = entry.getValue();
                sb.append( key );
                sb.append( ": " );
                for (String detail : details) {
                    sb.append( detail );
                    sb.append( ", " );
                }
                logger.log( INFO, sb + "\n" );
            }*/

            // 3. Conversion en PDF
            convertToPDF( document, outputPath );
        }
    }

    private void convertToPDF(XWPFDocument document, String outputPath) throws IOException {
        // On crée un fichier temporaire pour le Word modifié
        File tempWord = File.createTempFile( "temp_cv", ".docx" );

        try (FileOutputStream out = new FileOutputStream( tempWord )) {
            document.write( out );
        }

        // Utilisation de documents4j pour transformer le Word en PDF
        // On instancie le convertisseur
        IConverter converter = LocalConverter.builder().build();
        try (InputStream is = new FileInputStream( tempWord ); OutputStream os = new FileOutputStream( outputPath )) {

            converter.convert( is ).as( DocumentType.DOCX ).to( os ).as( DocumentType.PDF ).execute();
        } catch (Exception e) {
            logger.log( ERROR, e.getMessage() );
        } finally {
            // TRÈS IMPORTANT pour documents4j : fermer le convertisseur lui-même
            // C'est ce qui évite les messages de "Shutdown hook" désordonnés
            converter.shutDown();
        }

        tempWord.delete(); // On supprime le fichier temporaire
    }

    private void processParagraph(XWPFParagraph p, JsonNode rootNode) {
        /*List<XWPFRun> paragraph = p.getRuns();

        if (!paragraph.isEmpty()) {
            for (XWPFRun pa : paragraph) {
                for (String key : skillsName.keySet()) {
                    if (!pa.text().isEmpty()) {
                        boolean isKey = paragraph.getFirst().text().toUpperCase().trim().contains( key.toUpperCase().trim() );
                        if (isKey && key.equalsIgnoreCase( "Test" )) {
                            boolean ok = paragraph.getFirst().text().toUpperCase().substring( 0, 4 ).equals( key.toUpperCase() ) && paragraph.getFirst().text().toUpperCase().charAt( 5 ) == ':';
                            if (ok) {
                                String value = pa.text();
                                value = value.replace( ",", "" ).trim();
                                if (!value.trim().isEmpty() && !value.toUpperCase().trim().equals( key.toUpperCase() )) {
                                    skillsName.get( key ).add( value );
                                }
                            }
                            isKey = false;
                        }
                        if (isKey && key.equalsIgnoreCase( "Base de données" )) {
                            if (!paragraph.getFirst().text().trim().endsWith( key.toLowerCase() + "." )) {
                                String value = pa.text();
                                value = value.replace( ",", "" ).trim();
                                if (!value.trim().isEmpty() && !value.toUpperCase().trim().equals( key.toUpperCase() )) {
                                    skillsName.get( key ).add( value );
                                }
                            }
                            isKey = false;
                        }

                        if (isKey && !pa.text().contains( key ) && !pa.text().contains( ":" )) {
                            String value = pa.text();
                            value = value.replace( ",", "" ).trim();
                            if (!value.trim().isEmpty() && !value.equalsIgnoreCase( key )) {
                                skillsName.get( key ).add( value );
                            }
                        }
                    }
                }
            }
        }*/
        List<XWPFRun> runs = new ArrayList<>( p.getRuns() ); // Copie pour éviter les erreurs de modification

        for (XWPFRun run : runs) {
            String text = run.getText( 0 );
            if (text == null) continue;

            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String target = entry.getKey(); // Ton mot-clé (ex: NOMV)

                if (text.contains( target )) {
                    // Chercher le mot entier
                    int pos = text.indexOf( target );
                    boolean isWholeWord = false;
                    while (pos >= 0) {
                        boolean leftOk = (pos == 0) || !Character.isLetterOrDigit( text.charAt( pos - 1 ) );
                        boolean rightOk = (pos + target.length() == text.length()) || !Character.isLetterOrDigit( text.charAt( pos + target.length() ) );
                        if (leftOk && rightOk) {
                            isWholeWord = true;
                            break;
                        }
                        pos = text.indexOf( target, pos + 1 );
                    }

                    if (pos != -1 && isWholeWord) {
                        JsonNode data = entry.getValue();
                        String replacement = data.get( "valeur" ).asText();
                        boolean isBold = data.get( "gras" ).asBoolean();

                        // 1. Découper le texte
                        String before = text.substring( 0, pos );
                        String after = text.substring( pos + target.length() );

                        // 2. Récupérer l'index actuel pour insérer au bon endroit
                        int runIndex = p.getRuns().indexOf( run );

                        // 3. Créer le morceau "AVANT" (Style original)
                        XWPFRun runBefore = p.insertNewRun( runIndex + 1 );
                        runBefore.setText( before );
                        runBefore.setCapitalized( false );
                        runBefore.setSmallCaps( false );
                        copyStyle( run, runBefore );

                        // 4. Créer le morceau "REMPLACEMENT" (Style GRAS ou non)
                        XWPFRun runReplace = p.insertNewRun( runIndex + 2 );
                        runReplace.setText( replacement );
                        runReplace.setBold( isBold ); // On applique le gras SEULEMENT ici
                        runReplace.setCapitalized( false );
                        runReplace.setSmallCaps( true );
                        copyStyle( run, runReplace );

                        // 5. Créer le morceau "APRÈS" (Style original)
                        XWPFRun runAfter = p.insertNewRun( runIndex + 3 );
                        runAfter.setText( after );
                        runAfter.setCapitalized( false );
                        runAfter.setSmallCaps( false );
                        copyStyle( run, runAfter );

                        // 6. Supprimer l'ancien bloc qui contenait le mélange
                        p.removeRun( runIndex );

                        // Mettre à jour le texte pour continuer la recherche si besoin
                        text = after;
                    }
                }
            }
        }
    }

    // Méthode utilitaire pour garder la même police/taille que le reste du CV
    private void copyStyle(XWPFRun source, XWPFRun target) {
        if (source.getFontFamily() != null) target.setFontFamily( source.getFontFamily() );
        if (source.getFontSizeAsDouble() != null) target.setFontSize( source.getFontSizeAsDouble() );
        if (source.getColor() != null) target.setColor( source.getColor() );
    }

}