import dictionary.DictionaryLoader;
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
        DictionaryLoader diccionario = new DictionaryLoader("src/resources/spanish.dic");
        diccionario.cargaDiccionario();
        System.out.println("¿Existe casa? " + diccionario.containsKey("casa"));
    }
    
}