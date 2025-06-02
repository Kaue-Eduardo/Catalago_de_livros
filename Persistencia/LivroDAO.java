package Persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts; // Para ordenação
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import Modelo.Autor;
import Modelo.Livro;
import org.bson.conversions.Bson; // Para construir filtros complexos
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DAO (Data Access Object) para a entidade Livro.
 * Contém métodos para realizar operações CRUD (Create, Read, Update, Delete)
 * de livros no banco de dados MongoDB.
 */
public class LivroDAO {

    private static final String COLLECTION_NAME = "livros";
    private MongoCollection<Livro> colecaoLivros;
    private AutorDAO autorDAO;

    public LivroDAO() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.colecaoLivros = database.getCollection(COLLECTION_NAME, Livro.class);
    }

    public void setAutorDAO(AutorDAO autorDAO) {
        this.autorDAO = autorDAO;
    }

    public void adicionarLivro(Livro livro) {
        colecaoLivros.insertOne(livro);
    }

    public Livro buscarLivroPorId(ObjectId id) {
        if (id == null) return null;
        Livro livro = colecaoLivros.find(Filters.eq("_id", id)).first();
        if (livro != null) {
            popularAutoresCompletos(livro);
        }
        return livro;
    }

    public List<Livro> listarTodosLivros() {
        List<Livro> livros = new ArrayList<>();
        colecaoLivros.find().sort(Sorts.ascending("titulo")).into(livros);
        for (Livro livro : livros) {
            popularAutoresCompletos(livro);
        }
        return livros;
    }
    
    /**
     * Filtra livros com base nos critérios fornecidos, incluindo intervalo de ano de publicação.
     * Qualquer critério null ou vazio é ignorado na construção do filtro.
     * @param titulo Termo para buscar no título (parcial, case-insensitive).
     * @param nomeAutor Termo para buscar no nome de um dos autores (parcial, case-insensitive).
     * @param genero Gênero para filtrar (exato, case-insensitive).
     * @param anoInicio Ano de publicação inicial do intervalo (inclusivo).
     * @param anoFim Ano de publicação final do intervalo (inclusivo).
     * @return Lista de livros que atendem aos critérios, ordenados por título.
     */
    public List<Livro> filtrarLivros(String titulo, String nomeAutor, String genero, Integer anoInicio, Integer anoFim) {
        if (autorDAO == null && (nomeAutor != null && !nomeAutor.isEmpty()) ) {
             throw new IllegalStateException("AutorDAO não configurado em LivroDAO. Não é possível filtrar por nome de autor.");
        }

        List<Bson> filtrosPrincipais = new ArrayList<>();

        if (titulo != null && !titulo.trim().isEmpty()) {
            Pattern regexTitulo = Pattern.compile(Pattern.quote(titulo.trim()), Pattern.CASE_INSENSITIVE);
            filtrosPrincipais.add(Filters.regex("titulo", regexTitulo));
        }
        
        if (nomeAutor != null && !nomeAutor.trim().isEmpty()) {
            List<Autor> autoresEncontrados = autorDAO.buscarAutoresPorNome(nomeAutor.trim());
            if (autoresEncontrados.isEmpty()) {
                return new ArrayList<>(); 
            }
            List<ObjectId> idsAutoresEncontrados = new ArrayList<>();
            for (Autor autor : autoresEncontrados) {
                idsAutoresEncontrados.add(autor.getId());
            }
            filtrosPrincipais.add(Filters.in("autoresIds", idsAutoresEncontrados));
        }
        
        if (genero != null && !genero.trim().isEmpty() && !genero.equalsIgnoreCase("Todos os Gêneros")) { 
             Pattern regexGenero = Pattern.compile("^" + Pattern.quote(genero.trim()) + "$", Pattern.CASE_INSENSITIVE);
            filtrosPrincipais.add(Filters.regex("genero", regexGenero));
        }

        // Adiciona filtro por intervalo de ano de publicação
        if (anoInicio != null) {
            filtrosPrincipais.add(Filters.gte("anoPublicacao", anoInicio));
        }
        if (anoFim != null) {
            filtrosPrincipais.add(Filters.lte("anoPublicacao", anoFim));
        }
        // Validação de anoInicio <= anoFim já deve ser feita na GUI ou aqui se preferir.
        // Se anoInicio > anoFim, a query pode não retornar resultados ou ser inválida,
        // mas o MongoDB geralmente lida com isso retornando zero documentos.

        List<Livro> livrosFiltrados = new ArrayList<>();
        Bson filtroFinal = filtrosPrincipais.isEmpty() ? Filters.empty() : Filters.and(filtrosPrincipais);
        
        colecaoLivros.find(filtroFinal).sort(Sorts.ascending("titulo")).into(livrosFiltrados);
        
        for (Livro livro : livrosFiltrados) {
            popularAutoresCompletos(livro);
        }
        return livrosFiltrados;
    }


    public boolean atualizarLivro(Livro livro) {
        // ... (sem alterações neste método) ...
        if (livro.getId() == null) {
            System.err.println("Tentativa de atualizar livro sem ID.");
            return false; 
        }
        UpdateResult result = colecaoLivros.replaceOne(Filters.eq("_id", livro.getId()), livro);
        return result.getModifiedCount() > 0;
    }

    public boolean excluirLivro(ObjectId id) {
        // ... (sem alterações neste método) ...
        DeleteResult result = colecaoLivros.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    private void popularAutoresCompletos(Livro livro) {
        // ... (sem alterações neste método) ...
        if (autorDAO == null) {
            livro.setAutoresCompletos(new ArrayList<>());
            return;
        }
        if (livro.getAutoresIds() != null && !livro.getAutoresIds().isEmpty()) {
            List<Autor> autores = new ArrayList<>();
            for (ObjectId autorId : livro.getAutoresIds()) {
                Autor autor = autorDAO.buscarAutorPorId(autorId);
                if (autor != null) {
                    autores.add(autor);
                } else {
                    System.err.println("Aviso: Autor com ID " + autorId + " não encontrado, mas referenciado no livro '" + livro.getTitulo() + "'.");
                }
            }
            livro.setAutoresCompletos(autores);
        } else {
            livro.setAutoresCompletos(new ArrayList<>());
        }
    }
    
    public List<Livro> listarLivrosPorUnicoAutor(ObjectId autorId) {
        // ... (sem alterações neste método) ...
        List<Livro> livrosEncontrados = new ArrayList<>();
        colecaoLivros.find(
            Filters.and(
                Filters.eq("autoresIds", autorId), 
                Filters.size("autoresIds", 1)      
            )
        ).into(livrosEncontrados);
        return livrosEncontrados;
    }

    public long removerReferenciaAutorDeLivros(ObjectId autorId) {
        // ... (sem alterações neste método) ...
        UpdateResult result = colecaoLivros.updateMany(
            Filters.eq("autoresIds", autorId), 
            Updates.pull("autoresIds", autorId) 
        );
        return result.getModifiedCount();
    }
}