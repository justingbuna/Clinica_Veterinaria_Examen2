package examen.veterinaria.service;

import examen.veterinaria.model.Cliente;
import examen.veterinaria.model.Especie;
import examen.veterinaria.model.Mascota;
import examen.veterinaria.repository.RepositorioCliente;
import examen.veterinaria.repository.RepositorioMascota;

import java.util.ArrayList;
import java.util.List;

public class VeterinariaService {
    private final RepositorioCliente clientes;
    private final RepositorioMascota mascotas;

    public VeterinariaService(RepositorioCliente clientes, RepositorioMascota mascotas) {
        this.clientes = clientes;
        this.mascotas = mascotas;
    }

    public List<Cliente> listarClientes() {
        return clientes.cargarTodos();
    }

    public List<Mascota> listarMascotas() {
        return mascotas.cargarTodos();
    }

    public Cliente buscarCliente(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("Indique la cédula.");
        }
        for (Cliente cliente : clientes.cargarTodos()) {
            if (cliente.getCedula().equalsIgnoreCase(cedula.trim())) {
                return cliente;
            }
        }
        return null;
    }

    public void registrarCliente(String cedula, String nombre, String telefono) {
        Cliente cliente = new Cliente(cedula, nombre, telefono);
        if (buscarCliente(cliente.getCedula()) != null) {
            throw new IllegalArgumentException("La cédula ya está registrada.");
        }
        clientes.insertar(cliente);
    }

    public void registrarMascota(String codigo, String nombre, Especie especie,
                                 int edad, String cedulaCliente) {
        Mascota mascota = new Mascota(codigo, nombre, especie, edad, cedulaCliente);
        verificarDueno(mascota.getCedulaCliente());
        for (Mascota existente : listarMascotas()) {
            if (existente.getCodigo().equalsIgnoreCase(mascota.getCodigo())) {
                throw new IllegalArgumentException("El código de mascota ya existe.");
            }
        }
        mascotas.insertar(mascota);
    }

    public void actualizarMascota(String codigo, String nombre, Especie especie,
                                  int edad, String cedulaCliente) {
        Mascota mascota = new Mascota(codigo, nombre, especie, edad, cedulaCliente);
        verificarDueno(mascota.getCedulaCliente());
        mascotas.actualizar(mascota);
    }

    public void eliminarMascota(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Indique el código.");
        }
        mascotas.eliminar(codigo.trim());
    }

    public List<Mascota> mascotasPorCliente(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("Indique la cédula para filtrar.");
        }
        List<Mascota> resultado = new ArrayList<>();
        for (Mascota mascota : mascotas.cargarTodos()) {
            if (mascota.getCedulaCliente().equalsIgnoreCase(cedula.trim())) {
                resultado.add(mascota);
            }
        }
        return resultado;
    }

    public String listarMascotasConDueno() {
        List<String> filas = mascotas.listarConDueno();
        if (filas.isEmpty()) return "No hay mascotas registradas.";
        StringBuilder texto = new StringBuilder();
        for (String fila : filas) texto.append(fila).append('\n');
        return texto.toString();
    }

    private void verificarDueno(String cedula) {
        if (buscarCliente(cedula) == null) {
            throw new IllegalArgumentException("Registre primero al cliente dueño de la mascota.");
        }
    }
}
