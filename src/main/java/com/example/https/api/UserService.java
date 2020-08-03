package com.example.https.api;

import com.example.https.dto.DataAndKeysDTO;
import com.example.https.dto.KeyDTO;

import java.security.PublicKey;


public interface UserService {
    DataAndKeysDTO add(DataAndKeysDTO dataAndKeysDTO);

    DataAndKeysDTO update(long userId, DataAndKeysDTO dataAndKeysDTO);

    void delete(long userId);

    DataAndKeysDTO get(long id, KeyDTO clientsKey);

    DataAndKeysDTO list(KeyDTO keyDTO);

    KeyDTO getPublicKey();
}
