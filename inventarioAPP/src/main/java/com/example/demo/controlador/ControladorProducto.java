package com.example.demo.controlador;


import com.example.demo.modelo.Producto;
import com.example.demo.modelo.RepositorioProducto;
import com.example.demo.vista.DialogActualizar;
import com.example.demo.vista.FormProductos;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ControladorProducto implements ActionListener, KeyListener {

    RepositorioProducto repositorioProducto;
    FormProductos formProductos;
    //con este constructor argamos los eventos y la tabla
    public ControladorProducto(RepositorioProducto repositorio, FormProductos formProductos) {
        this.repositorioProducto = repositorio;
        this.formProductos = formProductos;
        agregarEventos();
        listar();
    }
    //Metodo encargado de adicionar un acionListnes a los botones
    private void agregarEventos() {
        formProductos.getBotonAgregar().addActionListener(this);
        formProductos.getBotonActualizar().addActionListener(this);
        formProductos.getBotonBorrar().addActionListener(this);
        formProductos.getBotonInforme().addActionListener(this);
        formProductos.getTextPrecio().addKeyListener(this);
        formProductos.getTextInventario().addKeyListener(this);
    }
    //obtener todos los registros  de la db y cargarlos a la tabla
    private List<Producto> listar() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        //nombre de columnas
        String[] nombrecolumnas = {"Codigo", "Nombre", "Precio", "Inventario"};
        //Objectec se refiere a los registros de la base de datos
        Object[][] datos = new Object[listaproductos.size()][4];
        int i = 0;
        //llammamos DecimalFormat para asignarle un formato a las columnas 
        //y mejorar presentacion
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        //asignamos los registros a la base de datos
        for (Producto lp : listaproductos) {
            Object[] arrData = {lp.getCodigo(), lp.getNombre(), 
                formatea.format(lp.getPrecio()), formatea.format(lp.getInventario())};
            datos[i] = arrData;
            i++;
        }
        //DefaultTableCellRenderer nos ayuada a dar formatoa a los numeros a la izquierda
        //asi mismo el color
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        DefaultTableModel modelo = new DefaultTableModel(datos, nombrecolumnas);
        tcr.setHorizontalAlignment(SwingConstants.RIGHT);
        //tcr.setBackground(Color.GREEN);
        tcr.setForeground(Color.red);
        JTable tabla = this.formProductos.getTablaProductos();
        //pintamos los registros en el modelo
        tabla.setModel(modelo);
        //eliminar la columna codigo para no mostrarla en la tabla
        //se poce despues de crear el modelo        
        TableColumnModel tcm = tabla.getColumnModel();
        tabla.removeColumn(tcm.getColumn(0));

        //renderizamos DefaultTableCellRenderer tcr para que tome sus efectos
        tabla.getColumnModel().getColumn(1).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(2).setCellRenderer(tcr);
        
        return listaproductos;
    }
    //eliminar el registro e la base de datos ya cualizacion em la tabla
    public void borraProducto() {
        try {
            ImageIcon icono = new ImageIcon("src/main/resources/img/ok.png");
            JTable tabla = formProductos.getTablaProductos();
            long codigo = (long) tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);
            repositorioProducto.deleteById(codigo);
            JOptionPane.showMessageDialog(null, "El producto fue borrado con exito",
                    "Información", JOptionPane.INFORMATION_MESSAGE, icono);

        } catch (Exception e) {
            System.out.println(e.getMessage());
//             ImageIcon icono = new ImageIcon("src//main//resources//ok.png");
//            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla",
//                    "Información", JOptionPane.INFORMATION_MESSAGE,icono);
        }

    }
    //validacopn para Agrefar un registro y actualizacion de productos
    public boolean validarCamposVacios(String nombre, String precio, String inventario) {
        if (nombre.equals("") || precio.equals("") || inventario.equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligarorios",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
    //colocamos las cajas de texto en blanco
    public void limpiarCajas() {
        formProductos.getTextNombre().setText("");
        formProductos.getTextPrecio().setText("");
        formProductos.getTextInventario().setText("");
        formProductos.getTextNombre().requestFocus();
    }

    //crea un registro  teniendo encuenta ña valicadionCamposVacios
    public void AgregarProducto() {
        if (!validarCamposVacios(formProductos.getTextNombre().getText(),
                formProductos.getTextPrecio().getText(),
                formProductos.getTextInventario().getText())) {
            try {
                Producto producto = new Producto(formProductos.getTextNombre().getText(),
                        Double.parseDouble(formProductos.getTextPrecio().getText()),
                        Integer.parseInt(formProductos.getTextInventario().getText()));
                repositorioProducto.save(producto);
                limpiarCajas();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Error al guardar el registro",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    //actualizamos un registro con sus respectiva validacion de campos avacios
    public void actualizarProducto() {

        try {
            JTable tabla = formProductos.getTablaProductos();
            long codigo = (long) tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);
            DialogActualizar da = new DialogActualizar(this.formProductos, true);
            da.pack();
            da.setResizable(false);
            da.setTitle("Actualizar Productos");
            da.setSize(450, 300);
            da.setLocationRelativeTo(null);
            da.setVisible(true);

            //System.out.println(codigo);
            String[] param = da.regresarDatos();
            if (!validarCamposVacios(param[0], param[1], param[2])) {
                repositorioProducto.save(new Producto(codigo, param[0],
                        Double.parseDouble(param[1]), Integer.parseInt(param[2])));

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    //implementacion del actionlistener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == formProductos.getBotonAgregar()) {
            AgregarProducto();
            listar();
        }
        if (e.getSource() == formProductos.getBotonBorrar()) {
            this.borraProducto();
            listar();
        }
        if (e.getSource() == formProductos.getBotonActualizar()) {
            actualizarProducto();
            listar();
        }
        if (e.getSource() == formProductos.getBotonInforme()) {
            realizarInforne();
        }

    }
    
    //calculos del producto mayor del inventario
    public String mayorInvetario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        Optional<Producto> mayor = st.max((Producto x, Producto y) -> (int) (x.getPrecio() - y.getPrecio()));
        return mayor.get().getNombre();
    }
    //calculo del producto del menor del inventario
    public String menorInventario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        Optional<Producto> menor = st.max((Producto x, Producto y) -> (int) (y.getPrecio() - x.getPrecio()));
        return menor.get().getNombre();
    }
//calculos del producto del valor del inventario
    public double valorInventario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        DoubleStream totalInv = st.mapToDouble(p -> p.getPrecio() * p.getInventario());
        return totalInv.sum();
    }
    //calculo del promedio del inventario
    public double promedioInvetario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        int tam = listaproductos.size();
        double od = st.mapToDouble(p -> p.getPrecio())
                .reduce(0, (x, y) -> x + y) / tam;
        return od;
    }
    //presentacion del informe general
    private void realizarInforne() {
        //creamos una imagen para en informe final que nos diga que esta ok
        ImageIcon icono = new ImageIcon("src/main/resources/img/ok.png");
        DecimalFormat formatea = new DecimalFormat("#,###.0");
        String mayor = "El producto de precio mayor: " + mayorInvetario();
        String menor = "\nEl producto de precio menor: " + menorInventario();
        String promedio = "\nPromedio de precios: " + formatea.format(promedioInvetario());
        String inventario = "\nValor de Inventario: " + formatea.format(valorInventario());
        JOptionPane.showMessageDialog(null, mayor + menor + promedio + inventario,
                "Informacion", JOptionPane.INFORMATION_MESSAGE,icono);
    }

    //keyTyped-> con este evento perminimos a las cajas de texto para que no acepten letras
    // y asi ayudar a filtrar la informacion que se regustra o actualiza
    @Override
    public void keyTyped(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
