package com.mandeng.representations;


import com.mandeng.dao.UserRepositoryImpl;
import com.mandeng.models.User;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.Map;


public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private User userEntity;
    private UserRepositoryImpl userRepositoryImpl;

    public static final String PHONE="PHONE";
    public static final String AVATAR_URL="AVATAR_URL";
    public static final String JOB_TITLE="JOB_TITLE";

    public UserAdapter(KeycloakSession session,
                       RealmModel realm,
                       ComponentModel storageProviderModel,
                       User userEntity,
                       UserRepositoryImpl userRepositoryImpl) {

        super(session, realm, storageProviderModel);
        this.userEntity = userEntity;
        if ((super.getFirstAttribute(FIRST_NAME) == null) && !(getFirstName()==null)) {
            super.setSingleAttribute(FIRST_NAME,getFirstName());
        }
        if ((super.getFirstAttribute(LAST_NAME) == null) && !(getFirstName()==null)) {
            super.setSingleAttribute(LAST_NAME,getLastName());
        }
        if ((super.getFirstAttribute(EMAIL) == null) && !(getFirstName()==null)) {
            super.setSingleAttribute(EMAIL,getEmail());
        }
        this.userRepositoryImpl = userRepositoryImpl;
    }


    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        userEntity.setUsername(username);
        userEntity = userRepositoryImpl.updateUser(userEntity);
    }


    @Override
    public String getId() {
        if (storageId == null) {
            storageId = new StorageId(storageProviderModel.getId(), userEntity.getId().toString());
        }
        return storageId.getId();
    }

    public String getPassword() {
        return userEntity.getPassword();
    }

    public void setPassword(String password) {
        userEntity.setPassword(password);
        userEntity = userRepositoryImpl.updateUser(userEntity);
    }
    @Override
    public String getFirstAttribute(String name) {
        switch (name){
            case EMAIL:
                return userEntity.getEmail();
            case FIRST_NAME:
                return userEntity.getFirstName();
            case LAST_NAME:
                return userEntity.getLastName();
            default:
                return super.getFirstAttribute(name);
        }

    }

    @Override
    public void setSingleAttribute(String name, String value) {
        switch (name){
            case EMAIL:
                userEntity.setEmail(value);
                break;
            case FIRST_NAME:
                userEntity.setFirstName(value);
                break;
            case LAST_NAME:
                userEntity.setLastName(value);
                break;
            case USERNAME:
                userEntity.setUsername(value);
                break;
        }
        userRepositoryImpl.updateUser(userEntity);
        super.setSingleAttribute(name, value);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attributes = super.getAttributes();
        MultivaluedHashMap<String,String> attrs=new MultivaluedHashMap<>();

        attributes.remove(PHONE);
        attributes.remove(JOB_TITLE);
        attributes.remove(AVATAR_URL);

        attrs.putAll(attributes);
        attrs.add(PHONE,userEntity.getPhone());
        attrs.add(JOB_TITLE,userEntity.getJobTitle());
        attrs.add(AVATAR_URL,userEntity.getAvatarUrl());
        return attrs;
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        switch (name){
            case JOB_TITLE:
                userEntity.setJobTitle(values.get(0));
                break;
            case PHONE:
                userEntity.setPhone(values.get(0));
                break;
            case AVATAR_URL:
                userEntity.setAvatarUrl(values.get(0));
                break;
            default:
                super.setAttribute(name, values);
        }
        userRepositoryImpl.updateUser(userEntity);
    }
}
