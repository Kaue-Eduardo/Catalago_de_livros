package Gui;

import Modelo.Autor;
import Modelo.Livro;
import Persistencia.AutorDAO;
import Persistencia.LivroDAO;
import Persistencia.MongoConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException; // Para Desktop.browse()
import java.net.URI;       // Para Desktop.browse()
import java.net.URISyntaxException; // Para Desktop.browse()
import java.util.List;

/**
 * Janela principal da aplicação da Biblioteca.
 */
public class JanelaPrincipal extends JFrame {

    private AutorDAO autorDAO;
    private LivroDAO livroDAO;

    private JTable tabelaLivros;
    private TableModelLivros tableModelLivros;

    private JButton btnAdicionarLivro;
    private JButton btnAlterarLivro;
    private JButton btnExcluirLivro;
    private JButton btnFiltrarLivros;
    private JButton btnLimparFiltrosTabela;
    private JButton btnAbrirLinkLivro; // NOVO BOTÃO

    private JButton btnAdicionarAutor;
    private JButton btnAlterarAutor;
    private JButton btnExcluirAutor;
    private JButton btnListarFiltrarAutores;

    private JLabel lblStatus;

    public JanelaPrincipal() {
        super("Catálogo de Livros Pessoal");

        autorDAO = new AutorDAO();
        livroDAO = new LivroDAO();
        autorDAO.setLivroDAO(livroDAO);
        livroDAO.setAutorDAO(autorDAO);

        initComponents();
        layoutComponents();
        addListeners();

        carregarLivrosNaTabela();
        atualizarEstadoBotoesLivro(); // Chamada inicial para definir estado dos botões

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(950, 650); // Aumentei um pouco a largura
        setLocationRelativeTo(null);
        setVisible(true);
    }

// Dentro da classe JanelaPrincipal.java

// Dentro da classe JanelaPrincipal.java

// Dentro da classe JanelaPrincipal.java
// Adicione este import no início do seu arquivo JanelaPrincipal.java, se ainda não estiver lá:
// import javax.swing.table.DefaultTableCellRenderer;
// import javax.swing.JLabel; // Para SwingConstants.CENTER

private void initComponents() {
    tableModelLivros = new TableModelLivros();
    tabelaLivros = new JTable(tableModelLivros);
    tabelaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabelaLivros.setAutoCreateRowSorter(true);

    // --- AJUSTES DE LARGURA E ALINHAMENTO DAS COLUNAS ---
    javax.swing.table.TableColumnModel columnModel = tabelaLivros.getColumnModel();

    // Criar um renderer para centralizar o conteúdo
    javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);

    // Coluna "#" (índice 0)
    javax.swing.table.TableColumn indexColumn = columnModel.getColumn(0);
    indexColumn.setPreferredWidth(40);
    indexColumn.setMaxWidth(60);
    indexColumn.setMinWidth(30);
    indexColumn.setCellRenderer(centerRenderer); // CENTRALIZA A COLUNA DE ÍNDICE

    // Coluna "Título" (índice 1) - Mantém alinhamento padrão (esquerda)
    javax.swing.table.TableColumn tituloColumn = columnModel.getColumn(1);
    tituloColumn.setPreferredWidth(250);
    tituloColumn.setMinWidth(150);

    // Coluna "Autores" (índice 2) - Mantém alinhamento padrão (esquerda)
    javax.swing.table.TableColumn autoresColumn = columnModel.getColumn(2);
    autoresColumn.setPreferredWidth(200);
    autoresColumn.setMinWidth(100);

    // Coluna "Ano Publicação" (índice 3)
    javax.swing.table.TableColumn anoColumn = columnModel.getColumn(3);
    anoColumn.setPreferredWidth(100);
    anoColumn.setMaxWidth(120);
    anoColumn.setMinWidth(80);
    anoColumn.setCellRenderer(centerRenderer); // CENTRALIZA A COLUNA DE ANO

