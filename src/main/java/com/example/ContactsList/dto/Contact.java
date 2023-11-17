package com.example.ContactsList.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Contact {
    private String fullName;
    private String phoneNumber;
    private String email;

    @Override
    public String toString() {
        return fullName + " | " + phoneNumber + " | " + email;
    }
}
