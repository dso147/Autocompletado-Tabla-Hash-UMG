package ui;

import spell.SpellChecker;
import spell.SuggestionEngine;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class EditorUI extends JFrame {

    // Área donde el usuario escribe el texto
    private JTextArea textArea;

    // Panel donde se mostrarán las sugerencias (botones dinámicos)
    private JPanel suggestionsPanel;

    // Etiqueta de estado para mostrar mensajes al usuario sobre la acción realizada
    private JLabel statusLabel;

    private SpellChecker spellChecker;
    private SuggestionEngine suggestionEngine;

    private Timer debounceTimer;
    
    //CAMBIOS PARA NAVEGACION TAB !!!!!
    private JPopupMenu suggestionsPopup;
    private JList<String> suggestionsList;
    private DefaultListModel<String> listModel;

    public EditorUI(SpellChecker spellChecker, SuggestionEngine suggestionEngine) {
        this.spellChecker = spellChecker;
        this.suggestionEngine = suggestionEngine;

        loadPanel();
    }

    private void loadPanel() {
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
        //--------- bottomPanel.add(suggestionsPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Se agrega al panel principal la sección de sugerencias y estado

        // procesamiento de palabra con debounce para evitar procesar cada letra escrita, se espera a que el usuario deje de escribir por 300ms
        debounceTimer = new Timer(300, e -> procesarTextoUsuario());
        debounceTimer.setRepeats(false);

        // Evento para detectar cambios en el texto, va a simular el estado de escritura del usuario
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            // Se ejecuta cuando el usuario escribe
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextStatusLabel("Escribiendo...");
            }

            // Se ejecuta cuando borra texto
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTextStatusLabel("Editando texto...");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTextStatusLabel("Actualizando...");
            }
        });
        
        //CAMBIOS PARA EL TAB
        listModel = new DefaultListModel<>();
        suggestionsList = new JList<>(listModel);
        suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionsList.setFont(new Font("SansSerif", Font.PLAIN, 16));

        suggestionsPopup = new JPopupMenu();
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionsPopup.add(new JScrollPane(suggestionsList));
        
        // ATAJOS DE TECLADO
        textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "nextSuggestion");
        textArea.getActionMap().put("nextSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = suggestionsList.getSelectedIndex();
                if (index < listModel.size() - 1) {
                    suggestionsList.setSelectedIndex(index + 1);
                }
            }
        });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "chooseSuggestion");
        textArea.getActionMap().put("chooseSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarSugerencia();
            }
        });

        // CLICK CON MOUSE
        suggestionsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarSugerencia();
            }
        });
        
    }

    private void updateTextStatusLabel(String mensaje) {
        statusLabel.setText(mensaje);
        debounceTimer.restart();
    }

    private void procesarTextoUsuario() {
        String texto = textArea.getText();
        String ultimaPalabra = obtenerUltimaPalabra(texto);

        System.out.println("Texto: " + texto);
        System.out.println("Palabra: " + ultimaPalabra);

        //limpiarSugerencias();

        if (ultimaPalabra.isEmpty()) {
            statusLabel.setText("Escribe una palabra...");
            return;
        }

        boolean palabraCorrecta = spellChecker.isCorrect(ultimaPalabra);
        
        if (!palabraCorrecta) {
            java.util.List<String> sugerencias = suggestionEngine.getSuggestions(ultimaPalabra);

            if (!sugerencias.isEmpty()) {
                mostrarPopup(sugerencias);
            } else {
                suggestionsPopup.setVisible(false);
            }
        } else {
            suggestionsPopup.setVisible(false);
        }

                    if (palabraCorrecta) {
                statusLabel.setText("Palabra correcta: " + ultimaPalabra);
            } else {
                java.util.List<String> sugerencias = suggestionEngine.getSuggestions(ultimaPalabra);

                System.out.println("Sugerencias: " + sugerencias);

                // Esta parte no afecto la actualización de texto, solo fue demo.

                if (sugerencias.isEmpty()) {
                    statusLabel.setText("No se encontraron sugerencias para: " + ultimaPalabra);
                } else {
                    for (String sugerencia : sugerencias) {
                        JButton botonSugerencia = new JButton(sugerencia);
                        botonSugerencia.setFocusPainted(false);

                        botonSugerencia.addActionListener(e -> {
                            statusLabel.setText("Seleccionaste: " + sugerencia);
                        });

                        suggestionsPanel.add(botonSugerencia);
                    }
                }
            }

        suggestionsPanel.revalidate();
        suggestionsPanel.repaint();
    }

    private String obtenerUltimaPalabra(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }

        String textoLimpio = texto.trim();
        String[] palabras = textoLimpio.split("\\s+");

        return palabras[palabras.length - 1];
    }

    private void limpiarSugerencias() {
        suggestionsPanel.removeAll();
        suggestionsPanel.revalidate();
        suggestionsPanel.repaint();
    }

    //CAMBIOS PARA EL TAB

    private void mostrarPopup(java.util.List<String> sugerencias) {
        listModel.clear();

        for (String s : sugerencias) {
            listModel.addElement(s);
        }

        suggestionsList.setSelectedIndex(0);

        try {
            Rectangle caret = textArea.modelToView(textArea.getCaretPosition());
            suggestionsPopup.show(textArea, caret.x, caret.y + 20);
        } catch (Exception e) {
            suggestionsPopup.setVisible(false);
        }
    }

    private void reemplazarUltimaPalabra(String nuevaPalabra) {
        String texto = textArea.getText();

        if (texto.trim().isEmpty()) return;

        int lastSpace = texto.lastIndexOf(" ");

        String nuevoTexto;

        if (lastSpace == -1) {
            nuevoTexto = nuevaPalabra;
        } else {
            nuevoTexto = texto.substring(0, lastSpace + 1) + nuevaPalabra;
        }

        textArea.setText(nuevoTexto);
        textArea.setCaretPosition(nuevoTexto.length());
    }

    private void seleccionarSugerencia() {
    String seleccion = suggestionsList.getSelectedValue();

        if (seleccion != null) {
            reemplazarUltimaPalabra(seleccion);
            suggestionsPopup.setVisible(false);
        }
    }

}