    // Coluna "Gênero" (índice 4)
    javax.swing.table.TableColumn generoColumn = columnModel.getColumn(4);
    generoColumn.setPreferredWidth(120);
    generoColumn.setMaxWidth(150);
    generoColumn.setMinWidth(100);

    // Coluna "Lido" (índice 5)
    javax.swing.table.TableColumn lidoColumn = columnModel.getColumn(5);
    lidoColumn.setPreferredWidth(60);
    lidoColumn.setMaxWidth(80);
    lidoColumn.setMinWidth(40);
    lidoColumn.setCellRenderer(centerRenderer); // CENTRALIZA A COLUNA "LIDO"

    // Coluna "Nota" (índice 6)
    javax.swing.table.TableColumn notaColumn = columnModel.getColumn(6);
    notaColumn.setPreferredWidth(50);
    notaColumn.setMaxWidth(70);
    notaColumn.setMinWidth(40);
    notaColumn.setCellRenderer(centerRenderer); // CENTRALIZA A COLUNA "NOTA"
    // --- FIM DOS AJUSTES DE LARGURA E ALINHAMENTO ---

    btnAdicionarLivro = new JButton("Adicionar Livro");
    btnAlterarLivro = new JButton("Alterar Livro");
    btnExcluirLivro = new JButton("Excluir Livro");
    btnFiltrarLivros = new JButton("Filtrar/Buscar Livros");
    btnLimparFiltrosTabela = new JButton("Mostrar Todos os Livros");
    btnAbrirLinkLivro = new JButton("Abrir Link do Conteúdo");
    btnAbrirLinkLivro.setEnabled(false);

    btnAdicionarAutor = new JButton("Adicionar Autor");
    btnAlterarAutor = new JButton("Alterar Autor");
    btnExcluirAutor = new JButton("Excluir Autor");
    btnListarFiltrarAutores = new JButton("Listar/Filtrar Autores");

