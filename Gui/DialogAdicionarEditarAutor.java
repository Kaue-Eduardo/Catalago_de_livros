package Gui;

import Modelo.Autor;
import Persistencia.AutorDAO; // Necessário se for verificar duplicidade de nome aqui, por exemplo.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate; // Para validar o ano de nascimento

/**
 * Janela de diálogo para adicionar ou editar um Autor.
 */
public class DialogAdicionarEditarAutor extends JDialog {

    private JTextField txtNome;
    private JTextField txtAnoNascimento;
    private JButton btnSalvar;
    private JButton btnCancelar;

    private Autor autor; // Autor sendo editado, ou null se for um novo autor
    private boolean salvo; // Flag para indicar se o autor foi salvo

    /**
     * Construtor para a janela de diálogo.
     * @param owner O Frame proprietário do diálogo.
     * @param autorParaEditar O Autor a ser editado. Se null, um novo autor será criado.
     */
    public DialogAdicionarEditarAutor(Frame owner, Autor autorParaEditar) {
        super(owner, true); // true para modal
        this.autor = autorParaEditar;
        this.salvo = false;

        setTitle(autor == null ? "Adicionar Novo Autor" : "Editar Autor");
        initComponents();
        layoutComponents();
        addListeners();

        if (autor != null) {
            preencherCampos();
        }

        pack(); // Ajusta o tamanho do diálogo aos componentes
        setLocationRelativeTo(owner); // Centraliza em relação ao proprietário
    }

    /**
     * Inicializa os componentes da UI.
     */
    private void initComponents() {
        txtNome = new JTextField(30);
        txtAnoNascimento = new JTextField(5); // Suficiente para 4 dígitos do ano
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
    }

    /**
     * Organiza os componentes na janela usando GridBagLayout.
     */
    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 1: Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome*:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Permite que o campo de texto expanda horizontalmente
        panel.add(txtNome, gbc);
        gbc.fill = GridBagConstraints.NONE; // Reset fill
        gbc.weightx = 0; // Reset weight

        // Linha 2: Ano de Nascimento
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ano de Nascimento*:"), gbc);

        gbc.gridx = 1;
        panel.add(txtAnoNascimento, gbc);

        // Linha 3: Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelBotoes, gbc);

        add(panel, BorderLayout.CENTER);
        add(new JLabel(" * Campos obrigatórios"), BorderLayout.SOUTH); // Rodapé com aviso
    }

    /**
     * Adiciona os listeners de evento aos botões.
     */
    private void addListeners() {
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarAutor();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvo = false;
                dispose(); // Fecha o diálogo
            }
        });
    }

    /**
     * Preenche os campos do formulário se estiver editando um autor existente.
     */
    private void preencherCampos() {
        if (autor != null) {
            txtNome.setText(autor.getNome());
            txtAnoNascimento.setText(String.valueOf(autor.getAnoNascimento()));
        }
    }

    /**
     * Valida os dados e tenta salvar o autor.
     */
    private void salvarAutor() {
        String nome = txtNome.getText().trim();
        String anoStr = txtAnoNascimento.getText().trim();

        // Validação
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Nome' é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            txtNome.requestFocus();
            return;
        }
        if (anoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Ano de Nascimento' é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            txtAnoNascimento.requestFocus();
            return;
        }

        int anoNascimento;
        try {
            anoNascimento = Integer.parseInt(anoStr);
            if (anoNascimento <= 0 || anoNascimento > LocalDate.now().getYear()) {
                JOptionPane.showMessageDialog(this, 
                        "Ano de Nascimento inválido. Deve ser um ano válido e não pode ser maior que o ano atual.", 
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                txtAnoNascimento.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano de Nascimento deve ser um número válido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            txtAnoNascimento.requestFocus();
            return;
        }

        // Se for um novo autor, cria a instância
        if (this.autor == null) {
            this.autor = new Autor();
        }
        // Atualiza os dados do autor
        this.autor.setNome(nome);
        this.autor.setAnoNascimento(anoNascimento);

        this.salvo = true;
        dispose(); // Fecha o diálogo
    }

    /**
     * Retorna o objeto Autor que foi salvo/editado.
     * @return O Autor, ou null se o diálogo foi cancelado.
     */
    public Autor getAutor() {
        return salvo ? autor : null;
    }

    /**
     * Verifica se os dados foram salvos.
     * @return true se o botão "Salvar" foi pressionado e os dados validados, false caso contrário.
     */
    public boolean isSalvo() {
        return salvo;
    }
}