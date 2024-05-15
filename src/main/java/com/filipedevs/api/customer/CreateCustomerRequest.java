package com.filipedevs.api.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCustomerRequest {

    private String name;

    private String email;

    private String address;

}
