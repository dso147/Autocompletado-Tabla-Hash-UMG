package spell;

import dictionary.DictionaryLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SuggestionEngine {

    // Cantidad máxima de sugerencias que se devolverán al usuario.
    private static final int MAX_SUGGESTIONS = 10;

    // Referencia al diccionario cargado en memoria.
    // verificar O(1) si una palabra existe o no.
    private final DictionaryLoader diccionario;

    public SuggestionEngine(DictionaryLoader diccionario) {
        this.diccionario = diccionario;
    }

    public List<String> getSuggestions(String palabra) {
        // LinkedHashSet evita duplicados y conserva orden de sugerencias.
        Set<String> suggestions = new LinkedHashSet<>();

        if (palabra == null) {
            return new ArrayList<>();
        }

        // Normaliza la palabra para trabajar siempre en minúsculas
        // y sin espacios al inicio o al final.
        String word = palabra.trim().toLowerCase();

        // Si después de limpiar queda vacía, tampoco se procesa.
        if (word.isEmpty()) return new ArrayList<>();

        // Si existe en diccionario no hace nada más
        if (diccionario.containsKey(word)) {
            return new ArrayList<>();
        }

        // Cuando se llenan las LinkedHashSet suggestions se retornan

        // Primero intenta generar sugerencias eliminando letras.
        generateByDeletion(word, suggestions);
        if (isFull(suggestions)) {
            return toList(suggestions);
        }

        // Generar sugerencias reemplazando letras.
        generateByReplacement(word, suggestions);
        if (isFull(suggestions)) {
            return toList(suggestions);
        }

        // Generar suerencias  insertando letras.
        generateByInsertion(word, suggestions);

        return toList(suggestions);
    }

    /*
     * Genera sugerencias eliminando una letra en cada posición.
     * Ejemplo:
     * palabra = "cassa"
     * candidatos:
     * "assa", "cssa", "casa", "casa", "cass"
     * Si alguno existe en el diccionario, se agrega como sugerencia
     */
    private void generateByDeletion(String word, Set<String> suggestions) {
        for (int i = 0; i < word.length(); i++) {
            // si se llena, termina el proceso
            if (isFull(suggestions)) {
                return;
            }

            // nueva palabra quitando el carácter en la posición i
            String candidate = word.substring(0, i) + word.substring(i + 1);

            // Si la sugerencia existe en el diccionario, se agrega
            if (diccionario.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }
    }

    /*
     * Genera sugerencias reemplazando una letra por otra
     * Se prueban:
     * - letras de la a a la z
     * - letras extra del español: á, é, í, ó, ú, ü, ñ
     * Ejemplo:
     * palabra = "caza"
     * podría generar "casa", "cava", "cara", etc
     */
    private void generateByReplacement(String word, Set<String> suggestions) {
        for (int i = 0; i < word.length(); i++) {

            // se intenta reemplazar el carácter actual por letras de a-z.
            for (char c = 'a'; c <= 'z'; c++) {

                //si esta lleno retorna
                if (isFull(suggestions)) {
                    return;
                }

                //si la letra es igual, salta la posición
                if (word.charAt(i) == c) {
                    continue;
                }

                // Construye una nueva palabra sustituyendo el carácter en la posición i
                String candidate = word.substring(0, i) + c + word.substring(i + 1);

                // Si existe en el diccionario se agrega
                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }

            // se prueban reemplazos con letras acentuadas
            for (char c : getExtraLetters()) {
                if (isFull(suggestions)) return;

                if (word.charAt(i) == c) continue;

                String candidate = word.substring(0, i) + c + word.substring(i + 1);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }
        }
    }

    /*
     * Genera sugerencias insertando una letra en cada posición posible.
     * Se recorre desde la posición 0 hasta el final de la palabra,
     * incluyendo la posibilidad de insertar al final.
     * Ejemplo:
     * palabra = "csa"
     * insertando 'a' en la posición correcta podría dar "casa".
     */
    private void generateByInsertion(String word, Set<String> suggestions) {
        // Se usa <= porque también se considera insertar al final de la palabra.
        for (int i = 0; i <= word.length(); i++) {

            // Inserta letras de la a a la z
            for (char c = 'a'; c <= 'z'; c++) {
                if (isFull(suggestions)) {
                    return;
                }

                // Construye una nueva palabra insertando el carácter en la posición i.
                String candidate = word.substring(0, i) + c + word.substring(i);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }

            // se prueban reemplazos con letras acentuadas
            for (char c : getExtraLetters()) {
                if (isFull(suggestions)) return;

                String candidate = word.substring(0, i) + c + word.substring(i);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }
        }
    }

    /*
     * Verifica si ya se alcanzó el límite máximo de sugerencias.
     */
    private boolean isFull(Set<String> suggestions) {
        return suggestions.size() >= MAX_SUGGESTIONS;
    }

    /*
     * Convierte sugerencias a una lista, para estandarizar respuesta a UI.
     */
    private List<String> toList(Set<String> suggestions) {
        return new ArrayList<>(suggestions);
    }

    /*
     * Devuelve letras adicionales acentuadas
     */
    private char[] getExtraLetters() {

        return new char[]{'á', 'é', 'í', 'ó', 'ú', 'ü', 'ñ'};
    }
}