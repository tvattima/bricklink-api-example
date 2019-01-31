package com.vattima.lego.data.dao;

import com.vattima.lego.data.dto.BricklinkSaleItem;
import com.vattima.lego.data.ibatis.mapper.BricklinkSaleItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BricklinkSaleItemDao {
    private final BricklinkSaleItemMapper bricklinkSaleItemMapper;

    public void insert(BricklinkSaleItem bricklinkSaleItem) {
        bricklinkSaleItemMapper.insert(bricklinkSaleItem);
    }
}
