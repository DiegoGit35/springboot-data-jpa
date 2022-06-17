package com.aprendiendo.springboot.app.model.dao;

import com.aprendiendo.springboot.app.model.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IClienteDAO extends CrudRepository<Cliente, Long> {

}
