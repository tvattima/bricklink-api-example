package com.vattima.lego.data.ibatis.mapper;

import com.vattima.lego.data.dto.BricklinkInventoryWork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BricklinkInventoryMapper {
    String INVENTORY_WORK_COLUMNS =
            "bi.bl_inventory_id," +
            "bi.inventory_id," +
            "bi.order_id," +
            "bi.box_id," +
            "bi.box_index," +
            "bi.item_id," +
            "i.item_number," +
            "i.item_name," +
            "bli.bl_item_number," +
            "bli.bl_item_id," +
            "bi.item_type," +
            "bi.quantity," +
            "bi.new_or_used," +
            "bi.completeness," +
            "bi.unit_price," +
            "bi.description," +
            "bi.remarks," +
            "bi.bulk," +
            "bi.is_retain," +
            "bi.is_stock_room," +
            "bi.stock_room_id," +
            "bi.date_created," +
            "bi.my_cost," +
            "bi.my_weight," +
            "bi.sealed," +
            "bi.built_once," +
            "bi.box_condition_id," +
            "bi.instructions_condition_id," +
            "bi.internal_comments," +
            "bi.update_timestamp," +
            "bi.last_synchronized_timestamp," +
            "bi.internal_comments ";

    @Select("SELECT " + INVENTORY_WORK_COLUMNS + " " +
            "FROM   bricklink_inventory bi " +
            "JOIN item i ON bi.item_id = i.item_id " +
            "JOIN bricklink_item bli ON i.item_id = bli.item_id")
    @ResultMap("bricklinkInventoryWorkResultMap")
    public List<BricklinkInventoryWork> getAllInventoryWork();

    @Select("SELECT " + INVENTORY_WORK_COLUMNS + " " +
            "FROM bricklink_inventory bi " +
            "JOIN item i ON bi.item_id = i.item_id " +
            "JOIN bricklink_item bli ON i.item_id = bli.item_id " +
            "WHERE (bi.last_synchronized_timestamp < CURRENT_TIMESTAMP OR bi.last_synchronized_timestamp IS NULL) " +
            "AND bi.item_type = 'SET' " +
            "AND bi.order_id IS NULL")
    @ResultMap("bricklinkInventoryWorkResultMap")
    public List<BricklinkInventoryWork> getInventoryWork();
}