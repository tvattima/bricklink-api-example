package com.vattima.lego.data.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Party {
    private int partyId;
    private String partyFirstName;
    private String partyMiddleInitial;
    private String partyLastName;
    private String partyAddress1;
    private String partyAddress2;
    private String partyCity;
    private String partyState;
    private String partyPostalCode;
    private String partyCountryCode;
    private String partyCountry;
    private String partyPhone;
    private String partyEmail;
    private String partyType;
    private String partyPassword;
    private LocalDate partyActivationDate;
    private String partyActiveIndicator;
}
