package spell;

import dictionary.DictionaryLoader;

public class SpellChecker {

    private final DictionaryLoader diccionario;

    public SpellChecker(DictionaryLoader diccionario) {
        this.diccionario = diccionario;
    }

    public boolean isCorrect(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return false;
        }

        return diccionario.containsKey(palabra);
    }
}