import dictionary.DictionaryLoader;
import java.util.HashMap;
//import ui.EditorUI;

//import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        /*SwingUtilities.invokeLater(() -> {
            EditorUI ui = new EditorUI();
            ui.setVisible(true);
        });*/
        /*HashMap<String, Boolean> diccionario =
                DictionaryLoader.cargarDiccionario();*/
        /*DictionaryLoader diccionario = new DictionaryLoader();
        dic.cargarDiccionario();*/
        HashMap<String, Boolean> diccionario =
                DictionaryLoader.cargarDiccionario("resources/spanish.dic");

        System.out.println("¿Existe casa? " + diccionario.containsKey("casa"));
    }
    
}