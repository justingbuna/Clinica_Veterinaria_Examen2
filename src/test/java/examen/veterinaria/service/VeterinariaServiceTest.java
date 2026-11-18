package examen.veterinaria.service;

import examen.veterinaria.model.Cliente;
import examen.veterinaria.model.Especie;
import examen.veterinaria.model.Mascota;
import examen.veterinaria.repository.RepositorioCliente;
import examen.veterinaria.repository.RepositorioMascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VeterinariaServiceTest {
    private RepositorioClientePrueba clientes;
    private RepositorioMascotaPrueba mascotas;
    private VeterinariaService servicio;

    @BeforeEach
    void preparar() {
        clientes = new RepositorioClientePrueba();
        mascotas = new RepositorioMascotaPrueba();
        servicio = new VeterinariaService(clientes, mascotas);
    }

    @Test
    void registraClienteConDatosValidos() {
        servicio.registrarCliente("101", "Ana Mora", "88881111");

        assertEquals(1, clientes.datos.size());
        assertEquals("Ana Mora", clientes.datos.getFirst().getNombre());
    }

    @Test
    void rechazaCedulaDuplicadaSinInsertar() {
        clientes.datos.add(new Cliente("101", "Ana Mora", "88881111"));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarCliente("101", "Otra persona", "87772222"));
        assertEquals(1, clientes.datos.size());
    }

    @Test
    void rechazaMascotaCuandoElDuenoNoExiste() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMascota("M01", "Luna", Especie.GATO, 3, "999"));
        assertEquals(0, mascotas.datos.size());
    }

    @Test
    void filtraMascotasPorCedulaDelCliente() {
        mascotas.datos.add(new Mascota("M01", "Luna", Especie.GATO, 3, "101"));
        mascotas.datos.add(new Mascota("M02", "Rocky", Especie.PERRO, 5, "202"));
        mascotas.datos.add(new Mascota("M03", "Nina", Especie.PERRO, 1, "101"));

        List<Mascota> resultado = servicio.mascotasPorCliente("101");

        assertEquals(2, resultado.size());
        assertEquals("M01", resultado.get(0).getCodigo());
        assertEquals("M03", resultado.get(1).getCodigo());
    }

    @Test
    void rechazaEdadFueraDelRangoOriginal() {
        clientes.datos.add(new Cliente("101", "Ana Mora", "88881111"));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMascota("M01", "Luna", Especie.GATO, 41, "101"));
        assertEquals(0, mascotas.datos.size());
    }

    private static class RepositorioClientePrueba implements RepositorioCliente {
        private final List<Cliente> datos = new ArrayList<>();

        @Override
        public List<Cliente> cargarTodos() {
            return new ArrayList<>(datos);
        }

        @Override
        public void insertar(Cliente cliente) {
            datos.add(cliente);
        }
    }

    private static class RepositorioMascotaPrueba implements RepositorioMascota {
        private final List<Mascota> datos = new ArrayList<>();

        @Override
        public List<Mascota> cargarTodos() {
            return new ArrayList<>(datos);
        }

        @Override
        public void insertar(Mascota mascota) {
            datos.add(mascota);
        }

        @Override
        public void actualizar(Mascota mascota) {
            for (int indice = 0; indice < datos.size(); indice++) {
                if (datos.get(indice).getCodigo().equalsIgnoreCase(mascota.getCodigo())) {
                    datos.set(indice, mascota);
                    return;
                }
            }
            throw new IllegalArgumentException("Mascota no encontrada.");
        }

        @Override
        public void eliminar(String codigo) {
            datos.removeIf(mascota -> mascota.getCodigo().equalsIgnoreCase(codigo));
        }

        @Override
        public List<String> listarConDueno() {
            return List.of();
        }
    }
}
