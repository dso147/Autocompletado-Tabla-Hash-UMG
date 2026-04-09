package ui;

import dictionary.DictionaryLoader;
import spell.SpellChecker;
import spell.SuggestionEngine;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class EditorUI extends JFrame {

    // Área donde el usuario escribe el texto
    private final JTextArea textArea;

    // Panel donde se mostrarán las sugerencias (botones dinámicos)
    private final JPanel suggestionsPanel;

    // Etiqueta de estado para mostrar mensajes al usuario sobre la acción realizada
    private final JLabel statusLabel;

    public EditorUI(SpellChecker spellChecker, SuggestionEngine suggestionEngine) {

        // Configuración básica de la ventana principal
        setTitle("Autocompletado y Corrección Ortográfica");
        setSize(800, 550);
        setLocationRelativeTo(null);  // centra la ventana en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        // Panel principal con padding interno, aca se ingresan los elementos de la vista
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Título superior de ventana
        JLabel titleLabel = new JLabel("Editor de texto", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        mainPanel.add(titleLabel, BorderLayout.NORTH); // se añade el título al panel principal

        // Área de texto donde el usuario escribe
        textArea = new JTextArea();
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        textArea.setLineWrap(true); // salto de línea automático
        textArea.setWrapStyleWord(true); // para corta por palabra y no por letra
        textArea.setMargin(new Insets(15, 15, 15, 15)); // padding interno de área de texto

        // Scroll para el área de texto
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ingreso de texto"));
        mainPanel.add(scrollPane, BorderLayout.CENTER); // se añade el área de texto con scroll al panel principal

        // Segundo bloque de editor, aquí se mostrarán las sugerencias y el estado de escritura

        // Label inferior que indica el estado de escritura del usuario
        statusLabel = new JLabel("Escribe una palabra...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel donde se mostrarán las sugerencias
        suggestionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        suggestionsPanel.setBorder(BorderFactory.createTitledBorder("Sugerencias"));

        // Panel inferior que contiene estado y sugerencias
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(suggestionsPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Se agrega al panel principal la sección de sugerencias y estado

        // Agrega botones de ejemplo
        addMockSuggestionButtons();

        // Evento para detectar cambios en el texto, va a simular el estado de escritura del usuario
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            // Se ejecuta cuando el usuario escribe
            @Override
            public void insertUpdate(DocumentEvent e) {
                statusLabel.setText("Escribiendo...");
            }

            // Se ejecuta cuando borra texto
            @Override
            public void removeUpdate(DocumentEvent e) {
                statusLabel.setText("Editando texto...");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // No se usa para JTextArea, pero es necesario implementarlo
            }
        });
    }

    /**
     * Método temporal para simular sugerencias
     */
    private void addMockSuggestionButtons() {

        // Ejemplos de sugerencias
        String[] ejemplos = {"Hola", "Holanda", "Cómo", "Comida", "Comedor"};

        for (String texto : ejemplos) {

            // Crear botón por cada sugerencia
            JButton button = new JButton(texto);
            button.setFocusPainted(false);

            // Acción click en boton simulado, muestra el cambio en el campo de estado
            button.addActionListener(e ->
                    statusLabel.setText("Seleccionaste: " + texto)
            );

            // Agregar botón al panel de sugerencias
            suggestionsPanel.add(button);
        }
    }
}