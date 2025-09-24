package br.com.fiap.acompanha.infrastructure.persistence;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//Impl = IMPLEMENT
public class DatabaseConnectionImpl implements DatabaseConnection {

    private final DataSource dataSource;

    public DatabaseConnectionImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

}
