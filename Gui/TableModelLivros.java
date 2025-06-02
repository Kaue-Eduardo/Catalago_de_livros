package Gui;

import Modelo.Autor;
import Modelo.Livro;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * TableModel personalizado para exibir uma lista de objetos Livro em uma JTable.
 */
public class TableModelLivros extends AbstractTableModel {

    private List<Livro> livros;
    // ADICIONA AS COLUNAS "Lido" E "Nota"
    private final String[] colunas = {"#", "Título", "Autores", "Ano Publicação", "Gênero", "Lido", "Nota"};

    public TableModelLivros(List<Livro> livros) {
        this.livros = livros != null ? new ArrayList<>(livros) : new ArrayList<>();
    }

    public TableModelLivros() {
        this.livros = new ArrayList<>();
    }

    @Override
    public int getRowCount() { return livros.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int columnIndex) { return colunas[columnIndex]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Livro livro = livros.get(rowIndex);
        switch (columnIndex) {
            case 0: return rowIndex + 1;
            case 1: return livro.getTitulo();
            case 2:
                if (livro.getAutoresCompletos() != null && !livro.getAutoresCompletos().isEmpty()) {
                    return livro.getAutoresCompletos().stream()
                                .map(Autor::getNome)
                                .collect(Collectors.joining(", "));
                }
                return "N/A";
            case 3: return livro.getAnoPublicacao();
            case 4: return livro.getGenero();
            case 5: return livro.isLido() ? "Sim" : "Não"; // EXIBE "Sim" ou "Não" PARA Lido
            case 6: return livro.getNota();               // EXIBE A Nota
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: // #
            case 3: // Ano Publicação
            case 6: // Nota
                return Integer.class;
            case 5: // Lido (exibido como String "Sim"/"Não")
                return String.class;
            default: // Título, Autores, Gênero
                return String.class;
        }
    }

    public Livro getLivroAt(int rowIndex) { /* ... (sem alteração) ... */ 
        if (rowIndex >= 0 && rowIndex < livros.size()) {
            return livros.get(rowIndex);
        }
        return null;
    }
    public void setLivros(List<Livro> novosLivros) { /* ... (sem alteração) ... */
        this.livros.clear();
        if (novosLivros != null) {
            this.livros.addAll(novosLivros);
        }
        fireTableDataChanged(); 
    }
    public void addLivro(Livro livro) { /* ... (sem alteração) ... */
        this.livros.add(livro);
        fireTableRowsInserted(this.livros.size() - 1, this.livros.size() - 1);
    }
    public void removeLivro(int rowIndex) { /* ... (sem alteração) ... */
        if (rowIndex >= 0 && rowIndex < this.livros.size()) {
            this.livros.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    public void updateLivro(int rowIndex, Livro livroAtualizado) { /* ... (sem alteração) ... */
        if (rowIndex >= 0 && rowIndex < this.livros.size()) {
            this.livros.set(rowIndex, livroAtualizado);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}