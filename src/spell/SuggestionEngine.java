package spell;

import dictionary.DictionaryLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SuggestionEngine {

    private final SpellChecker spellChecker;
    private final DictionaryLoader diccionario;

    public SuggestionEngine(SpellChecker spellChecker, DictionaryLoader diccionario) {
        this.spellChecker = spellChecker;
        this.diccionario = diccionario;
    }

    public List<String> getSuggestions(String palabra) {
        Set<String> suggestions = new LinkedHashSet<>();

        if (palabra == null) {
            return new ArrayList<>(suggestions);
        }

        String word = palabra.trim().toLowerCase();

        if (word.isEmpty()) {
            return new ArrayList<>(suggestions);
        }

        if (spellChecker.isCorrect(word)) {
            suggestions.add(word);
            return new ArrayList<>(suggestions);
        }

        generateByDeletion(word, suggestions);
        generateByReplacement(word, suggestions);
        generateByInsertion(word, suggestions);

        return new ArrayList<>(suggestions);
    }

    private void generateByDeletion(String word, Set<String> suggestions) {
        for (int i = 0; i < word.length(); i++) {
            String candidate = word.substring(0, i) + word.substring(i + 1);

            if (diccionario.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }
    }

    private void generateByReplacement(String word, Set<String> suggestions) {
        for (int i = 0; i < word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (word.charAt(i) == c) {
                    continue;
                }

                String candidate = word.substring(0, i) + c + word.substring(i + 1);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }

            for (char c : getExtraLetters()) {
                if (word.charAt(i) == c) {
                    continue;
                }

                String candidate = word.substring(0, i) + c + word.substring(i + 1);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }
        }
    }

    private void generateByInsertion(String word, Set<String> suggestions) {
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String candidate = word.substring(0, i) + c + word.substring(i);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }

            for (char c : getExtraLetters()) {
                String candidate = word.substring(0, i) + c + word.substring(i);

                if (diccionario.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
            }
        }
    }

    private char[] getExtraLetters() {
        return new char[]{'á', 'é', 'í', 'ó', 'ú', 'ü', 'ñ'};
    }
}