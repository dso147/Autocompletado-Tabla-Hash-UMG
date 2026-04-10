import dictionary.DictionaryLoader;
import spell.SpellChecker;
import spell.SuggestionEngine;
import ui.EditorUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Se crea una instancia de DictionaryLoader y se le indica la ruta del archivo del diccionario
        DictionaryLoader diccionario = new DictionaryLoader("src/resources/spanish.dic");
        diccionario.cargaDiccionario();
        
        //Se crea el verificador ortografico utilizando el diccionario cargado
        SpellChecker spellChecker = new SpellChecker(diccionario);
        //Se crea el motor de sugerencias basado en el diccionario
        SuggestionEngine suggestionEngine = new SuggestionEngine(diccionario);
        
       // Se asegura que la interfaz gráfica se ejecute en el hilo de eventos de Swing //Se asegu
        SwingUtilities.invokeLater(() -> {
            //Se crea la interfaz del editor de texto
            EditorUI ui = new EditorUI(spellChecker, suggestionEngine);
            //Se hace visible la interfaz gráfica
            ui.setVisible(true);
        });
    }
    
}