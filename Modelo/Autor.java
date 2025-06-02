package Modelo;

import org.bson.types.ObjectId;
import java.util.Objects;

/**
 * Representa um autor de um livro.
 * Cada autor possui um ID único, nome e ano de nascimento.
 */
public class Autor {

    private ObjectId id;
    private String nome;
    private int anoNascimento;

    /**
     * Construtor padrão.
     */
    public Autor() {
    }

    /**
     * Construtor para criar um autor com nome e ano de nascimento.
     * O ID é gerado automaticamente pelo MongoDB ao ser inserido se este construtor
     * for usado para um novo autor e o campo id não for setado antes da inserção.
     * @param nome O nome do autor.
     * @param anoNascimento O ano de nascimento do autor.
     */
    public Autor(String nome, int anoNascimento) {
        this.nome = nome;
        this.anoNascimento = anoNascimento;
    }
    
    /**
     * Construtor para criar um autor com ID, nome e ano de nascimento.
     * Usado principalmente ao recuperar dados do MongoDB.
     * @param id O ObjectId do autor.
     * @param nome O nome do autor.
     * @param anoNascimento O ano de nascimento do autor.
     */
    public Autor(ObjectId id, String nome, int anoNascimento) {
        this.id = id;
        this.nome = nome;
        this.anoNascimento = anoNascimento;
    }

    /**
     * Obtém o ID do autor.
     * @return O ObjectId do autor. Pode ser null se o autor ainda não foi persistido.
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Define o ID do autor.
     * Geralmente definido pelo MongoDB após a inserção, ou ao carregar do banco.
     * @param id O ObjectId do autor.
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Obtém o nome do autor.
     * @return O nome do autor.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do autor.
     * @param nome O nome do autor.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o ano de nascimento do autor.
     * @return O ano de nascimento.
     */
    public int getAnoNascimento() {
        return anoNascimento;
    }

    /**
     * Define o ano de nascimento do autor.
     * @param anoNascimento O ano de nascimento.
     */
    public void setAnoNascimento(int anoNascimento) {
        this.anoNascimento = anoNascimento;
    }

    /**
     * Retorna uma representação em String do nome do autor.
     * Usado frequentemente em JComboBoxes ou JLists para exibição.
     * @return O nome do autor.
     */
    @Override
    public String toString() {
        return nome; 
    }

    /**
     * Compara este Autor com outro objeto para igualdade.
     * Dois autores são considerados iguais se seus IDs são iguais e não nulos.
     * Se ambos os IDs forem nulos, compara pelos outros campos.
     * @param o O objeto a ser comparado.
     * @return true se os objetos são iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        if (id != null && autor.id != null) {
            return id.equals(autor.id);
        }
        // Se os IDs são nulos, compara por outros campos (para autores ainda não persistidos)
        // Embora o ideal seja que a igualdade seja baseada em ID após persistência.
        return anoNascimento == autor.anoNascimento &&
               Objects.equals(nome, autor.nome);
    }

    /**
     * Retorna o código hash para este Autor.
     * Baseado no ID se não nulo, caso contrário nos outros campos.
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(nome, anoNascimento);
    }
}