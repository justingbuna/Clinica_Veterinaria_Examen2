package examen.veterinaria;

import examen.veterinaria.controller.VeterinariaController;
import examen.veterinaria.repository.RepositorioCliente;
import examen.veterinaria.repository.RepositorioClienteMySQL;
import examen.veterinaria.repository.RepositorioMascota;
import examen.veterinaria.repository.RepositorioMascotaMySQL;
import examen.veterinaria.service.VeterinariaService;
import examen.veterinaria.view.VentanaVeterinaria;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RepositorioCliente clientes = new RepositorioClienteMySQL();
            RepositorioMascota mascotas = new RepositorioMascotaMySQL();
            VeterinariaService service = new VeterinariaService(clientes, mascotas);
            new VentanaVeterinaria(new VeterinariaController(service)).setVisible(true);
        });
    }
}
