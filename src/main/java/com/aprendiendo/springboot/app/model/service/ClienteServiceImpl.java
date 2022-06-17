package com.aprendiendo.springboot.app.model.service;


import com.aprendiendo.springboot.app.model.dao.IClienteDAO;
import com.aprendiendo.springboot.app.model.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService{

    @Autowired
    private IClienteDAO clienteDAO;
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteDAO.findAll();
    }

    @Override
    @Transactional
    public void save(Cliente cliente) {
        clienteDAO.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findOne(Long id) {
        return clienteDAO.findOne(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        clienteDAO.delete(id);
    }
}
