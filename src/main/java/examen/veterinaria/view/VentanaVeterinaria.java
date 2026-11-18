package examen.veterinaria.view;

import examen.veterinaria.controller.VeterinariaController;
import examen.veterinaria.model.Especie;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class VentanaVeterinaria extends JFrame {
    private final VeterinariaController controller;
    private final JTextField txtCedula = new JTextField(12);
    private final JTextField txtNombreCliente = new JTextField(16);
    private final JTextField txtTelefono = new JTextField(12);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombreMascota = new JTextField(12);
    private final JTextField txtEdad = new JTextField(5);
    private final JTextField txtDueno = new JTextField(12);
    private final JTextField txtFiltro = new JTextField(12);
    private final JComboBox<Especie> cmbEspecie = new JComboBox<>(Especie.values());
    private final JTextArea salida = new JTextArea(16, 64);
    private final JLabel estado = new JLabel("Sistema listo");

    public VentanaVeterinaria(VeterinariaController controller) {
        super("Clínica Veterinaria");
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", panelClientes());
        pestanas.addTab("Mascotas", panelMascotas());
        add(pestanas, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(5, 5));
        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        centro.add(new JLabel("Resultados:"), BorderLayout.NORTH);
        centro.add(new JScrollPane(salida), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.add(estado, BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);
        ejecutar(() -> salida.setText(controller.verMascotas()));
    }

    private JPanel panelClientes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        panel.add(new JLabel("Cédula:"));
        panel.add(txtCedula);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombreCliente);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);

        JButton registrar = new JButton("Registrar cliente");
        JButton listar = new JButton("Listar clientes");
        panel.add(registrar);
        panel.add(listar);

        registrar.addActionListener(e -> ejecutar(() -> {
            controller.registrarCliente(txtCedula.getText(), txtNombreCliente.getText(), txtTelefono.getText());
            salida.setText(controller.verClientes());
            estado.setText("Cliente guardado.");
        }));
        listar.addActionListener(e -> ejecutar(() -> salida.setText(controller.verClientes())));
        return panel;
    }

    private JPanel panelMascotas() {
        JPanel contenedor = new JPanel(new BorderLayout());
        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
        datos.add(new JLabel("Código:"));
        datos.add(txtCodigo);
        datos.add(new JLabel("Nombre:"));
        datos.add(txtNombreMascota);
        datos.add(new JLabel("Especie:"));
        datos.add(cmbEspecie);
        datos.add(new JLabel("Edad:"));
        datos.add(txtEdad);
        datos.add(new JLabel("Cédula dueño:"));
        datos.add(txtDueno);
        contenedor.add(datos, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton registrar = new JButton("Registrar mascota");
        JButton actualizar = new JButton("Actualizar mascota");
        JButton eliminar = new JButton("Eliminar mascota");
        JButton listar = new JButton("Listar con dueño");
        JButton filtrar = new JButton("Filtrar por cédula");
        botones.add(registrar);
        botones.add(actualizar);
        botones.add(eliminar);
        botones.add(listar);
        botones.add(new JLabel("Cédula:"));
        botones.add(txtFiltro);
        botones.add(filtrar);
        contenedor.add(botones, BorderLayout.SOUTH);

        registrar.addActionListener(e -> ejecutar(() -> {
            controller.registrarMascota(txtCodigo.getText(), txtNombreMascota.getText(),
                    (Especie) cmbEspecie.getSelectedItem(), txtEdad.getText(), txtDueno.getText());
            salida.setText(controller.verMascotas());
            estado.setText("Mascota registrada.");
        }));
        actualizar.addActionListener(e -> ejecutar(() -> {
            controller.actualizarMascota(txtCodigo.getText(), txtNombreMascota.getText(),
                    (Especie) cmbEspecie.getSelectedItem(), txtEdad.getText(), txtDueno.getText());
            salida.setText(controller.verMascotas());
            estado.setText("Mascota actualizada.");
        }));
        eliminar.addActionListener(e -> ejecutar(() -> {
            controller.eliminarMascota(txtCodigo.getText());
            salida.setText(controller.verMascotas());
            estado.setText("Mascota eliminada.");
        }));
        listar.addActionListener(e -> ejecutar(() -> salida.setText(controller.verMascotas())));
        filtrar.addActionListener(e -> ejecutar(() -> salida.setText(controller.buscarPorCliente(txtFiltro.getText()))));
        txtFiltro.addActionListener(e -> filtrar.doClick());
        return contenedor;
    }

    private void ejecutar(Runnable tarea) {
        try {
            tarea.run();
        } catch (RuntimeException e) {
            estado.setText("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Operación no realizada",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
