package com.example.https.controller;

import com.example.https.api.UserService;
import com.example.https.dto.DataAndKeysDTO;
import com.example.https.dto.KeyDTO;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Api(value = "Handles operations related to Users")
@RequiredArgsConstructor
public class Controller {

    private static final String GET_API_OPERATION_VALUE = "Retrieves a user";
    private static final String LIST_API_OPERATION_VALUE = "Retrieves the list of existing users";
    private static final String CODE_404_USER_DOES_NOT_EXISTS = "User does not exists";
    private static final String ID_OF_USER = "Id of user";
    private static final String UPDATES_THE_USER = "Updates the user";
    private static final String DELETES_THE_USER = "Deletes the user";
    private static final String ADDS_A_NEW_USER = "Adds a new user";
    private static final String DATA_AND_KEYS_API_PARAM = "Contains the encrypted data, the encrypted symmetric key and the client's public key";
    private static final String KEY_DTO_DESCRIPTION = "Client's public key";
    private static final String GET_PUBLIC_KEY_OPERATION_VALUE = "Returns with the server's public key";

    private final UserService userService;


    @PostMapping("/list")
    @ApiOperation(value = LIST_API_OPERATION_VALUE, response = DataAndKeysDTO.class)
    public ResponseEntity<DataAndKeysDTO> list(@ApiParam(value = KEY_DTO_DESCRIPTION, required = true) @RequestBody KeyDTO clientsKey) {
        return ResponseEntity.ok(userService.list(clientsKey));
    }

    @PostMapping("/{id}")
    @ApiOperation(value = GET_API_OPERATION_VALUE, response = DataAndKeysDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public ResponseEntity<DataAndKeysDTO> get(@ApiParam(value = ID_OF_USER, required = true) @PathVariable(value = "id") long id, @ApiParam(value = KEY_DTO_DESCRIPTION, required = true) @RequestBody KeyDTO clientsKey) {

        return ResponseEntity.ok().body(userService.get(id, clientsKey));
    }

    @PostMapping("")
    @ApiOperation(value = ADDS_A_NEW_USER, response = DataAndKeysDTO.class)
    public DataAndKeysDTO add(@ApiParam(value = DATA_AND_KEYS_API_PARAM, required = true) @RequestBody DataAndKeysDTO dataAndKeysDTO) {

        return userService.add(dataAndKeysDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = DELETES_THE_USER)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public void delete(@ApiParam(value = ID_OF_USER, required = true) @PathVariable(value = "id") long userId) {
        userService.delete(userId);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = UPDATES_THE_USER, response = DataAndKeysDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public ResponseEntity<DataAndKeysDTO> update(
            @ApiParam(value = ID_OF_USER, required = true)
            @PathVariable(value = "id") long userId,
            @ApiParam(value = DATA_AND_KEYS_API_PARAM, required = true)
            @RequestBody DataAndKeysDTO dataAndKeysDTO) {

        return ResponseEntity.ok(userService.update(userId, dataAndKeysDTO));
    }

    @GetMapping("/get_public_key")
    @ApiOperation(value = GET_PUBLIC_KEY_OPERATION_VALUE, response = KeyDTO.class)
    public ResponseEntity<KeyDTO> getPublicKey() {
        return ResponseEntity.ok(userService.getPublicKey());
    }
}
