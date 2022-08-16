package com.usa.reto5;

import com.usa.reto5.controlador.ControladorProducto;
import com.usa.reto5.model.RepositorioProducto;
import com.usa.reto5.vista.FormProductos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventarioAPP {

    @Autowired
    RepositorioProducto repositorio;

    public static void main(String[] args) {
        //SpringApplication.run(InventarioAPP.class, args);       
        SpringApplicationBuilder builder = new SpringApplicationBuilder(InventarioAPP.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);
    }

    @Bean
    ApplicationRunner ar() {
        return args -> {
            FormProductos formProductos = new FormProductos();
            ControladorProducto controladorProducto = new ControladorProducto(repositorio, formProductos);
        };

    }

}
