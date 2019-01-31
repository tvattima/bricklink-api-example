package com.vattima.lego.data.ibatis.mapper;

import com.vattima.lego.data.dto.BricklinkItem;
import com.vattima.lego.data.dto.Item;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ItemMapper {
    @Select("SELECT item_id," +
            "       itemNumber," +
            "       item_name," +
            "       number_of_pieces," +
            "       issue_year," +
            "       issue_location," +
            "       theme_id," +
            "       item_type_code," +
            "       notes " +
            "FROM item")
    @ResultMap("itemResultMap")
    List<Item> getAll();

    @Select("SELECT * " +
            "FROM item i " +
            "WHERE NOT EXISTS (SELECT 1 FROM bricklink_item bi WHERE bi.item_id = i.item_id)")
    @ResultMap("itemResultMap")
    List<Item> getAllWithNoBricklinkItem();

    @Insert("INSERT INTO bricklink_item(bl_item_id, bl_item_number, item_id) " +
            "VALUES (#{bricklinkItemId}, #{bricklinkItemNo}, #{itemId}) " +
            "ON DUPLICATE KEY UPDATE " +
            "    bl_item_number = #{bricklinkItemNo}, " +
            "    item_id = #{itemId}")
    void insertBricklinkItem(BricklinkItem bricklinkItem);

    @Insert("INSERT INTO item (itemNumber, item_name, number_of_pieces, issue_year, issue_location, theme_id, item_type_code, notes) " +
            "VALUES (#{itemNumber}, #{itemName}, #{numberOfPieces}, #{issueYear}, #{issueLocation}, #{themeId}, #{itemTypeCode}, #{notes})")
    @Options(useGeneratedKeys=true, keyProperty="itemId")
    void insertItem(Item item);

    @Select("SELECT item_id," +
            "       itemNumber," +
            "       item_name," +
            "       number_of_pieces," +
            "       issue_year," +
            "       issue_location," +
            "       theme_id," +
            "       item_type_code," +
            "       notes " +
            "FROM item " +
            "WHERE item_id = #{itemId}")
    @ResultMap("itemResultMap")
    Item findItemById(int itemId);

    @Select("SELECT item_id," +
            "       itemNumber," +
            "       item_name," +
            "       number_of_pieces," +
            "       issue_year," +
            "       issue_location," +
            "       theme_id," +
            "       item_type_code," +
            "       notes " +
            "FROM item " +
            "WHERE itemNumber = #{itemNumber}")
    @ResultMap("itemResultMap")
    Item findItemByNumber(String itemNumber);
}
