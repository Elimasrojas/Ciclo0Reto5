package com.example.demo;

import com.example.demo.controlador.ControladorProducto;
import com.example.demo.modelo.RepositorioProducto;
import com.example.demo.vista.FormProductos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventarioAppApplication {

    @Autowired
    RepositorioProducto repositorio;

    public static void main(String[] args) {
        //SpringApplication.run(InventarioAPP.class, args);       
        SpringApplicationBuilder builder = new SpringApplicationBuilder(InventarioAppApplication.class);
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
