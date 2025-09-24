package br.com.fiap.acompanha.infrastructure.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    Connection getConnection() throws SQLException;

}
