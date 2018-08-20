package org.apache.solr.handler.dataimport.dto;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.apache.solr.handler.dataimport.DataImportHandlerException;
import org.apache.solr.handler.dataimport.properties.DataSourceXmlProperties;

import java.util.Arrays;
import java.util.Properties;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

/**
 * @author Guilherme Viterbo Galvão
 */
public final class DataSourceDTO {

    public static final DataSourceDTO getInstance(Properties initProps) throws NumberFormatException {
        return new DataSourceDTO(
            initProps.getProperty(DataSourceXmlProperties.MONGO_HOST.getPropertieName()),
            Integer.parseInt(initProps.getProperty(DataSourceXmlProperties.MONGO_PORT.getPropertieName())),
            initProps.getProperty(DataSourceXmlProperties.MONGO_USERNAME.getPropertieName()),
            initProps.getProperty(DataSourceXmlProperties.MONGO_PASSWORD.getPropertieName()),
            initProps.getProperty(DataSourceXmlProperties.MONGO_DATABASE.getPropertieName())
        );
    }

    private DataSourceDTO (
        String host,
        Integer port,
        String username,
        String password,
        String database
    ) {
        if (database == null) {
            throw new DataImportHandlerException(SEVERE, "database can not be null");
        }
        this.host = host == null || host.isEmpty() ? DataSourceXmlProperties.DEFAULT_HOST.getPropertieName() : host.trim();
        this.port = port == null || port <= 0 ? Integer.parseInt(DataSourceXmlProperties.DEFAULT_PORT.getPropertieName()) : port;
        this.username = username != null ? username.trim() : "";
        this.password = password != null ? password.trim() : "";
        this.database = database != null ? database.trim() : "";
    }

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String database;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    private MongoCredential mongoCredential;

    public MongoCredential getMongoCredential() {
        if (mongoCredential == null) {
            synchronized (this) {
                if (mongoCredential == null) {
                    mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
                }
            }
        }
        return mongoCredential;
    }

    private MongoClient mongoClient;

    public MongoClient getMongoClient() {
        if (mongoClient == null) {
            synchronized (this) {
                if (mongoClient == null) {
                    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                        mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(getMongoCredential()));
                    } else {
                        mongoClient = new MongoClient(new ServerAddress(host, port));
                    }
                }
            }
        }
        return mongoClient;
    }

    private MongoDatabase mongoDatabase;

    public MongoDatabase getMongoDatabase() {
        if (mongoDatabase == null) {
            synchronized (this) {
                if (mongoDatabase == null) {
                    mongoDatabase = getMongoClient().getDatabase(database);
                }
            }
        }
        return mongoDatabase;
    }
}
