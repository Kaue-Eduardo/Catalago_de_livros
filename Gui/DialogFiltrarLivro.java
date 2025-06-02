package Gui;

import Modelo.Autor;
import Modelo.Livro;
import Persistencia.AutorDAO;
import Persistencia.LivroDAO;
import java.util.stream.Collectors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * Janela de diálogo para buscar e selecionar um Livro.
 * Pode ser usada para a busca principal ou antes de operações de alteração/exclusão.
 */
public class DialogFiltrarLivro extends JDialog {

    private JTextField txtTituloFiltro;
    private JComboBox<Autor> comboAutorFiltro;
    private JComboBox<String> comboGeneroFiltro;
    private JTextField txtAnoInicioFiltro; // NOVO CAMPO
    private JTextField txtAnoFimFiltro;   // NOVO CAMPO
    private JButton btnBuscar;
    private JButton btnLimparFiltro;

    private JList<Livro> listResultadoLivros;
    private DefaultListModel<Livro> listModelResultado;

    private JButton btnAcao;
    private JButton btnCancelar;

    private LivroDAO livroDAO;
    private AutorDAO autorDAO;
    private Livro livroSelecionado;
    private String tipoDialogo;

    private static final String OPCAO_TODOS_AUTORES = "Todos os Autores";
    private static final String OPCAO_TODOS_GENEROS = "Todos os Gêneros";

