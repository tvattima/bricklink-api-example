package com.vattima.lego.data.ibatis.mapper;

import com.vattima.lego.data.dto.InventoryIndex;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryIndexMapper {
    @Select("SELECT boxIndex, box_number, itemNumber, sealed, quantity, description FROM inventory_index")
    @ResultMap("inventoryIndexResultMap")
    List<InventoryIndex> getAll();

    @Select("select boxIndex, box_number, itemNumber, sealed, quantity, description from inventory_index ii " +
            "where not exists (select 1 from item i where i.itemNumber = ii.itemNumber) " +
            "and ii.itemNumber not in ('no #','') " +
            "and ii.itemNumber is not null")
    @ResultMap("inventoryIndexResultMap")
    List<InventoryIndex> getAllWithNoItem();

    @Insert("INSERT INTO inventory_index(boxIndex, box_number, itemNumber, sealed, quantity, description) " +
            "VALUES (#{boxIndex}, #{boxNumber}, #{itemNumber}, #{sealed}, #{quantity}, #{description})")
    void insertInventoryIndex(InventoryIndex inventoryIndex);

    @Update("UPDATE inventory_index " +
            "SET    sealed = #{sealed}," +
            "       quantity = #{quantity}," +
            "       description = #{description} " +
            "WHERE  boxIndex = #{boxIndex} " +
            "AND    box_number = #{boxNumber} " +
            "AND    itemNumber = #{itemNumber} ")
    void udpateInventoryIndex(InventoryIndex inventoryIndex);
}







