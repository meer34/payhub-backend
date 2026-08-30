package com.mir.payhub.profile.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponse {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
