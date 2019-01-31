package com.bricklink.api.ajax.model.v1;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Type {
    private String type;
    private Integer count;
    private List<Item> items;
}
