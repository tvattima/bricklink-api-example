package com.vattima.lego.data.dao;

import com.vattima.lego.data.dto.BricklinkItem;
import com.vattima.lego.data.dto.Item;
import com.vattima.lego.data.ibatis.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemDao {
    private final ItemMapper itemMapper;

    public List<Item> getAll() {
        return itemMapper.getAll();
    }

    public List<Item> getAllWithNoBricklinkItem() {
        return itemMapper.getAllWithNoBricklinkItem();
    }

    public void insertItem(Item item) {
        itemMapper.insertItem(item);
    }

    public Item findItemById(int itemId) {
        return itemMapper.findItemById(itemId);
    }

    public Item findItemByNumber(String itemNumber) {
        return itemMapper.findItemByNumber(itemNumber);
    }

    public void insertBricklinkItem(BricklinkItem bricklinkItem) {
        itemMapper.insertBricklinkItem(bricklinkItem);
    }
}
