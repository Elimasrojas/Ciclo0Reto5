package com.usa.reto5.controlador;

import com.usa.reto5.model.Producto;
import com.usa.reto5.model.RepositorioProducto;
import com.usa.reto5.vista.DialogActualizar;
import com.usa.reto5.vista.FormProductos;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ControladorProducto implements ActionListener {

    RepositorioProducto repositorioProducto;
    FormProductos formProductos;

    public ControladorProducto(RepositorioProducto repositorio, FormProductos formProductos) {
        this.repositorioProducto = repositorio;
        this.formProductos = formProductos;
        agregarEventos();
        listar();
    }

    private void agregarEventos() {
        formProductos.getBotonAgregar().addActionListener(this);
        formProductos.getBotonActualizar().addActionListener(this);
        formProductos.getBotonBorrar().addActionListener(this);
        formProductos.getBotonInforme().addActionListener(this);
    }

    private List<Producto> listar() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        String[] nombrecolumnas = {"Codigo", "Nombre", "Precio", "Inventario"};

        Object[][] datos = new Object[listaproductos.size()][4];
        int i = 0;
        DecimalFormat formatea = new DecimalFormat("###,###.##");
        for (Producto lp : listaproductos) {
            Object[] arrData = {lp.getCodigo(), lp.getNombre(), formatea.format(lp.getPrecio()), formatea.format(lp.getInventario())};
            datos[i] = arrData;
            i++;
        }

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        DefaultTableModel modelo = new DefaultTableModel(datos, nombrecolumnas);
        tcr.setHorizontalAlignment(SwingConstants.RIGHT);
        //tcr.setBackground(Color.GREEN);
        tcr.setForeground(Color.red);
        JTable tabla = this.formProductos.getTablaProductos();
        tabla.setModel(modelo);
        //eliminar la columna codigo para no mostrarla en la tabla
        //se poce despues de crear el modelo
        TableColumnModel tcm = tabla.getColumnModel();
        tabla.removeColumn(tcm.getColumn(0));

        //renderizamos DefaultTableCellRenderer tcr para que tome sus efectos
        tabla.getColumnModel().getColumn(1).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(2).setCellRenderer(tcr);
        /*
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        JTable tabla =formProductos.getTablaProductos();
        
        int row=0;
        for (Producto lp : listaproductos) {
            tabla.setValueAt(lp.getCodigo(), row, 0);
            tabla.setValueAt(lp.getNombre(), row, 1);
            tabla.setValueAt(lp.getPrecio(), row, 2);
            tabla.setValueAt(lp.getInventario(), row, 3);
            
        }
        for (int i = row; i < tabla.getRowCount(); i++) {
            tabla.setValueAt("", row, 0);
            tabla.setValueAt("", row, 1);
            tabla.setValueAt("", row, 2);
            tabla.setValueAt("", row, 3);
        }
         */
        return listaproductos;
    }

    public void borraProducto() {
        try {
            JTable tabla = formProductos.getTablaProductos();
            long codigo = (long) tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);
            repositorioProducto.deleteById(codigo);
            JOptionPane.showMessageDialog(null, "El producto fue borrado con exito",
                    "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public boolean validarCamposVacios(String nombre,String precio,String inventario) {
        if (nombre.equals("") || precio.equals("")|| inventario.equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligarorios",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    public void limpiarCajas() {
        formProductos.getTextNombre().setText("");
        formProductos.getTextPrecio().setText("");
        formProductos.getTextInventario().setText("");
        formProductos.getTextNombre().requestFocus();
    }

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
                JOptionPane.showMessageDialog(null, "Error al guardar el registro",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    public void actualizarProducto() {

        try {
            DialogActualizar da = new DialogActualizar(this.formProductos, true);
            da.pack();
            da.setResizable(false);
            da.setTitle("Actualizar Productos");
            da.setSize(450, 300);
            da.setLocationRelativeTo(null);
            da.setVisible(true);
            JTable tabla = formProductos.getTablaProductos();
            long codigo = (long) tabla.getModel().getValueAt(tabla.getSelectedRow(), 0);
            String[] param =da.regresarDatos();
             if (!validarCamposVacios(param[0],param[1],param[2])){
                 repositorioProducto.save(new Producto( codigo, param[0],
                         Double.parseDouble(param[1]), Integer.parseInt(param[2])));
//                 JOptionPane.showMessageDialog(null, "El producto se actualizo con exito",
//                    "Información", JOptionPane.INFORMATION_MESSAGE);
             }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla",
                    "Error", JOptionPane.ERROR);
        }

    }

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
    public String mayorInvetario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        Optional<Producto> mayor = st.max((Producto x,Producto y)->(int)(x.getPrecio()-y.getPrecio()));       
        return mayor.get().getNombre();
    }

    public String menorInventario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        Optional<Producto> menor = st.max((Producto x,Producto y)->(int)(y.getPrecio()-x.getPrecio()));       
        return menor.get().getNombre();
    }

    public String valorInventario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        DoubleStream totalInv = st.mapToDouble(p -> p.getPrecio() * p.getInventario());
        return "" + totalInv.sum();
    }

    public String promedioInvetario() {
        List<Producto> listaproductos = (List<Producto>) repositorioProducto.findAll();
        Stream<Producto> st = listaproductos.stream();
        int tam=listaproductos.size();
        double od= st.mapToDouble(p->p.getPrecio())
                .reduce(0, (x, y) -> x + y)/tam;        
        return "" +od;
    }

    private void realizarInforne() {
        String   mayor="El producto de precio mayor: "+mayorInvetario();
        String menor="\nEl producto de precio menor: "+menorInventario();
        String promedio="\nPromedio de precios: "+String.format("%.1f",Double.parseDouble(promedioInvetario()));
        String inventario="\nValor de Inventario: "+String.format("%.1f",Double.parseDouble(valorInventario()));
       JOptionPane.showMessageDialog(null, mayor+menor+promedio+inventario,
                        "Advertencia", JOptionPane.OK_OPTION);
    }
}
