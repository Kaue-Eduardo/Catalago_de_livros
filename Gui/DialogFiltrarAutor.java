package Gui;

import Modelo.Autor;
import Persistencia.AutorDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Janela de diálogo para buscar e selecionar um Autor.
 * Usada antes de operações de alteração ou exclusão de autores.
 */
public class DialogFiltrarAutor extends JDialog {

    private JTextField txtNomeFiltro;
    private JButton btnBuscar;
    private JList<Autor> listResultadoAutores;
    private DefaultListModel<Autor> listModelResultado;
    private JButton btnSelecionar; // Ou "Alterar Selecionado" / "Excluir Selecionado" dependendo do contexto
    private JButton btnCancelar;

    private AutorDAO autorDAO;
    private Autor autorSelecionado;
    private String acao; // "ALTERAR" ou "EXCLUIR" para mudar o texto do botão de ação

    /**
     * Construtor.
     * @param owner O Frame proprietário.
     * @param autorDAO DAO para buscar autores.
     * @param acao Ação a ser realizada ("ALTERAR" ou "EXCLUIR"), para customizar o botão.
     */
    public DialogFiltrarAutor(Frame owner, AutorDAO autorDAO, String acao) {
        super(owner, "Buscar Autor para " + acao.toLowerCase(), true);
        this.autorDAO = autorDAO;
        this.acao = acao;
        this.autorSelecionado = null;

        initComponents();
        layoutComponents();
        addListeners();

        pack();
        setMinimumSize(new Dimension(450, 350));
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        txtNomeFiltro = new JTextField(25);
        btnBuscar = new JButton("Buscar");

        listModelResultado = new DefaultListModel<>();
        listResultadoAutores = new JList<>(listModelResultado);
        listResultadoAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnSelecionar = new JButton(acao.substring(0,1).toUpperCase() + acao.substring(1).toLowerCase() + " Selecionado");
        btnSelecionar.setEnabled(false); // Habilita após seleção
        btnCancelar = new JButton("Cancelar");
    }

    private void layoutComponents() {
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Nome do Autor:"));
        panelFiltro.add(txtNomeFiltro);
        panelFiltro.add(btnBuscar);

        JPanel panelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoesAcao.add(btnSelecionar);
        panelBotoesAcao.add(btnCancelar);

        JPanel panelConteudo = new JPanel(new BorderLayout(5, 5));
        panelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelConteudo.add(panelFiltro, BorderLayout.NORTH);
        panelConteudo.add(new JScrollPane(listResultadoAutores), BorderLayout.CENTER);
        panelConteudo.add(panelBotoesAcao, BorderLayout.SOUTH);

        add(panelConteudo);
    }

    private void addListeners() {
        btnBuscar.addActionListener((ActionEvent e) -> buscarAutores());

        txtNomeFiltro.addActionListener((ActionEvent e) -> buscarAutores()); // Permite buscar com Enter

        listResultadoAutores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSelecionar.setEnabled(listResultadoAutores.getSelectedIndex() != -1);
            }
        });

        listResultadoAutores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Duplo clique
                    if (listResultadoAutores.getSelectedIndex() != -1) {
                        confirmarSelecao();
                    }
                }
            }
        });

        btnSelecionar.addActionListener(e -> confirmarSelecao());

        btnCancelar.addActionListener(e -> {
            autorSelecionado = null;
            dispose();
        });
    }

    private void buscarAutores() {
        String termo = txtNomeFiltro.getText().trim();
        listModelResultado.clear();
        btnSelecionar.setEnabled(false);

        if (termo.isEmpty()) {
            // Opcional: Carregar todos os autores se o campo estiver vazio, ou mostrar mensagem.
            // Por ora, não faz nada se vazio, esperando um termo.
            // JOptionPane.showMessageDialog(this, "Digite um termo para buscar.", "Busca", JOptionPane.INFORMATION_MESSAGE);
            // return;
        }
        
        List<Autor> autores = autorDAO.buscarAutoresPorNome(termo);
        if (autores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum autor encontrado com o termo '" + termo + "'.", "Busca", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Autor autor : autores) {
                listModelResultado.addElement(autor);
            }
            // Otimização: Se apenas um resultado, seleciona e habilita o botão
            if (autores.size() == 1) {
                listResultadoAutores.setSelectedIndex(0);
                // Se a ação for "ALTERAR" ou "EXCLUIR" e houver apenas um resultado,
                // poderíamos até chamar confirmarSelecao() diretamente, mas vamos deixar o usuário confirmar.
                 btnSelecionar.setEnabled(true); 
                 // Opcional: if (acao.equals("ALTERAR") || acao.equals("EXCLUIR")) { confirmarSelecao(); }
            }
        }
    }

    private void confirmarSelecao() {
        autorSelecionado = listResultadoAutores.getSelectedValue();
        if (autorSelecionado != null) {
            dispose();
        } else {
            // Isso não deveria acontecer se o botão estiver habilitado corretamente
            JOptionPane.showMessageDialog(this, "Nenhum autor selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retorna o autor que foi selecionado no diálogo.
     * @return O Autor selecionado, ou null se o diálogo foi cancelado ou nenhum autor foi selecionado.
     */
    public Autor getAutorSelecionado() {
        return autorSelecionado;
    }
}