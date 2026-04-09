package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DictionaryLoader {

    private String ruta;
    private HashMap<String, Boolean> diccionario;

    public DictionaryLoader(String route) {
        this.ruta = route;
    }

    public void cargaDiccionario() {
        diccionario = new HashMap<>();

        // si falla en la lectura, cierra el archivo automaticamente
        // por eso esta en un try el buffer reader
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
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
            System.out.println("Error al cargar el archivo: " + e.getMessage());
        }
    }

    public boolean containsKey(String palabra) {
        if (diccionario == null) {
            throw new IllegalStateException("El diccionario no ha sido cargado. Llama a cargaDiccionario() primero.");
        }
        return diccionario.containsKey(palabra.trim().toLowerCase());
    }
}
