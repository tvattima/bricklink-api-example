package com.bricklink.api.ajax.model.v1;


import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AjaxResult {
    private Integer returnCode;
    private String returnMessage;
    private Integer errorTicket;
    private Integer procssingTime;
}
