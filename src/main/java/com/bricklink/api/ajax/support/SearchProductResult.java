package com.bricklink.api.ajax.support;

import com.bricklink.api.ajax.model.v1.AjaxResult;
import com.bricklink.api.ajax.model.v1.Result;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class SearchProductResult extends AjaxResult {
    private Result result;
}
