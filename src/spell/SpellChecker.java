package spell;

import dictionary.DictionaryLoader;

public class SpellChecker {

    // Atributo que representa el diccionario utilizado para validar palabras
    private final DictionaryLoader diccionario;

    // Constructor de la clase, recibe un diccionario como parámetro
    public SpellChecker(DictionaryLoader diccionario) {
        this.diccionario = diccionario;
    }
    // Método que verifica si una palabra es correcta según el diccionario
    public boolean isCorrect(String palabra) {
        //Se valida que la palabra no se nula ni este vacia
        if (palabra == null || palabra.trim().isEmpty()) {
            return false;
        }
        // Se consulta si la palabra existe dentro del diccionario
        return diccionario.containsKey(palabra);
    }
}