package com.example.https.service;

import com.example.https.api.UserService;
import com.example.https.dto.DataAndKeysDTO;
import com.example.https.dto.KeyDTO;
import com.example.https.dto.UserDTO;
import com.example.https.exception.ResourceNotFoundException;
import com.example.https.model.User;
import com.example.https.repository.UserRepository;
import com.example.https.transform.AES.AESEncryptingCodec;
import com.example.https.transform.AES.GenerateAESKey;
import com.example.https.transform.RSA.RSAEncryptingCodec;
import com.example.https.transform.RSA.RSAKeysHandler;
import com.example.https.transform.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with this id :: ";

    private final UserRepository userRepository;

    private final GenerateAESKey generateAESKey;
    private final RSAKeysHandler rsaKeysHandler;

    private final AESEncryptingCodec aesEncryptingCodec;
    private final RSAEncryptingCodec rsaEncryptingCodec;

    private final UserMapper userMapper;

    private final Gson gson;


    @Override
    public DataAndKeysDTO add(DataAndKeysDTO dataAndKeysDTO) {
        String data = decrypt(dataAndKeysDTO);

        UserDTO userDTO = gson.fromJson(data, UserDTO.class);

        User user = userRepository.save(userMapper.toModel(userDTO));

        UserDTO userToDTO = userMapper.toDTO(user);
        String userInJSON = gson.toJson(userToDTO);

        return encrypt(dataAndKeysDTO.getPublicKey(), userInJSON.getBytes());
    }

    @Override
    public DataAndKeysDTO update(long userId, DataAndKeysDTO dataAndKeysDTO) {
        String data = decrypt(dataAndKeysDTO);

        UserDTO userDTO = gson.fromJson(data, UserDTO.class);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        user.setAge(userDTO.getAge());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setUuid(userDTO.getUuid());

        User userAfterSave = userRepository.save(user);

        UserDTO returnUserDTO = userMapper.toDTO(userAfterSave);
        String userDTOInJSONString = gson.toJson(returnUserDTO);

        return encrypt(dataAndKeysDTO.getPublicKey(), userDTOInJSONString.getBytes());
    }

    @Override
    public void delete(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
        userRepository.delete(user);
    }

    @Override
    public DataAndKeysDTO get(long userId, KeyDTO keyDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        UserDTO userToDTO = userMapper.toDTO(user);
        String userInJSON = gson.toJson(userToDTO);
        byte[] userInBytes = userInJSON.getBytes();


        return encrypt(keyDTO, userInBytes);
    }

    @Override
    public DataAndKeysDTO list(KeyDTO keyDTO) {
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOS = users.stream().map(user -> userMapper.toDTO(user)).collect(Collectors.toList());
        Type type = new TypeToken<List<User>>() {
        }.getType();
        String json = gson.toJson(userDTOS, type);
        byte[] usersListInBytes = json.getBytes();

        DataAndKeysDTO dataAndKeysDTO = encrypt(keyDTO, usersListInBytes);

        return dataAndKeysDTO;
    }

    private DataAndKeysDTO encrypt(String keyInString, byte[] data) {
        SecretKey secretKey = generateAESKey.generateSecretKey();

        byte[] encryptedData = aesEncryptingCodec.encrypt(data, secretKey);

        byte[] publicKeyInBytes = Base64.decodeBase64(keyInString);

        PublicKey publicKey = rsaKeysHandler.createPublicKeyOutOfBytes(publicKeyInBytes);

        byte[] encryptedAESKey = rsaEncryptingCodec.encrypt(secretKey.getEncoded(), publicKey);

        return new DataAndKeysDTO(Base64.encodeBase64String(encryptedData), Base64.encodeBase64String(encryptedAESKey));
    }

    private DataAndKeysDTO encrypt(KeyDTO keyDTO, byte[] data) {
        return encrypt(keyDTO.getKey(), data);
    }

    private String decrypt(DataAndKeysDTO dataAndKeysDTO) {
        String data = dataAndKeysDTO.getData();
        String key = dataAndKeysDTO.getKeyUsedForEncrypting();

        byte[] dataBytes = Base64.decodeBase64(data);
        byte[] keyBytes = Base64.decodeBase64(key);

        byte[] decryptedKeyBytes = rsaEncryptingCodec.decrypt(keyBytes, rsaKeysHandler.getPrivateKey(/*"local/privateKey"*/));
        SecretKey secretKeyGotDecrypted = new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");

        byte[] decryptedData = aesEncryptingCodec.decrypt(dataBytes, secretKeyGotDecrypted);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    @Override
    public KeyDTO getPublicKey() {
        return new KeyDTO(Base64.encodeBase64String(rsaKeysHandler.getPublicKey(/*"local/publicKey"*/).getEncoded()));
    }

}