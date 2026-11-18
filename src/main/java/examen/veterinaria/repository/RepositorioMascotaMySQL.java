package examen.veterinaria.repository;

import examen.veterinaria.model.Cliente;
import examen.veterinaria.model.Especie;
import examen.veterinaria.model.Mascota;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepositorioMascotaMySQL implements RepositorioMascota {
    private final String url;
    private final String usuario;
    private final String clave;

    public RepositorioMascotaMySQL() {
        this(System.getenv("VET_DB_URL"),
                System.getenv("VET_DB_USER"),
                System.getenv("VET_DB_PASSWORD"));
    }

    RepositorioMascotaMySQL(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    @Override
    public List<Mascota> cargarTodos() {
        List<Mascota> mascotas = new ArrayList<>();
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall("{CALL sp_listar_mascotas()}");
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                mascotas.add(mapear(resultado));
            }
            return mascotas;
        } catch (SQLException e) {
            throw fallo("consultar las mascotas", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("MySQL devolvió datos de mascota inválidos.", e);
        }
    }

    @Override
    public void insertar(Mascota mascota) {
        ejecutarGuardado("{CALL sp_insertar_mascota(?,?,?,?,?)}", mascota, "registrar la mascota");
    }

    @Override
    public void actualizar(Mascota mascota) {
        ejecutarGuardado("{CALL sp_actualizar_mascota(?,?,?,?,?)}", mascota, "actualizar la mascota");
    }

    @Override
    public void eliminar(String codigo) {
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall("{CALL sp_eliminar_mascota(?)}")) {
            sentencia.setString(1, codigo);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw fallo("eliminar la mascota", e);
        }
    }

    @Override
    public List<String> listarConDueno() {
        List<String> filas = new ArrayList<>();
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall("{CALL sp_listar_mascotas_con_dueno()}");
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                Mascota mascota = mapear(resultado);
                filas.add(mascota + " | Cliente: " + resultado.getString("nombre_cliente"));
            }
            return filas;
        } catch (SQLException e) {
            throw fallo("consultar las mascotas con su dueño", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("MySQL devolvió datos de mascota inválidos.", e);
        }
    }

    private void ejecutarGuardado(String llamada, Mascota mascota, String operacion) {
        try (Connection conexion = conectar();
             CallableStatement sentencia = conexion.prepareCall(llamada)) {
            sentencia.setString(1, mascota.getCodigo());
            sentencia.setString(2, mascota.getNombre());
            sentencia.setString(3, mascota.getEspecie().name());
            sentencia.setInt(4, mascota.getEdad());
            sentencia.setString(5, mascota.getCedulaCliente());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw fallo(operacion, e);
        }
    }

    private Mascota mapear(ResultSet resultado) throws SQLException {
        return new Mascota(
                resultado.getString("codigo"),
                resultado.getString("nombre"),
                Especie.valueOf(resultado.getString("especie").toUpperCase(Locale.ROOT)),
                resultado.getInt("edad"),
                resultado.getString("cedula_cliente")
        );
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
