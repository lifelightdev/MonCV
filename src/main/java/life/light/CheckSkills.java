package life.light;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CheckSkills {

    private void processParagraph(XWPFParagraph p, Set<String> globalLanguages, Set<String> allGlobalSkills, Set<String> allMissionTools) {
        String text = p.getText().trim();
        if (text.isEmpty()) return;

        // 1. Extract Global Skills
        if (text.toUpperCase().startsWith( "Langages".toUpperCase() )) {
            extractGlobalSkills( text, globalLanguages );
            extractGlobalSkills( text, allGlobalSkills );
        } /*else if (text.toUpperCase().startsWith( "TEST" ) ||
                text.toUpperCase().startsWith( "FRAMEWORKS" ) ||
                text.toUpperCase().startsWith( "ÉDITEUR" ) ||
                text.toUpperCase().startsWith( "BASE DE DONNÉES" ) ||
                text.toUpperCase().startsWith( "INTÉGRATION CONTINUE" ) ||
                text.toUpperCase().startsWith( "VERSIONING" ) ||
                text.toUpperCase().startsWith( "BUG TRACKER" ) ||
                text.toUpperCase().startsWith( "AUTRE OUTILS" ) ||
                text.toUpperCase().startsWith( "SYSTÈMES D’EXPLOITATION" ) ||
                text.toUpperCase().startsWith( "ARCHITECTURE" ) ||
                text.toUpperCase().startsWith( "MÉTHODOLOGIE" )) {
            extractGlobalSkills( text, allGlobalSkills );
        }*/

        // 2. Extract Mission Tools
        if (nextIsTechnique) {
            String[] tools = text.split( "," );
            for (String t : tools) {
                if (t.endsWith( "." )) t = t.substring( 0, t.length() - 1 );
                allMissionTools.add( t );
            }

            nextIsTechnique = false;
        }

        if (text.equalsIgnoreCase( "TECHNIQUE" )) {
            nextIsTechnique = true;
        }
    }

    boolean nextIsTechnique = false;

    public void testCheckLanguagesCoherence() throws IOException {
        Set<String> globalLanguages = new HashSet<>();
        Set<String> allGlobalSkills = new HashSet<>();
        Set<String> allMissionTools = new HashSet<>();
        StringBuilder out = new StringBuilder();

        try (FileInputStream fis = new FileInputStream( "Dossier de compétences.docx" );
             XWPFDocument document = new XWPFDocument( fis )) {

            for (XWPFParagraph p : document.getParagraphs()) {
                processParagraph( p, globalLanguages, allGlobalSkills, allMissionTools );
            }

            for (org.apache.poi.xwpf.usermodel.XWPFTable table : document.getTables()) {
                for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            processParagraph( p, globalLanguages, allGlobalSkills, allMissionTools );
                        }
                    }
                }
            }
        }

        out.append( "--- Langages déclarés globalement ---\n" );
        out.append( globalLanguages ).append( "\n\n" );

        // 3. Compare Global Languages with Mission Tools
        Set<String> missionLanguages = new HashSet<>();
        for (String tool : allMissionTools) {
            // Find tools that match a global language (case-insensitive)
            for (String gl : globalLanguages) {
                if (tool.equalsIgnoreCase( gl ) || tool.toLowerCase().contains( gl.toLowerCase() )) {
                    missionLanguages.add( gl );
                }
            }
        }

        out.append( "--- Langages déclarés globalement et retrouvés dans les missions ---\n" );
        out.append( missionLanguages ).append( "\n\n" );

        Set<String> inGlobalNotMission = new HashSet<>( globalLanguages );
        inGlobalNotMission.removeAll( missionLanguages );

        out.append( "--- Langages dans le récapitulatif mais absents des missions ---\n" );
        if (inGlobalNotMission.isEmpty()) {
            out.append( "Aucun. C'est parfait !\n" );
        } else {
            out.append( inGlobalNotMission ).append( "\n" );
            out.append( "(Peut-être s'agit-il d'expériences plus anciennes ?)\n" );
        }
        out.append( "\n" );

        // Find tools in missions that are not in ANY global list
        Set<String> toolsNotInGlobal = new HashSet<>();
        for (String tool : allMissionTools) {
            boolean found = false;
            for (String gs : allGlobalSkills) {
                if (tool.equalsIgnoreCase( gs ) || tool.toLowerCase().contains( gs.toLowerCase() ) || gs.toLowerCase().contains( tool.toLowerCase() )) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toolsNotInGlobal.add( tool );
            }
        }

        out.append( "--- Outils/Langages dans les missions mais absents de TOUT le récapitulatif global ---\n" );
        if (toolsNotInGlobal.isEmpty()) {
            out.append( "Aucun. C'est parfait !\n" );
        } else {
            out.append( toolsNotInGlobal ).append( "\n" );
            out.append( "(Il faudrait peut-être les ajouter dans Langages, Frameworks, ou Autres Outils)\n" );
        }

        System.out.println( out );
    }

    private void extractGlobalSkills(String text, Set<String> targetSet) {
        text = text.replace( '\u00A0', ' ' );
        int colonIndex = text.indexOf( ':' );
        if (colonIndex == -1) colonIndex = text.indexOf( ' ' );

        if (colonIndex != -1 && text.substring( 0, colonIndex ).trim().toUpperCase().matches( "^[A-ZÉÈÊ ]+$" )) {
            String rightPart = text.substring( colonIndex + 1 ).trim();
            if (rightPart.startsWith( ":" )) rightPart = rightPart.substring( 1 ).trim();
            String[] skills = rightPart.split( "," );
            for (String s : skills) {
                targetSet.add( s.trim() );
            }
        }
    }
}
