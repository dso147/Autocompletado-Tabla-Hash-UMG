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

    //popup emergente
    private JPopupMenu suggestionsPopup;
    //Lista visual que contiene las sugerencias en el popup
    private JList<String> suggestionsList;
    //modelo de datos que almacena las sugerencias de forma dinamica
    private DefaultListModel<String> listModel;
    //constructor de la clase EditorUI
    //recibe el corrector ortografico y el motor de sugerencias como dependencias
    public EditorUI(SpellChecker spellChecker, SuggestionEngine suggestionEngine) {
        this.spellChecker = spellChecker;
        this.suggestionEngine = suggestionEngine;
        //se inicializa toda la interfaz grafica
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
        textArea.setFocusTraversalKeysEnabled(false); // MEJORA-TAB: Desactiva la navegación por foco con TAB, permitiendo capturar la tecla para navegación de sugerencias

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

        //se inicializa el modelo de datos para las sugerencias
        listModel = new DefaultListModel<>();
        //se crea la lista visual
        suggestionsList = new JList<>(listModel);
        //se permite seleccionar solo un elemento a l vez
        suggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //fuente de texto de la lista 
        suggestionsList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        //se crea el popop
        suggestionsPopup = new JPopupMenu();
        //se define el borde gris
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        //se agrega la lista dentro del popop con scroll
        suggestionsPopup.add(new JScrollPane(suggestionsList));

        // ATAJOS DE TECLADO

        // MEJORA-TAB: Se usa WHEN_FOCUSED para asegurar que los atajos solo funcionen cuando el textArea tiene el foco
        // Evita conflictos con otros componentes o con el sistema de navegación por defecto de Swing
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        // MEJORA-TAB: Separar InputMap y ActionMap permite un control más claro de eventos de teclado
        ActionMap actionMap = textArea.getActionMap();

        // MEJORA-TAB: Se reemplaza el uso de "TAB" string por KeyEvent.VK_TAB para mayor precisión y compatibilidad
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "nextSuggestion");

        actionMap.put("nextSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // MEJORA-TAB: Se evita ejecutar navegación si el popup no está visible o no hay sugerencias
                // Previene errores y comportamientos inesperados
                if (!suggestionsPopup.isVisible() || listModel.isEmpty()) {
                    return;
                }

                int index = suggestionsList.getSelectedIndex();

                if (index < listModel.size() - 1) {
                    suggestionsList.setSelectedIndex(index + 1);
                } else {
                    // MEJORA-TAB: Navegación circular, cuando llega al final vuelve al inicio
                    suggestionsList.setSelectedIndex(0);
                }

                // MEJORA-TAB: Asegura que el elemento seleccionado siempre sea visible dentro del
                // scroll del popup, mejorando la experiencia de usuario al navegar por sugerencias largas
                suggestionsList.ensureIndexIsVisible(suggestionsList.getSelectedIndex());
            }
        });

        // MEJORA-TAB: Uso explícito de KeyEvent para evitar conflictos con acciones por defecto de JTextArea
        // similar a la implementación de arriba.
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "chooseSuggestion");
        actionMap.put("chooseSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // MEJORA-TAB: Solo selecciona sugerencia si el popup está activo
                if (suggestionsPopup.isVisible() && !listModel.isEmpty()) {
                    seleccionarSugerencia(); // esto no se toco, pero se asegura que funcione en el evento correcto
                } else {
                    // MEJORA-TAB: Se respeta el comportamiento natural del ENTER (salto de línea)
                    // cuando no hay sugerencias activas.
                    textArea.append("\n");
                }
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
            // MEJORA-TAB: Se asegura ocultar el popup cuando no hay palabra válida.
            suggestionsPopup.setVisible(false);
            return;
        }

        boolean palabraCorrecta = spellChecker.isCorrect(ultimaPalabra);
        //Si la palabra no es correcta segun el diccionario
        if (!palabraCorrecta) {
            //se obtienen las sugerencias
            java.util.List<String> sugerencias = suggestionEngine.getSuggestions(ultimaPalabra);
            //si existen sugerencuas, se muestran en el popup
            if (!sugerencias.isEmpty()) {
                statusLabel.setText("Sugerencias para: " + ultimaPalabra); //MEJORA-TAB: Solo se agrega la alerta, no hace nada realmente.
                mostrarPopup(sugerencias);
            //si no hay, se oculta el popup
            } else {
                statusLabel.setText("No se encontraron sugerencias para: " + ultimaPalabra); //MEJORA-TAB: Solo se agrega la alerta, no hace nada realmente.
                suggestionsPopup.setVisible(false);
            }
            //si la palabra es correcta, se oculta el popup
        } else {
            statusLabel.setText("Palabra correcta: " + ultimaPalabra);
            suggestionsPopup.setVisible(false);

            //MEJORA-TAB: Se elimina el codigo que se habia descomentado, aunque no generara conflictos de funcionamiento
            //ya no pertenecia al flujo implementado.
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

    //se muestra el popup con las sugerencias
    private void mostrarPopup(java.util.List<String> sugerencias) {
        //se limpia
        listModel.clear();
        //se agregan las nuevas sugerencias
        for (String s : sugerencias) {
            listModel.addElement(s);
        }

        // MEJORA-TAB: Selección automática del primer elemento,
        // necesaria para que TAB funcione desde la primera interacción.
        if (!listModel.isEmpty()) {
            suggestionsList.setSelectedIndex(0);
        }

        try {
            Rectangle caret = textArea.modelToView(textArea.getCaretPosition());
            suggestionsPopup.show(textArea, caret.x, caret.y + 20);

            // MEJORA-TAB: Fuerza el foco de regreso al JTextArea después de mostrar el popup,
            // evitando que TAB o ENTER dejen de funcionar por pérdida de foco.
            textArea.requestFocusInWindow();
        } catch (Exception e) {
            suggestionsPopup.setVisible(false);
        }
    }

    //reemplaza la ultima palabra por la sugerencia
    private void reemplazarUltimaPalabra(String nuevaPalabra) {
        //se obtiene todo el texto del area escrita
        String texto = textArea.getText();
        
        if (texto.trim().isEmpty()) return;
        //se busca la ultima posicion de un espacio
        int lastSpace = texto.lastIndexOf(" ");
        
        String nuevoTexto;
        //si no hay espacio se reemplaza todo el texto
        if (lastSpace == -1) {
            nuevoTexto = nuevaPalabra;
        } else {
            //se conserva el texto anterior y se reemplaza solo la ultima palabra
            nuevoTexto = texto.substring(0, lastSpace + 1) + nuevaPalabra;
        }
        //se actualiza el contenido del area de texto
        textArea.setText(nuevoTexto);
        //se posiciona el cursos al final del texto
        textArea.setCaretPosition(nuevoTexto.length());
    }

    //Se gestiona la seleccion de la sugerencia
    private void seleccionarSugerencia() {
        //se obtiene la sugerencia seleccionada
        String seleccion = suggestionsList.getSelectedValue();
        //si es una seleccion valida, se reemplaza
        if (seleccion != null) {
            reemplazarUltimaPalabra(seleccion);
            //se oculta el popup despues de la seleccion
            suggestionsPopup.setVisible(false);
        }
    }

}