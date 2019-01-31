package com.vattima.lego.data.dao;

import com.vattima.lego.data.dto.InventoryIndex;
import com.vattima.lego.data.ibatis.mapper.InventoryIndexMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryIndexDao {
    private final InventoryIndexMapper inventoryIndexMapper;

    public List<InventoryIndex> getAll() {
        return inventoryIndexMapper.getAll();
    }

    public List<InventoryIndex> getAllWithNoItem() {
        return inventoryIndexMapper.getAllWithNoItem();
    }

    public void insertInventoryIndex(InventoryIndex inventoryIndex) {
        inventoryIndexMapper.insertInventoryIndex(inventoryIndex);
    }

    public void udpateInventoryIndex(InventoryIndex inventoryIndex) {
        inventoryIndexMapper.udpateInventoryIndex(inventoryIndex);
    }
}
