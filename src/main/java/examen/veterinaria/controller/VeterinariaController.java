package examen.veterinaria.controller;

import examen.veterinaria.model.Cliente;
import examen.veterinaria.model.Especie;
import examen.veterinaria.model.Mascota;
import examen.veterinaria.service.VeterinariaService;

import java.util.List;

public class VeterinariaController {
    private final VeterinariaService servicio;

    public VeterinariaController(VeterinariaService servicio) {
        this.servicio = servicio;
    }

    public void registrarCliente(String cedula, String nombre, String telefono) {
        servicio.registrarCliente(cedula, nombre, telefono);
    }

    public void registrarMascota(String codigo, String nombre, Especie especie, String edad, String cedula) {
        servicio.registrarMascota(codigo, nombre, especie, convertirEdad(edad), cedula);
    }

    public void actualizarMascota(String codigo, String nombre, Especie especie, String edad, String cedula) {
        servicio.actualizarMascota(codigo, nombre, especie, convertirEdad(edad), cedula);
    }

    public void eliminarMascota(String codigo) {
        servicio.eliminarMascota(codigo);
    }

    public String verClientes() {
        List<Cliente> datos = servicio.listarClientes();
        if (datos.isEmpty()) return "No hay clientes registrados.";
        StringBuilder texto = new StringBuilder();
        for (Cliente cliente : datos) texto.append(cliente).append('\n');
        return texto.toString();
    }












    public String verMascotas() {
        return servicio.listarMascotasConDueno();
    }

    public String buscarPorCliente(String cedula) {
        List<Mascota> datos = servicio.mascotasPorCliente(cedula);
        if (datos.isEmpty()) return "No hay mascotas para esa cédula.";
        StringBuilder texto = new StringBuilder();
        for (Mascota mascota : datos) texto.append(mascota).append('\n');
        return texto.toString();
    }

    private int convertirEdad(String edad) {
        if (edad == null || edad.isBlank()) {
            throw new IllegalArgumentException("Indique la edad de la mascota.");
        }
        try {
            return Integer.parseInt(edad.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La edad debe ser un número entero.");
        }
    }
}
