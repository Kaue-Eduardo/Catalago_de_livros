package Persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import Modelo.Autor;
import Modelo.Livro; // Importar Livro para a verificação de exclusão
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DAO (Data Access Object) para a entidade Autor.
 * Contém métodos para realizar operações CRUD (Create, Read, Update, Delete)
 * de autores no banco de dados MongoDB.
 */
public class AutorDAO {

    private static final String COLLECTION_NAME = "autores";
    private MongoCollection<Autor> colecaoAutores;
    private LivroDAO livroDAO;

    public AutorDAO() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.colecaoAutores = database.getCollection(COLLECTION_NAME, Autor.class);
    }

    public void setLivroDAO(LivroDAO livroDAO) {
        this.livroDAO = livroDAO;
    }

    public void adicionarAutor(Autor autor) {
        colecaoAutores.insertOne(autor);
    }

    public Autor buscarAutorPorId(ObjectId id) {
        if (id == null) return null;
        return colecaoAutores.find(Filters.eq("_id", id)).first();
    }

    public List<Autor> listarTodosAutores() {
        List<Autor> autores = new ArrayList<>();
        colecaoAutores.find().sort(Sorts.ascending("nome")).into(autores); // Usa Sorts
        return autores;
    }

    public List<Autor> buscarAutoresPorNome(String termoBusca) {
        List<Autor> autores = new ArrayList<>();
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            return listarTodosAutores();
        }
        Pattern regex = Pattern.compile(Pattern.quote(termoBusca.trim()), Pattern.CASE_INSENSITIVE);
        colecaoAutores.find(Filters.regex("nome", regex)).sort(Sorts.ascending("nome")).into(autores); // Usa Sorts
        return autores;
    }

    /**
     * Filtra autores com base no nome e/ou intervalo de ano de nascimento.
     * @param nome Parte do nome do autor (case-insensitive).
     * @param anoNascimentoInicio Ano de nascimento inicial do intervalo (inclusivo).
     * @param anoNascimentoFim Ano de nascimento final do intervalo (inclusivo).
     * @return Uma lista de autores que correspondem aos critérios, ordenada por nome.
     */
    public List<Autor> filtrarAutores(String nome, Integer anoNascimentoInicio, Integer anoNascimentoFim) {
        List<Bson> filtros = new ArrayList<>(); // Usa Bson

        if (nome != null && !nome.trim().isEmpty()) {
            Pattern regexNome = Pattern.compile(Pattern.quote(nome.trim()), Pattern.CASE_INSENSITIVE);
            filtros.add(Filters.regex("nome", regexNome));
        }
        if (anoNascimentoInicio != null) {
            filtros.add(Filters.gte("anoNascimento", anoNascimentoInicio));
        }
        if (anoNascimentoFim != null) {
            filtros.add(Filters.lte("anoNascimento", anoNascimentoFim));
        }

        List<Autor> autoresFiltrados = new ArrayList<>();
        Bson filtroFinal = filtros.isEmpty() ? Filters.empty() : Filters.and(filtros); // Usa Bson

        colecaoAutores.find(filtroFinal).sort(Sorts.ascending("nome")).into(autoresFiltrados); // Usa Sorts
        return autoresFiltrados;
    }

    public boolean atualizarAutor(Autor autor) {
        if (autor.getId() == null) {
            System.err.println("Tentativa de atualizar autor sem ID.");
            return false;
        }
        UpdateResult result = colecaoAutores.replaceOne(Filters.eq("_id", autor.getId()), autor);
        return result.getModifiedCount() > 0;
    }

    public boolean excluirAutor(ObjectId autorId) throws IllegalStateException {
        if (livroDAO == null) {
            throw new IllegalStateException("LivroDAO não configurado em AutorDAO. Não é possível verificar dependências.");
        }
        List<Livro> livrosComEsteAutorComoUnico = livroDAO.listarLivrosPorUnicoAutor(autorId);
        if (!livrosComEsteAutorComoUnico.isEmpty()) {
            StringBuilder titulos = new StringBuilder();
            for(Livro l : livrosComEsteAutorComoUnico) {
                if(titulos.length() > 0) titulos.append(", ");
                titulos.append("'").append(l.getTitulo()).append("'");
            }
            throw new IllegalStateException("Autor não pode ser excluído, pois é o único autor do(s) livro(s): " + titulos.toString() + ".");
        }
        livroDAO.removerReferenciaAutorDeLivros(autorId);
        DeleteResult result = colecaoAutores.deleteOne(Filters.eq("_id", autorId));
        if (result.getDeletedCount() == 0) {
            System.err.println("Nenhum autor encontrado com o ID para exclusão: " + autorId);
            return false;
        }
        return true;
    }
}