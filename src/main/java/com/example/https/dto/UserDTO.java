package com.example.https.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private long id;
    @NonNull
    private String name;
    @NonNull
    private int age;
    @NonNull
    private String email;
    @NonNull
    private UUID uuid;

}
