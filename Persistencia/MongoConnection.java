package Persistencia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation; // Corrigido: UuidRepresentation é de org.bson
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * Classe utilitária para gerenciar a conexão com o MongoDB.
 * Fornece um método para obter a instância do banco de dados da biblioteca.
 */
public class MongoConnection {

    // ATENÇÃO: Substitua "localhost:27017" pela string de conexão do seu MongoDB se for diferente.
    private static final String CONNECTION_STRING = "mongodb://localhost:27017"; 
    private static final String DATABASE_NAME = "biblioteca_swing_mongo"; 
    private static MongoClient mongoClientInstance;
    private static MongoDatabase databaseInstance;

    /**
     * Construtor privado para evitar instanciação (padrão Singleton).
     */
    private MongoConnection() {
    }

    /**
     * Obtém uma instância do cliente MongoDB.
     * Cria uma nova instância se nenhuma existir (Singleton).
     * Configura o codec para POJOs (Plain Old Java Objects).
     * @return Uma instância de MongoClient.
     */
    private static MongoClient getMongoClient() {
        if (mongoClientInstance == null) {
            // Configuração para o PojoCodecProvider funcionar corretamente com POJOs
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
                    PojoCodecProvider.builder().automatic(true).build());
            
            // Combina o codec padrão com o codec POJO
            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(), 
                    pojoCodecRegistry);

            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .codecRegistry(codecRegistry)
                    // Configurar representação de UUID se for usar Java UUIDs nativamente com MongoDB
                    // Para ObjectId, esta configuração não é diretamente necessária, mas é bom ter se usar UUIDs.
                    .uuidRepresentation(UuidRepresentation.STANDARD) 
                    .build();
            
            mongoClientInstance = MongoClients.create(clientSettings);
        }
        return mongoClientInstance;
    }
    
    /**
     * Obtém a instância do banco de dados da biblioteca.
     * Se a instância do banco de dados ainda não existir, ela é criada.
     * Utiliza o PojoCodecProvider para permitir o mapeamento automático de POJOs.
     * @return A instância do MongoDatabase configurada para a biblioteca.
     */
    public static MongoDatabase getDatabase() {
        if (databaseInstance == null) {
            MongoClient client = getMongoClient(); // Garante que o cliente está inicializado com o codec POJO
            databaseInstance = client.getDatabase(DATABASE_NAME);
        }
        return databaseInstance;
    }

    /**
     * Fecha a conexão com o MongoDB.
     * Deve ser chamado ao finalizar a aplicação para liberar recursos.
     * É uma boa prática adicionar um shutdown hook na aplicação principal para chamar este método.
     */
    public static void close() {
        if (mongoClientInstance != null) {
            try {
                mongoClientInstance.close();
                System.out.println("Conexão com MongoDB fechada.");
            } catch (Exception e) {
                System.err.println("Erro ao fechar a conexão com MongoDB: " + e.getMessage());
            } finally {
                mongoClientInstance = null;
                databaseInstance = null; 
            }
        }
    }
}