package Gui;

import Modelo.Autor;
import Persistencia.AutorDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Janela de diálogo para visualizar e filtrar Autores.
 * Permite buscar autores por nome e/ou intervalo de ano de nascimento.
 * A lista de resultados é apenas para visualização.
 */
public class DialogVisualizarAutores extends JDialog {

    private JTextField txtNomeFiltro;
    private JTextField txtAnoNascimentoInicioFiltro;
    private JTextField txtAnoNascimentoFimFiltro;
    private JButton btnBuscarAutores;
    private JButton btnLimparFiltrosAutores;
    private JList<Autor> listResultadoAutores;
    private DefaultListModel<Autor> listModelResultado;
    private JButton btnFecharDialogo;

    private AutorDAO autorDAO;

    /**
     * Construtor do diálogo.
     * @param owner O Frame proprietário do diálogo.
     * @param autorDAO Uma instância de AutorDAO para buscar os autores.
     */
    public DialogVisualizarAutores(Frame owner, AutorDAO autorDAO) {
        super(owner, "Visualizar e Filtrar Autores", true); // Modal
        this.autorDAO = autorDAO;

        initComponents();
        layoutComponents();
        addListeners();
        
        buscarAutores(); // Carrega todos os autores inicialmente ou com filtros vazios

        pack();
        setMinimumSize(new Dimension(550, 450));
        setLocationRelativeTo(owner);
    }

    /**
     * Inicializa os componentes da UI.
     */
    private void initComponents() {
        txtNomeFiltro = new JTextField(20);
        txtAnoNascimentoInicioFiltro = new JTextField(5);
        txtAnoNascimentoFimFiltro = new JTextField(5);

        btnBuscarAutores = new JButton("Buscar Autores");
        btnLimparFiltrosAutores = new JButton("Limpar Filtros");

        listModelResultado = new DefaultListModel<>();
        listResultadoAutores = new JList<>(listModelResultado);
        listResultadoAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        // Não há ação na seleção, mas manter SINGLE_SELECTION é comum.

        btnFecharDialogo = new JButton("Fechar");
    }

    /**
     * Organiza os componentes na janela.
     */
    private void layoutComponents() {
        // Painel para os filtros
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 0: Nome do Autor
        gbc.gridx = 0; gbc.gridy = 0; panelFiltros.add(new JLabel("Nome do Autor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelFiltros.add(txtNomeFiltro, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // Linha 1: Ano de Nascimento (De / Até)
        gbc.gridx = 0; gbc.gridy = 1; panelFiltros.add(new JLabel("Ano Nasc. De:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelFiltros.add(txtAnoNascimentoInicioFiltro, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; panelFiltros.add(new JLabel("Até:"), gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panelFiltros.add(txtAnoNascimentoFimFiltro, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // Linha 2: Botões de Filtro
        JPanel panelBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotoesFiltro.add(btnBuscarAutores);
        panelBotoesFiltro.add(btnLimparFiltrosAutores);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        panelFiltros.add(panelBotoesFiltro, gbc);

        // Painel para o botão Fechar
        JPanel panelBotaoFechar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotaoFechar.add(btnFecharDialogo);

        // Painel principal do diálogo
        JPanel panelConteudo = new JPanel(new BorderLayout(10, 10));
        panelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelConteudo.add(panelFiltros, BorderLayout.NORTH);
        panelConteudo.add(new JScrollPane(listResultadoAutores), BorderLayout.CENTER);
        panelConteudo.add(panelBotaoFechar, BorderLayout.SOUTH);

        add(panelConteudo);
    }

    /**
     * Adiciona listeners de evento aos componentes.
     */
    private void addListeners() {
        btnBuscarAutores.addActionListener((ActionEvent e) -> buscarAutores());
        // Permitir busca com Enter nos campos de texto
        txtNomeFiltro.addActionListener((ActionEvent e) -> buscarAutores());
        txtAnoNascimentoInicioFiltro.addActionListener((ActionEvent e) -> buscarAutores());
        txtAnoNascimentoFimFiltro.addActionListener((ActionEvent e) -> buscarAutores());

        btnLimparFiltrosAutores.addActionListener((ActionEvent e) -> limparFiltrosEBuscar());
        btnFecharDialogo.addActionListener((ActionEvent e) -> dispose()); // Simplesmente fecha o diálogo
    }

    /**
     * Limpa os campos de filtro e realiza uma nova busca (que listará todos os autores).
     */
    private void limparFiltrosEBuscar() {
        txtNomeFiltro.setText("");
        txtAnoNascimentoInicioFiltro.setText("");
        txtAnoNascimentoFimFiltro.setText("");
        buscarAutores();
    }

    /**
     * Busca autores com base nos filtros preenchidos e atualiza a JList.
     */
    private void buscarAutores() {
        String nome = txtNomeFiltro.getText().trim();
        String anoInicioStr = txtAnoNascimentoInicioFiltro.getText().trim();
        String anoFimStr = txtAnoNascimentoFimFiltro.getText().trim();

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
                JOptionPane.showMessageDialog(this, 
                        "O 'Ano Nasc. De' não pode ser maior que o 'Ano Nasc. Até'.", 
                        "Erro de Validação de Ano", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe a busca se a validação falhar
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                        "Os anos de nascimento devem ser números válidos.", 
                        "Erro de Validação de Ano", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe a busca
        }

        listModelResultado.clear(); // Limpa resultados anteriores
        
        try {
            List<Autor> autores = autorDAO.filtrarAutores(nome, anoInicio, anoFim);
            if (autores.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                            "Nenhum autor encontrado com os filtros aplicados.", 
                            "Busca de Autores", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Autor autor : autores) {
                    listModelResultado.addElement(autor);
                }
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, 
                            "Erro ao buscar autores: " + e.getMessage(), 
                            "Erro de Busca", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
    }
}