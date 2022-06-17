package com.aprendiendo.springboot.app.model.dao;

import com.aprendiendo.springboot.app.model.entity.Cliente;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository("clienteDaoImpl")
public class ClienteDAOImpl implements IClienteDAO{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Cliente> findAll() {
        return em.createQuery("from Cliente").getResultList();
    }


    @Override
    public Cliente findOne(Long id) {
        return em.find(Cliente.class, id);
    }

    @Override
    public void save(Cliente cliente) {
        if(cliente.getId() != null && cliente.getId() > 0){
            em.merge(cliente);
        } else {
            em.persist(cliente);
        }
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = findOne(id);
        em.remove(cliente);
    }
}
