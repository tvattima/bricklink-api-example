package com.vattima.lego.data.ibatis.mapper;

import com.vattima.lego.data.dto.BricklinkItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BrinklinkItemMapper {
    @Select("SELECT item_id, bricklink_item_id FROM bricklink_item WHERE item_id = #{itemId}")
    BricklinkItem getBricklinkItemForItemId(int itemId);
}
