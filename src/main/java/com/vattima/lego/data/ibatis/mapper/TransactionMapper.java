package com.vattima.lego.data.ibatis.mapper;

import com.vattima.lego.data.dto.Transaction;
import com.vattima.lego.data.dto.TransactionItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TransactionMapper {

    @Insert("INSERT INTO transactions (transaction_date, notes, shipping_amount, from_party_id, to_party_id) " +
            "VALUES (#{transactionDate}, #{notes}, #{shippingAmount}, #{fromPartyId}, #{toPartyId})")
    @Options(useGeneratedKeys=true, keyProperty="transactionId")
    void insertTransaction(Transaction transaction);

    @Insert("INSERT INTO transaction_item (item_id, transaction_id, transaction_type_code, price, notes, box_condition_id, instructions_condition_id) " +
            "VALUES (#{itemId}, #{transactionId}, #{transactionTypeCode}, #{price}, #{notes}, #{boxConditionId}, #{instructionsConditionId})")
    @Options(useGeneratedKeys=true, keyProperty="itemTransactionId")
    void insertTransactionItem(TransactionItem transactionItem);

    @Select("SELECT transaction_id, transaction_date, notes, shipping_amount, from_party_id, to_party_id " +
            "FROM transactions " +
            "WHERE transaction_date = #{date,jdbcType=DATE}")
    @ResultMap("transactionResultMap")
    public List<Transaction> findByTransactionDate(LocalDate date);

    @Select("SELECT transaction_id, transaction_date, notes, shipping_amount, from_party_id, to_party_id " +
            "FROM transactions " +
            "WHERE transaction_date = #{date,jdbcType=DATE} " +
            "AND notes = 'Auto generated from 2018 inventory'")
    @ResultMap("transactionResultMap")
    public List<Transaction> findByAutoGeneratedAndTransactionDate(LocalDate date);
}