import dictionary.DictionaryLoader;
import spell.SpellChecker;
import spell.SuggestionEngine;
import ui.EditorUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DictionaryLoader diccionario = new DictionaryLoader("src/resources/spanish.dic");
        diccionario.cargaDiccionario();

        SpellChecker spellChecker = new SpellChecker(diccionario);
        SuggestionEngine suggestionEngine = new SuggestionEngine(diccionario);

        SwingUtilities.invokeLater(() -> {
            EditorUI ui = new EditorUI(spellChecker, suggestionEngine);
            ui.setVisible(true);
        });
    }
    
}