package com.mandeng;



import com.mandeng.dao.UserRepositoryImpl;
import com.mandeng.models.User;
import com.mandeng.representations.UserAdapter;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.*;
import java.util.stream.Collectors;

public class MySQLUserStorageProvider implements UserStorageProvider,
    UserLookupProvider,
    UserRegistrationProvider,
    UserQueryProvider,
    CredentialInputUpdater,
    CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepositoryImpl userRepositoryImpl;

    public MySQLUserStorageProvider(KeycloakSession session, ComponentModel model, UserRepositoryImpl userRepositoryImpl) {
        this.session = session;
        this.model = model;
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public void close() {
        userRepositoryImpl.close();
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType) && (userRepositoryImpl.getPassword(user.getUsername()) != null);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if (!(credentialInput instanceof UserCredentialModel) || (!supportsCredentialType(credentialInput.getType()))){
            return false;
        }
        UserCredentialModel userCredentialModel= (UserCredentialModel) credentialInput;
        return userRepositoryImpl.getPassword(user.getUsername()).equals(userCredentialModel.getChallengeResponse());

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel userModel, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        User user = userRepositoryImpl.getUserByUsername(userModel.getUsername()).get();
        user.setPassword(input.getChallengeResponse());
        userRepositoryImpl.updateUser(user);
        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) return;
        getUserRepresentation(user).setPassword(null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        if (getUserRepresentation(user).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(PasswordCredentialModel.TYPE);
            return set;
        } else {
            return Collections.emptySet();
        }
    }

    public UserAdapter getUserRepresentation(UserModel user) {
        UserAdapter userAdapter = null;
        if (user instanceof CachedUserModel) {
            userAdapter = (UserAdapter) ((CachedUserModel) user).getDelegateForUpdate();
        } else {
            userAdapter = (UserAdapter) user;
        }
        return userAdapter;
    }

    public UserAdapter getUserRepresentation(User user, RealmModel realm) {
        return new UserAdapter(session, realm, model, user, userRepositoryImpl);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return userRepositoryImpl.size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return userRepositoryImpl.findAll()
            .stream()
            .map(user -> new UserAdapter(session, realm, model, user, userRepositoryImpl))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return userRepositoryImpl.findAll(firstResult, maxResults)
            .stream()
            .map(user -> new UserAdapter(session, realm, model, user, userRepositoryImpl))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return userRepositoryImpl.searchForUserByUsernameOrEmail(search)
            .stream()
            .map(user -> new UserAdapter(session, realm, model, user, userRepositoryImpl))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return userRepositoryImpl.searchForUserByUsernameOrEmail(search, firstResult, maxResults)
            .stream()
            .map(user -> new UserAdapter(session, realm, model, user, userRepositoryImpl))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return new ArrayList<>();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult,
                                         int maxResults) {
        return userRepositoryImpl.findAll(firstResult, maxResults)
            .stream()
            .map(user -> new UserAdapter(session, realm, model, user, userRepositoryImpl))
            .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return new ArrayList<>();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return new ArrayList<>();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return new ArrayList<>();
    }

    @Override
    public UserModel getUserById(String keycloakId, RealmModel realm) {
        String id = StorageId.externalId(keycloakId);
        return new UserAdapter(session, realm, model, userRepositoryImpl.getUserById(Long.valueOf(id)), userRepositoryImpl);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        Optional<User> optionalUser = userRepositoryImpl.getUserByUsername(username);
        return optionalUser.map(user -> getUserRepresentation(user, realm)).orElse(null);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        Optional<User> optionalUser = userRepositoryImpl.getUserByEmail(email);
        return optionalUser.map(user -> getUserRepresentation(user, realm)).orElse(null);
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        User user = new User();
        user.setUsername(username);
        user = userRepositoryImpl.createUser(user);

        return new UserAdapter(session, realm, model, user, userRepositoryImpl);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        User userEntity = userRepositoryImpl.getUserById(Long.valueOf(StorageId.externalId(user.getId())));
        if (userEntity == null) {
            return false;
        }
        userRepositoryImpl.deleteUser(userEntity);
        return true;
    }

    /*public String getPassword(UserModel user) {
        String password = null;
        if (user instanceof UserAdapter) {
            password = ((UserAdapter) user).getPassword();
        }
        return password;
    }*/

}
