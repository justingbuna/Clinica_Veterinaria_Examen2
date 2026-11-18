package examen.veterinaria.repository;

import examen.veterinaria.model.Cliente;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static jdk.dynalink.StandardOperation.CALL;

public class RepositorioClienteMySQL implements RepositorioCliente {
    private final String url;
    private final String usuario;
    private final String clave;

    public RepositorioClienteMySQL() {
        this(System.getenv("VET_DB_URL"),
                System.getenv("VET_DB_USER"),
                System.getenv("VET_DB_PASSWORD"));
    }

    RepositorioClienteMySQL(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    @Override
    public List<Cliente> cargarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall("{CALL sp_listar_clientes()}");
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                clientes.add(new Cliente(
                        resultado.getString("cedula"),
                        resultado.getString("nombre"),
                        resultado.getString("telefono")
                ));
            }
            return clientes;
        } catch (SQLException e) {
            throw fallo("consultar los clientes", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("MySQL devolvió datos de cliente inválidos.", e);
        }
    }

    @Override
    public void insertar(Cliente cliente) {
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall("{CALL sp_insertar_cliente(?,?,?)}")) {
            sentencia.setString(1, cliente.getCedula());
            sentencia.setString(2, cliente.getNombre());
            sentencia.setString(3, cliente.getTelefono());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw fallo("registrar el cliente", e);
        }
    }

    private Connection conectar() throws SQLException {
        validarConfiguracion();
        return DriverManager.getConnection(url, usuario, clave);
    }

    private void validarConfiguracion() {
        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()
                || clave == null || clave.isBlank()) {
            throw new PersistenciaException(
                    "Configure VET_DB_URL, VET_DB_USER y VET_DB_PASSWORD en la ejecución de IntelliJ."
            );
        }
    }

    private PersistenciaException fallo(String operacion, SQLException causa) {
        return new PersistenciaException(
                "No fue posible " + operacion + ". Verifique la conexión con MySQL y los datos ingresados.",
                causa
        );
    }


}
