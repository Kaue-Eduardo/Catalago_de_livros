package Gui;

import Modelo.Autor;
import Modelo.Livro;
import org.bson.types.ObjectId; // Para lidar com IDs de autores
import Persistencia.AutorDAO; // Para buscar a lista de autores


import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Janela de diálogo para adicionar ou editar um Livro.
 */
public class DialogAdicionarEditarLivro extends JDialog {

    private JTextField txtTitulo;
    private JComboBox<Integer> comboAnoPublicacao;
    private JComboBox<String> comboGenero;
    private JList<Autor> listAutores;
    private DefaultListModel<Autor> listModelAutores;
    private JTextField txtLinkConteudo;
    private JCheckBox chkLido;         // NOVO CAMPO
    private JComboBox<Integer> comboNota; // NOVO CAMPO

    private JButton btnSalvar;
    private JButton btnCancelar;

    private Livro livroAtual;
    private boolean salvo;

    private AutorDAO autorDAO;

    private static final String[] GENEROS_PREDEFINIDOS = {
            "Ficção Científica", "Fantasia", "Romance", "Suspense", "Drama",
            "Aventura", "Biografia", "História", "Técnico", "Poesia", "Outro"
    };
    private static final Integer[] NOTAS_POSSIVEIS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public DialogAdicionarEditarLivro(Frame owner, Livro livroParaEditar, AutorDAO autorDAO) {
        super(owner, true);
        this.livroAtual = livroParaEditar;
        this.autorDAO = autorDAO;
        this.salvo = false;

        setTitle(livroAtual == null ? "Adicionar Novo Livro" : "Editar Livro");
        initComponents();
        layoutComponents();
        addListeners();

        if (livroAtual != null) {
            preencherCampos();
        }

        pack();
        setMinimumSize(new Dimension(600, 550)); // Aumentei um pouco o tamanho
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        txtTitulo = new JTextField(30);

        comboAnoPublicacao = new JComboBox<>();
        int anoCorrente = LocalDate.now().getYear();
        for (int i = anoCorrente + 1; i >= 1000; i--) {
            comboAnoPublicacao.addItem(i);
        }
        comboAnoPublicacao.setSelectedItem(anoCorrente);

        comboGenero = new JComboBox<>(GENEROS_PREDEFINIDOS);
        comboGenero.setSelectedIndex(0); // Seleciona o primeiro gênero por padrão

        listModelAutores = new DefaultListModel<>();
        List<Autor> autoresDisponiveis = autorDAO.listarTodosAutores();
        for (Autor autor : autoresDisponiveis) {
            listModelAutores.addElement(autor);
        }
        listAutores = new JList<>(listModelAutores);
        listAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAutores.setVisibleRowCount(4); // Reduzi um pouco para caber mais campos

        txtLinkConteudo = new JTextField(30);
        chkLido = new JCheckBox("Marcado como Lido"); // INICIALIZA CHECKBOX
        comboNota = new JComboBox<>(NOTAS_POSSIVEIS); // INICIALIZA COMBO NOTA
        comboNota.setSelectedIndex(0); // Seleciona nota 0 por padrão

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int linha = 0;
        // Linha 0: Título
        gbc.gridx = 0; gbc.gridy = linha; panel.add(new JLabel("Título*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(txtTitulo, gbc); gbc.weightx = 0;
        linha++;

        // Linha 1: Ano de Publicação
        gbc.gridx = 0; gbc.gridy = linha; panel.add(new JLabel("Ano Publicação*:"), gbc);
        gbc.gridx = 1; panel.add(comboAnoPublicacao, gbc);
        linha++;

        // Linha 2: Gênero
        gbc.gridx = 0; gbc.gridy = linha; panel.add(new JLabel("Gênero*:"), gbc);
        gbc.gridx = 1; panel.add(comboGenero, gbc);
        linha++;

        // Linha 3: Link do Conteúdo
        gbc.gridx = 0; gbc.gridy = linha; panel.add(new JLabel("Link Conteúdo*:"), gbc);
        gbc.gridx = 1; panel.add(txtLinkConteudo, gbc);
        linha++;

        // Linha 4: Lido e Nota (NOVA LINHA)
        gbc.gridx = 0; gbc.gridy = linha; panel.add(new JLabel("Status/Nota*:"), gbc);
        JPanel panelLidoNota = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Layout para agrupar
        panelLidoNota.add(chkLido);
        panelLidoNota.add(new JLabel("  Nota (0-10):")); // Espaçamento
        panelLidoNota.add(comboNota);
        gbc.gridx = 1; panel.add(panelLidoNota, gbc);
        linha++;
        
        // Linha 5: Autores
        gbc.gridx = 0; gbc.gridy = linha; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Autores*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        panel.add(new JScrollPane(listAutores), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        linha++;

        // Linha 6: Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(panelBotoes, gbc);

        add(panel, BorderLayout.CENTER);
        add(new JLabel(" * Campos obrigatórios", SwingConstants.CENTER), BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnSalvar.addActionListener(e -> salvarLivro());
        btnCancelar.addActionListener(e -> {
            salvo = false;
            dispose();
        });
    }

    private void preencherCampos() {
        if (livroAtual != null) {
            txtTitulo.setText(livroAtual.getTitulo());
            comboAnoPublicacao.setSelectedItem(livroAtual.getAnoPublicacao());
            comboGenero.setSelectedItem(livroAtual.getGenero());
            txtLinkConteudo.setText(livroAtual.getLinkConteudo() != null ? livroAtual.getLinkConteudo() : "");
            chkLido.setSelected(livroAtual.isLido()); // PREENCHE LIDO
            comboNota.setSelectedItem(livroAtual.getNota()); // PREENCHE NOTA

            if (livroAtual.getAutoresCompletos() != null && !livroAtual.getAutoresCompletos().isEmpty()) {
                // ... (lógica de seleção de autores na JList permanece a mesma) ...
                List<Integer> indicesParaSelecionar = new ArrayList<>();
                for (Autor autorDoLivro : livroAtual.getAutoresCompletos()) {
                    for (int i = 0; i < listModelAutores.getSize(); i++) {
                        if (listModelAutores.getElementAt(i).equals(autorDoLivro)) {
                            indicesParaSelecionar.add(i);
                            break;
                        }
                    }
                }
                listAutores.setSelectedIndices(indicesParaSelecionar.stream().mapToInt(Integer::intValue).toArray());
            }
        }
    }

    private void salvarLivro() {
        String titulo = txtTitulo.getText().trim();
        Integer anoPublicacao = (Integer) comboAnoPublicacao.getSelectedItem();
        String genero = (String) comboGenero.getSelectedItem();
        String linkConteudo = txtLinkConteudo.getText().trim();
        boolean lido = chkLido.isSelected(); // OBTÉM LIDO
        Integer nota = (Integer) comboNota.getSelectedItem(); // OBTÉM NOTA
        List<Autor> autoresSelecionados = listAutores.getSelectedValuesList();

        if (titulo.isEmpty()) { /*...*/ JOptionPane.showMessageDialog(this, "O campo 'Título' é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE); txtTitulo.requestFocus(); return; }
        if (anoPublicacao == null) { /*...*/ JOptionPane.showMessageDialog(this, "Selecione o 'Ano de Publicação'.", "Erro", JOptionPane.ERROR_MESSAGE); comboAnoPublicacao.requestFocus(); return; }
        if (genero == null || genero.trim().isEmpty()) { /*...*/ JOptionPane.showMessageDialog(this, "Selecione o 'Gênero'.", "Erro", JOptionPane.ERROR_MESSAGE); comboGenero.requestFocus(); return; }
        if (linkConteudo.isEmpty()) { /*...*/ JOptionPane.showMessageDialog(this, "O campo 'Link Conteúdo' é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE); txtLinkConteudo.requestFocus(); return; }
        if (nota == null) { // Validação para Nota, se o JComboBox pudesse ter null (não é o caso aqui com Integers)
             JOptionPane.showMessageDialog(this, "Selecione uma 'Nota'.", "Erro", JOptionPane.ERROR_MESSAGE);
             comboNota.requestFocus(); return;
        }
        if (autoresSelecionados.isEmpty()) { /*...*/ JOptionPane.showMessageDialog(this, "Selecione pelo menos um 'Autor'.", "Erro", JOptionPane.ERROR_MESSAGE); listAutores.requestFocus(); return; }

        List<ObjectId> idsAutoresSelecionados = autoresSelecionados.stream()
                                                                  .map(Autor::getId)
                                                                  .collect(Collectors.toList());
        if (this.livroAtual == null) {
            this.livroAtual = new Livro();
        }
        this.livroAtual.setTitulo(titulo);
        this.livroAtual.setAnoPublicacao(anoPublicacao);
        this.livroAtual.setGenero(genero);
        this.livroAtual.setLinkConteudo(linkConteudo);
        this.livroAtual.setLido(lido);     // SALVA LIDO
        this.livroAtual.setNota(nota);       // SALVA NOTA
        this.livroAtual.setAutoresIds(idsAutoresSelecionados);
        this.livroAtual.setAutoresCompletos(new ArrayList<>(autoresSelecionados));

        this.salvo = true;
        dispose();
    }

    public Livro getLivro() { return salvo ? livroAtual : null; }
    public boolean isSalvo() { return salvo; }
}