    lblStatus = new JLabel("Pronto.");
    lblStatus.setBorder(BorderFactory.createEtchedBorder());
}

    private void layoutComponents() {
        JPanel painelConteudo = new JPanel(new BorderLayout(10, 10));
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPaneTabela = new JScrollPane(tabelaLivros);
        painelConteudo.add(scrollPaneTabela, BorderLayout.CENTER);

        JPanel painelAcoesLivro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoesLivro.setBorder(BorderFactory.createTitledBorder("Gerenciar Livros"));
        painelAcoesLivro.add(btnAdicionarLivro);
        painelAcoesLivro.add(btnAlterarLivro);
        painelAcoesLivro.add(btnExcluirLivro);
        painelAcoesLivro.add(btnAbrirLinkLivro); // ADICIONA NOVO BOTÃO AO PAINEL
        painelAcoesLivro.add(btnFiltrarLivros);
        painelAcoesLivro.add(btnLimparFiltrosTabela);


        JPanel painelAcoesAutor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoesAutor.setBorder(BorderFactory.createTitledBorder("Gerenciar Autores"));
        painelAcoesAutor.add(btnAdicionarAutor);
        painelAcoesAutor.add(btnAlterarAutor);
        painelAcoesAutor.add(btnExcluirAutor);
        painelAcoesAutor.add(btnListarFiltrarAutores);

        JPanel painelAcoesAgrupado = new JPanel();
        painelAcoesAgrupado.setLayout(new BoxLayout(painelAcoesAgrupado, BoxLayout.Y_AXIS));
        painelAcoesAgrupado.add(painelAcoesLivro);
        painelAcoesAgrupado.add(painelAcoesAutor);

        painelConteudo.add(painelAcoesAgrupado, BorderLayout.SOUTH);
        painelConteudo.add(lblStatus, BorderLayout.NORTH);

        setContentPane(painelConteudo);
    }

    private void addListeners() {
        tabelaLivros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                atualizarEstadoBotoesLivro(); // Atualiza também o botão de link
            }
        });

        btnAdicionarLivro.addActionListener(e -> adicionarNovoLivro());
        btnAlterarLivro.addActionListener(e -> alterarLivroSelecionado());
        btnExcluirLivro.addActionListener(e -> excluirLivroSelecionado());
        btnAbrirLinkLivro.addActionListener(e -> abrirLinkDoLivroSelecionado()); // AÇÃO DO NOVO BOTÃO
        btnFiltrarLivros.addActionListener(e -> filtrarTabelaPrincipal());
        btnLimparFiltrosTabela.addActionListener(e -> carregarLivrosNaTabela());

        btnAdicionarAutor.addActionListener(e -> adicionarNovoAutor());
        btnAlterarAutor.addActionListener(e -> alterarAutor());
        btnExcluirAutor.addActionListener(e -> excluirAutor());
        btnListarFiltrarAutores.addActionListener(e -> visualizarAutores());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSaida();
            }
        });
    }

    /**
     * Atualiza o estado dos botões "Alterar Livro", "Excluir Livro" e "Abrir Link do Conteúdo"
     * com base na seleção da tabela e na existência do link.
     */
    private void atualizarEstadoBotoesLivro() {
        int selectedRow = tabelaLivros.getSelectedRow();
        boolean selecionado = selectedRow != -1;
        
        btnAlterarLivro.setEnabled(selecionado);
        btnExcluirLivro.setEnabled(selecionado);

        if (selecionado) {
            int modelRow = tabelaLivros.convertRowIndexToModel(selectedRow);
            Livro livro = tableModelLivros.getLivroAt(modelRow);
            if (livro != null && livro.getLinkConteudo() != null && !livro.getLinkConteudo().trim().isEmpty()) {
                btnAbrirLinkLivro.setEnabled(true);
            } else {
                btnAbrirLinkLivro.setEnabled(false);
            }
        } else {
            btnAbrirLinkLivro.setEnabled(false);
        }
    }
    
    /**
     * Tenta abrir o link associado ao livro selecionado no navegador padrão.
     */