    /**
     * Construtor.
     * @param owner O Frame proprietário.
     * @param livroDAO DAO para buscar livros.
     * @param autorDAO DAO para buscar autores (para o filtro).
     * @param tipoDialogo Indica o propósito do diálogo: "BUSCA_PRINCIPAL", "ALTERAR", ou "EXCLUIR".
     */
    public DialogFiltrarLivro(Frame owner, LivroDAO livroDAO, AutorDAO autorDAO, String tipoDialogo) {
        super(owner, "Buscar Livro", true);
        this.livroDAO = livroDAO;
        this.autorDAO = autorDAO;
        this.tipoDialogo = tipoDialogo;
        this.livroSelecionado = null;

        initComponents();
        layoutComponents();
        addListeners();
        configurarParaTipoDialogo();

        pack();
        setMinimumSize(new Dimension(650, 500)); // Aumentei um pouco a largura mínima
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        txtTituloFiltro = new JTextField(20);

        comboAutorFiltro = new JComboBox<>();
        comboAutorFiltro.addItem(null); // Representa "Todos os Autores"
        List<Autor> autores = autorDAO.listarTodosAutores();
        for(Autor autor : autores) {
            comboAutorFiltro.addItem(autor);
        }
        comboAutorFiltro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText(OPCAO_TODOS_AUTORES);
                } else if (value instanceof Autor) {
                    setText(((Autor) value).getNome());
                }
                return this;
            }
        });
        comboAutorFiltro.setSelectedItem(null);

        comboGeneroFiltro = new JComboBox<>();
        comboGeneroFiltro.addItem(OPCAO_TODOS_GENEROS);
        String[] generosDefinidos = {
            "Ficção Científica", "Fantasia", "Romance", "Suspense", "Drama",
            "Aventura", "Biografia", "História", "Técnico", "Poesia", "Outro"
        };
        for (String genero : generosDefinidos) {
            comboGeneroFiltro.addItem(genero);
        }
        comboGeneroFiltro.setSelectedItem(OPCAO_TODOS_GENEROS);

        txtAnoInicioFiltro = new JTextField(5); // NOVO CAMPO
        txtAnoFimFiltro = new JTextField(5);   // NOVO CAMPO

        btnBuscar = new JButton("Buscar");
        btnLimparFiltro = new JButton("Limpar Filtros");

        listModelResultado = new DefaultListModel<>();
        listResultadoLivros = new JList<>(listModelResultado);
        listResultadoLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listResultadoLivros.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Livro) {
                    Livro livro = (Livro) value;
                    String autoresStr = livro.getAutoresCompletos().stream()
                                            .map(Autor::getNome)
                                            .collect(Collectors.joining(", "));
                    setText(livro.getTitulo() + " (Por: " + (autoresStr.isEmpty() ? "N/A" : autoresStr) + ")");
                }
                return this;
            }
        });

        btnAcao = new JButton("Selecionar");
        btnAcao.setEnabled(false);
        btnCancelar = new JButton("Cancelar");
    }

    private void configurarParaTipoDialogo() {
        // ... (sem alterações aqui) ...
        switch (tipoDialogo) {
            case "BUSCA_PRINCIPAL":
                setTitle("Filtrar Livros na Tabela Principal");
                btnAcao.setText("Aplicar Filtro na Tabela"); 
                break;
            case "ALTERAR":
                setTitle("Buscar Livro para Alterar");
                btnAcao.setText("Alterar Selecionado");
                break;
            case "EXCLUIR":
                setTitle("Buscar Livro para Excluir");
                btnAcao.setText("Excluir Selecionado");
                break;
        }
    }

    private void layoutComponents() {
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5); // Aumentei um pouco o espaçamento vertical
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 0: Título
        gbc.gridx = 0; gbc.gridy = 0; panelFiltros.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelFiltros.add(txtTituloFiltro, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // Linha 1: Autor e Gênero
        gbc.gridx = 0; gbc.gridy = 1; panelFiltros.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelFiltros.add(comboAutorFiltro, gbc);
        
        gbc.gridx = 3; gbc.gridy = 1; panelFiltros.add(new JLabel("Gênero:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelFiltros.add(comboGeneroFiltro, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // Linha 2: Ano Início e Ano Fim
        gbc.gridx = 0; gbc.gridy = 2; panelFiltros.add(new JLabel("Ano Pub. De:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.2;
        panelFiltros.add(txtAnoInicioFiltro, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; panelFiltros.add(new JLabel("Até:"), gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.2;
        panelFiltros.add(txtAnoFimFiltro, gbc);
        // Espaço restante na linha 2 (se necessário)
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.weightx = 0.6; panelFiltros.add(new JLabel(""), gbc); // Espaçador
        gbc.gridwidth = 1; gbc.weightx = 0;

        // Linha 3: Botões de Filtro
        JPanel panelBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centralizado
        panelBotoesFiltro.add(btnBuscar);
        panelBotoesFiltro.add(btnLimparFiltro);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 6; // Ocupa todas as 6 colunas virtuais
        gbc.anchor = GridBagConstraints.CENTER;
        panelFiltros.add(panelBotoesFiltro, gbc);


        JPanel panelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoesAcao.add(btnAcao);
        panelBotoesAcao.add(btnCancelar);

        JPanel panelConteudo = new JPanel(new BorderLayout(10, 10));
        panelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelConteudo.add(panelFiltros, BorderLayout.NORTH);
        panelConteudo.add(new JScrollPane(listResultadoLivros), BorderLayout.CENTER);
        panelConteudo.add(panelBotoesAcao, BorderLayout.SOUTH);

        add(panelConteudo);
    }

    private void addListeners() {
        // ... (sem alterações nos listeners existentes de seleção e duplo clique na lista) ...
        btnBuscar.addActionListener((ActionEvent e) -> buscarLivros());
        btnLimparFiltro.addActionListener((ActionEvent e) -> limparFiltrosEBuscar());
        txtTituloFiltro.addActionListener((ActionEvent e) -> buscarLivros()); // Enter no título busca
        txtAnoInicioFiltro.addActionListener((ActionEvent e) -> buscarLivros()); // Enter no ano início busca
        txtAnoFimFiltro.addActionListener((ActionEvent e) -> buscarLivros());   // Enter no ano fim busca

        listResultadoLivros.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnAcao.setEnabled(listResultadoLivros.getSelectedIndex() != -1);
            }
        });

        listResultadoLivros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    if (listResultadoLivros.getSelectedIndex() != -1) {
                        confirmarAcao();
                    }
                }
            }
        });

        btnAcao.addActionListener(e -> confirmarAcao());
        btnCancelar.addActionListener(e -> {
            livroSelecionado = null; 
            dispose();
        });
    }
    
    private void limparFiltrosEBuscar() {
        txtTituloFiltro.setText("");
        comboAutorFiltro.setSelectedItem(null);
        comboGeneroFiltro.setSelectedItem(OPCAO_TODOS_GENEROS);
        txtAnoInicioFiltro.setText(""); // LIMPA CAMPO ANO INÍCIO
        txtAnoFimFiltro.setText("");   // LIMPA CAMPO ANO FIM
        buscarLivros(); 
    }

    private void buscarLivros() {
        String titulo = txtTituloFiltro.getText().trim();
        Autor autor = (Autor) comboAutorFiltro.getSelectedItem();
        String genero = (String) comboGeneroFiltro.getSelectedItem();
        String anoInicioStr = txtAnoInicioFiltro.getText().trim(); // NOVO
        String anoFimStr = txtAnoFimFiltro.getText().trim();     // NOVO

        String nomeAutorFiltro = (autor != null) ? autor.getNome() : null;
        String generoFiltro = (genero != null && !genero.equals(OPCAO_TODOS_GENEROS)) ? genero : null;
        
        Integer anoInicio = null;
        Integer anoFim = null;

        try { // Validação dos anos
            if (!anoInicioStr.isEmpty()) {
                anoInicio = Integer.parseInt(anoInicioStr);
            }
            if (!anoFimStr.isEmpty()) {
                anoFim = Integer.parseInt(anoFimStr);
            }
            if (anoInicio != null && anoFim != null && anoInicio > anoFim) {
                JOptionPane.showMessageDialog(this, "O 'Ano De' não pode ser maior que o 'Ano Até'.", "Erro de Validação de Ano", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Os anos devem ser números válidos.", "Erro de Validação de Ano", JOptionPane.ERROR_MESSAGE);
            return;
        }

        listModelResultado.clear();
        btnAcao.setEnabled(false);
        
        // Modificar chamada para o DAO
        List<Livro> livros = livroDAO.filtrarLivros(titulo, nomeAutorFiltro, generoFiltro, anoInicio, anoFim);

        if (livros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum livro encontrado com os filtros aplicados.", "Busca de Livros", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Livro livro : livros) {
                listModelResultado.addElement(livro);
            }
            if (livros.size() == 1 && !tipoDialogo.equals("BUSCA_PRINCIPAL")) {
                listResultadoLivros.setSelectedIndex(0);
                 btnAcao.setEnabled(true);
            }
        }
    }

    private void confirmarAcao() {
        // ... (sem alterações aqui) ...
        livroSelecionado = listResultadoLivros.getSelectedValue();
        if (livroSelecionado != null) {
            dispose();
        } else {
            if (!tipoDialogo.equals("BUSCA_PRINCIPAL")) { 
                 JOptionPane.showMessageDialog(this, "Nenhum livro selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                dispose();
            }
        }
    }

    public Livro getLivroSelecionado() {
        // ... (sem alterações aqui) ...
        return livroSelecionado;
    }

    /**
     * Retorna os parâmetros de filtro atuais, incluindo o intervalo de anos.
     * @return Um array de Object contendo [titulo, nomeAutor, genero, anoInicio, anoFim].
     */
    public Object[] getFiltrosAplicados() {
        if (livroSelecionado != null && !tipoDialogo.equals("BUSCA_PRINCIPAL")) {
            return null;
        }
        String titulo = txtTituloFiltro.getText().trim();
        Autor autor = (Autor) comboAutorFiltro.getSelectedItem();
        String genero = (String) comboGeneroFiltro.getSelectedItem();
        String anoInicioStr = txtAnoInicioFiltro.getText().trim();
        String anoFimStr = txtAnoFimFiltro.getText().trim();
        
        String nomeAutorFiltro = (autor != null) ? autor.getNome() : null;
        String generoFiltro = (genero != null && !genero.equals(OPCAO_TODOS_GENEROS)) ? genero : null;
        Integer anoInicio = null;
        Integer anoFim = null;

        try {
            if (!anoInicioStr.isEmpty()) anoInicio = Integer.parseInt(anoInicioStr);
            if (!anoFimStr.isEmpty()) anoFim = Integer.parseInt(anoFimStr);
            // A validação de anoInicio > anoFim já foi feita em buscarLivros
        } catch (NumberFormatException e) {
            // Ignora aqui, pois a validação principal é em buscarLivros.
            // A JanelaPrincipal pode precisar lidar com strings de ano inválidas se isso passar.
            // Ou, idealmente, só retornamos valores válidos.
        }

        return new Object[]{titulo, nomeAutorFiltro, generoFiltro, anoInicio, anoFim}; // Adiciona anos
    }
}