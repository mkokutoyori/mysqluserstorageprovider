package com.mandeng.dao;




import com.mandeng.models.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


public class UserRepositoryImpl {

    private EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<User> findAll() {
        return findAll(null, null);
    }

    public List<User> findAll(int start, int max) {
        return findAll((Integer)start, (Integer)max);
    }

    private List<User> findAll(Integer start, Integer max) {
        TypedQuery<User> query = entityManager.createNamedQuery("searchForUser", User.class);
        if(start != null) {
            query.setFirstResult(start);
        }
        if(max != null) {
            query.setMaxResults(max);
        }
        query.setParameter("search", "%");
        List<User> users =  query.getResultList();
        return users;
    }

    public Optional<User> getUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createNamedQuery("getUserByUsername", User.class);
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> getUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createNamedQuery("getUserByEmail", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    public List<User> searchForUserByUsernameOrEmail(String searchString) {
        return searchForUserByUsernameOrEmail(searchString, null, null);
    }

    public List<User> searchForUserByUsernameOrEmail(String searchString, int start, int max) {
        return searchForUserByUsernameOrEmail(searchString, (Integer)start, (Integer)max);
    }

    private List<User> searchForUserByUsernameOrEmail(String searchString, Integer start, Integer max) {
        TypedQuery<User> query = entityManager.createNamedQuery("searchForUser", User.class);
        query.setParameter("search", "%" + searchString + "%");
        if(start != null) {
            query.setFirstResult(start);
        }
        if(max != null) {
            query.setMaxResults(max);
        }
        return query.getResultList();
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }


    public User createUser(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        return user;
    }

    public void deleteUser(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.remove(user);
        transaction.commit();
    }

    public void close() {
        this.entityManager.close();
    }

    public User updateUser(User userEntity) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(userEntity);
        transaction.commit();
        return userEntity;
    }

    public int size() {
        return entityManager.createNamedQuery("getUserCount", Integer.class).getSingleResult();
    }

    public String getPassword(String search){
        TypedQuery<String> password = entityManager.createNamedQuery("getPassword", String.class);
        password.setParameter("search",search);
        return password.getSingleResult();
    }
}
