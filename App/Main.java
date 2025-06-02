package app; // Ou o nome do pacote que você escolheu

import Gui.JanelaPrincipal;
import Persistencia.MongoConnection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException; // <<< LINHA ADICIONADA

/**
 * Classe principal para iniciar a aplicação da Biblioteca.
 * Configura o Look and Feel e instancia a JanelaPrincipal.
 * Adiciona um shutdown hook para fechar a conexão com o MongoDB ao sair.
 */
public class Main {

    /**
     * Método principal da aplicação.
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) { // Agora o compilador deve encontrar
            System.err.println("Não foi possível definir o Look and Feel do sistema: " + e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook: Encerrando a aplicação, fechando conexão com MongoDB...");
            MongoConnection.close();
        }));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaPrincipal().setVisible(true);
            }
        });
    }
}