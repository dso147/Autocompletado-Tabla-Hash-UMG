package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DictionaryLoader {
    public static HashMap<String, Boolean> cargarDiccionario(String ruta) {
        HashMap<String, Boolean> diccionario = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            br.readLine(); // omitir primera línea

            String palabra;
            while ((palabra = br.readLine()) != null) {
                palabra = palabra.trim().toLowerCase();

                if (!palabra.isEmpty()) {
                    diccionario.put(palabra, true);
                }
            }

            System.out.println("Diccionario cargado correctamente");
            System.out.println("Total de palabras: " + diccionario.size());

        } catch (IOException e) {
            System.out.println("Error al cargar el archivo");
            e.printStackTrace();
        }

        return diccionario;
    }
}
