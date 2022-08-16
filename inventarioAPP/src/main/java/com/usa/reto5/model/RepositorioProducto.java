
package com.usa.reto5.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface RepositorioProducto extends  CrudRepository<Producto, Long>{
    
}
