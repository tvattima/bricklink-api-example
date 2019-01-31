package com.vattima.lego.data.dao;

import com.vattima.lego.data.dto.BricklinkInventoryWork;
import com.vattima.lego.data.ibatis.mapper.BricklinkInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BricklinkInventoryDao {
    private final BricklinkInventoryMapper bricklinkInventoryMapper;

    public List<BricklinkInventoryWork> getAllInventoryWork() {
        return bricklinkInventoryMapper.getAllInventoryWork();
    }

    public List<BricklinkInventoryWork> getInventoryWork() {
        return bricklinkInventoryMapper.getInventoryWork();
    }
}
