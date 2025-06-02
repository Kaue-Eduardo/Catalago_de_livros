package Modelo;

import org.bson.types.ObjectId;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Representa um livro na biblioteca.
 * Contém informações como título, ano de publicação, gênero, uma lista de IDs de autores,
 * um link ilustrativo para o conteúdo, status de leitura e nota.
 */
public class Livro {

    private ObjectId id;
    private String titulo;
    private int anoPublicacao;
    private String genero;
    private List<ObjectId> autoresIds;
    private String linkConteudo;
    private boolean lido; // NOVO CAMPO: true se lido, false se não lido
    private int nota;     // NOVO CAMPO: Nota de 0 a 10

    private transient List<Autor> autoresCompletos;

    public Livro() {
        this.autoresIds = new ArrayList<>();
        this.autoresCompletos = new ArrayList<>();
        // Valores padrão para os novos campos (podem ser ajustados conforme necessidade)
        this.lido = false; // Padrão para não lido
        this.nota = 0;     // Padrão para nota mínima, já que é obrigatório
    }

    // Construtor pode ser atualizado se necessário, mas os setters farão o trabalho.

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public List<ObjectId> getAutoresIds() {
        if (this.autoresIds == null) this.autoresIds = new ArrayList<>();
        return autoresIds;
    }
    public void setAutoresIds(List<ObjectId> autoresIds) { this.autoresIds = autoresIds; }

    public String getLinkConteudo() { return linkConteudo; }
    public void setLinkConteudo(String linkConteudo) { this.linkConteudo = linkConteudo; }

    /**
     * Verifica se o livro foi lido.
     * @return true se o livro foi marcado como lido, false caso contrário.
     */
    public boolean isLido() { // Getter para boolean é "isLido" por convenção
        return lido;
    }

    /**
     * Define o status de leitura do livro.
     * @param lido true se o livro foi lido, false caso contrário.
     */
    public void setLido(boolean lido) {
        this.lido = lido;
    }

    /**
     * Obtém a nota atribuída ao livro.
     * @return A nota do livro (0 a 10).
     */
    public int getNota() {
        return nota;
    }

    /**
     * Define a nota atribuída ao livro.
     * @param nota A nota do livro (espera-se que seja entre 0 e 10).
     */
    public void setNota(int nota) {
        this.nota = nota;
    }

    public List<Autor> getAutoresCompletos() {
        if (this.autoresCompletos == null) this.autoresCompletos = new ArrayList<>();
        return autoresCompletos;
    }
    public void setAutoresCompletos(List<Autor> autoresCompletos) { this.autoresCompletos = autoresCompletos; }

    @Override
    public String toString() { return titulo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(id, livro.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}