package com.mandeng;


import com.mandeng.dao.UserRepositoryImpl;
import com.mandeng.models.User;
import lombok.extern.jbosslog.JBossLog;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.*;

/**
 * MySQLUserStorageProviderFactory
 */
@JBossLog
public class MySQLUserStorageProviderFactory implements UserStorageProviderFactory<MySQLUserStorageProvider> {

    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DATABASE_NAME = "newuserstorage";
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "regnesurlecielgele00";

    @Override
    public MySQLUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        Map properties = new HashMap();
        properties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.put("hibernate.connection.url", "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        properties.put("hibernate.connection.username", USER_NAME);
        properties.put("hibernate.connection.password", PASSWORD);
        properties.put("hibernate.show-sql", "true");
        properties.put("hibernate.archive.autodetection", "class, hbm");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.connection.autocommit", "true");

        EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(getPersistenceUnitInfo("mysql"), properties);
        UserRepositoryImpl userDAO = new UserRepositoryImpl(entityManagerFactory.createEntityManager());
        return new MySQLUserStorageProvider(session, model, userDAO);
    }

    private PersistenceUnitInfo getPersistenceUnitInfo(String name) {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return name;
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                try {
                    return Collections.list(this.getClass()
                        .getClassLoader()
                        .getResources(""));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                List<String> managedClasses = new LinkedList<>();
                managedClasses.add(User.class.getName());
                return managedClasses;
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return SharedCacheMode.UNSPECIFIED;
            }

            @Override
            public ValidationMode getValidationMode() {
                return ValidationMode.AUTO;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return "2.2";
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {
            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }

    @Override
    public String getId() {
        return "mysql-provider";
    }

}