// Dentro da classe JanelaPrincipal.java

    /**
     * Tenta abrir o link associado ao livro selecionado no navegador padrão ou aplicativo associado.
     */
    private void abrirLinkDoLivroSelecionado() {
        int selectedRow = tabelaLivros.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Nenhum livro selecionado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabelaLivros.convertRowIndexToModel(selectedRow);
        Livro livro = tableModelLivros.getLivroAt(modelRow);

        if (livro != null && livro.getLinkConteudo() != null && !livro.getLinkConteudo().trim().isEmpty()) {
            String link = livro.getLinkConteudo().trim(); // Pega o link como foi salvo

            // REMOVEMOS a lógica que adicionava "http://" automaticamente.
            // Agora, o link deve ser uma URI válida como está armazenado.

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    // Tenta criar a URI e abri-la.
                    // O usuário deve garantir que o link armazenado é uma URI válida.
                    Desktop.getDesktop().browse(new URI(link));
                    lblStatus.setText("Tentando abrir link: " + link);
                } catch (IOException | URISyntaxException | UnsupportedOperationException | IllegalArgumentException ex) {
                    // IllegalArgumentException pode ocorrer se o link não for uma URI absoluta ou tiver caracteres inválidos.
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível abrir o link: " + ex.getMessage() +
                            "\nVerifique se o link é uma URI válida (ex: comece com http://, https://, file:///) e se há um aplicativo associado.",
                            "Erro ao Abrir Link", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Ajuda a depurar no console
                    lblStatus.setText("Erro ao tentar abrir link: " + link);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "A funcionalidade de abrir links não é suportada neste sistema.",
                        "Funcionalidade Indisponível", JOptionPane.WARNING_MESSAGE);
                lblStatus.setText("Abrir link não suportado.");
            }
        } else {
             JOptionPane.showMessageDialog(this, "O livro selecionado não possui um link de conteúdo ou o link está vazio.", "Link Indisponível", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // ... (restante dos métodos da JanelaPrincipal permanecem os mesmos) ...
    // confirmarSaida(), carregarLivrosNaTabela(), filtrarTabelaPrincipal(),
    // adicionarNovoAutor(), alterarAutor(), excluirAutor(),
    // adicionarNovoLivro(), alterarLivroSelecionado(), excluirLivroSelecionado(), visualizarAutores()
    // (Copie os métodos inalterados da versão anterior de JanelaPrincipal.java)
    
    private void confirmarSaida() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            MongoConnection.close(); 
            dispose();
            System.exit(0); 
        }
    }

    private void carregarLivrosNaTabela() {
        try {
            List<Livro> livros = livroDAO.listarTodosLivros();
            tableModelLivros.setLivros(livros);
            lblStatus.setText(livros.size() + " livro(s) carregado(s). (Filtros limpos)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar livros do banco de dados: " + e.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
            lblStatus.setText("Erro ao carregar livros.");
        }
    }

    private void filtrarTabelaPrincipal() {
        DialogFiltrarLivro dialog = new DialogFiltrarLivro(this, livroDAO, autorDAO, "BUSCA_PRINCIPAL");
        dialog.setVisible(true);
        Object[] filtros = dialog.getFiltrosAplicados(); 
        if (dialog.isDisplayable() == false && filtros != null) { 
            try {
                String titulo = (String) filtros[0];
                String nomeAutor = (String) filtros[1];
                String genero = (String) filtros[2];
                Integer anoInicio = (Integer) filtros[3]; 
                Integer anoFim = (Integer) filtros[4];     
                List<Livro> livrosFiltrados = livroDAO.filtrarLivros(titulo, nomeAutor, genero, anoInicio, anoFim); 
                tableModelLivros.setLivros(livrosFiltrados);
                if (livrosFiltrados.isEmpty() && ( (titulo == null || titulo.isEmpty()) && nomeAutor == null && genero == null && anoInicio == null && anoFim == null)) {
                    lblStatus.setText("Nenhum livro encontrado com os filtros especificados.");
                } else {
                    lblStatus.setText(livrosFiltrados.size() + " livro(s) encontrado(s) com o filtro.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao aplicar filtro: " + e.getMessage(), "Erro de Filtragem", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                lblStatus.setText("Erro ao filtrar livros.");
            }
        }
    }

    private void adicionarNovoAutor() {
        DialogAdicionarEditarAutor dialog = new DialogAdicionarEditarAutor(this, null);
        dialog.setVisible(true);
        Autor novoAutor = dialog.getAutor();
        if (novoAutor != null && dialog.isSalvo()) {
            try {
                autorDAO.adicionarAutor(novoAutor);
                JOptionPane.showMessageDialog(this, "Autor adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("Autor '" + novoAutor.getNome() + "' adicionado.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar autor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                lblStatus.setText("Erro ao adicionar autor.");
            }
        }
    }
    private void alterarAutor() {
        DialogFiltrarAutor dialogFiltro = new DialogFiltrarAutor(this, autorDAO, "ALTERAR");
        dialogFiltro.setVisible(true);
        Autor autorParaEditar = dialogFiltro.getAutorSelecionado();
        if (autorParaEditar != null) {
            DialogAdicionarEditarAutor dialogEdicao = new DialogAdicionarEditarAutor(this, autorParaEditar);
            dialogEdicao.setVisible(true);
            Autor autorEditado = dialogEdicao.getAutor();
            if (autorEditado != null && dialogEdicao.isSalvo()) {
                try {
                    autorDAO.atualizarAutor(autorEditado);
                    JOptionPane.showMessageDialog(this, "Autor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    lblStatus.setText("Autor '" + autorEditado.getNome() + "' atualizado.");
                    carregarLivrosNaTabela(); 
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar autor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    lblStatus.setText("Erro ao atualizar autor.");
                }
            }
        }
    }
    private void excluirAutor() {
        DialogFiltrarAutor dialogFiltro = new DialogFiltrarAutor(this, autorDAO, "EXCLUIR");
        dialogFiltro.setVisible(true);
        Autor autorParaExcluir = dialogFiltro.getAutorSelecionado();
        if (autorParaExcluir != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o autor '" + autorParaExcluir.getNome() + "'?\n" +
                    "Esta ação removerá o autor de todos os livros onde ele é coautor.",
                    "Confirmar Exclusão de Autor",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    autorDAO.excluirAutor(autorParaExcluir.getId());
                    JOptionPane.showMessageDialog(this, "Autor excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    lblStatus.setText("Autor '" + autorParaExcluir.getNome() + "' excluído.");
                    carregarLivrosNaTabela(); 
                } catch (IllegalStateException ise) { 
                    JOptionPane.showMessageDialog(this, ise.getMessage(), "Exclusão Impedida", JOptionPane.WARNING_MESSAGE);
                    lblStatus.setText("Exclusão do autor '" + autorParaExcluir.getNome() + "' impedida.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir autor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    lblStatus.setText("Erro ao excluir autor.");
                }
            }
        }
    }

    private void adicionarNovoLivro() {
        DialogAdicionarEditarLivro dialog = new DialogAdicionarEditarLivro(this, null, autorDAO);
        dialog.setVisible(true);
        Livro novoLivro = dialog.getLivro();
        if (novoLivro != null && dialog.isSalvo()) {
            try {
                livroDAO.adicionarLivro(novoLivro);
                carregarLivrosNaTabela(); 
                JOptionPane.showMessageDialog(this, "Livro adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("Livro '" + novoLivro.getTitulo() + "' adicionado.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar livro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                lblStatus.setText("Erro ao adicionar livro.");
            }
        }
    }
    private void alterarLivroSelecionado() {
        int selectedRow = tabelaLivros.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabelaLivros.convertRowIndexToModel(selectedRow);
        Livro livroParaEditar = tableModelLivros.getLivroAt(modelRow);
        if (livroParaEditar != null) {
            DialogAdicionarEditarLivro dialog = new DialogAdicionarEditarLivro(this, livroParaEditar, autorDAO);
            dialog.setVisible(true);
            Livro livroEditado = dialog.getLivro();
            if (livroEditado != null && dialog.isSalvo()) {
                try {
                    livroDAO.atualizarLivro(livroEditado);
                    livroDAO.buscarLivroPorId(livroEditado.getId()); 
                    tableModelLivros.updateLivro(modelRow, livroEditado);
                    JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    lblStatus.setText("Livro '" + livroEditado.getTitulo() + "' atualizado.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar livro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    lblStatus.setText("Erro ao atualizar livro.");
                }
            }
        }
    }
    private void excluirLivroSelecionado() {
        int selectedRow = tabelaLivros.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabelaLivros.convertRowIndexToModel(selectedRow);
        Livro livroParaExcluir = tableModelLivros.getLivroAt(modelRow);
        if (livroParaExcluir != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o livro '" + livroParaExcluir.getTitulo() + "'?",
                    "Confirmar Exclusão de Livro",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    livroDAO.excluirLivro(livroParaExcluir.getId());
                    tableModelLivros.removeLivro(modelRow); 
                    JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    lblStatus.setText("Livro '" + livroParaExcluir.getTitulo() + "' excluído.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir livro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    lblStatus.setText("Erro ao excluir livro.");
                }
            }
        }
    }
    
    private void visualizarAutores() {
        DialogVisualizarAutores dialog = new DialogVisualizarAutores(this, autorDAO);
        dialog.setVisible(true);
        lblStatus.setText("Janela de visualização de autores fechada.");
    }
}