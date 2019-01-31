package com.bricklink.api.ajax.model.v1;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Result {
    private Integer nCustomItemCnt;
    private List<Type> typeList;
}
