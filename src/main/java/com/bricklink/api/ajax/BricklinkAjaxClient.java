package com.bricklink.api.ajax;

import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.ajax.support.SearchProductResult;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface BricklinkAjaxClient {
    @RequestLine("GET /ajax/clone/search/searchproduct.ajax?" +
            "q={q}&" +
            "st={st}&" +
            "cond={cond}&" +
            "brand={brand}&" +
            "type={type}&" +
            "cat={cat}&" +
            "yf={yf}&" +
            "yt={yt}&" +
            "loc={loc}&" +
            "reg={reg}&" +
            "ca={ca}&" +
            "ss={ss}&" +
            "pmt={pmt}&" +
            "nmp={nmp}&" +
            "color={color}&" +
            "min={min}&" +
            "max={max}&" +
            "minqty={minqty}&" +
            "nosuperlot={nosuperlot}&" +
            "incomplete={incomplete}&" +
            "showempty={showempty}&" +
            "rpp={rpp}&" +
            "pi={pi}&" +
            "ci={ci}")
    SearchProductResult searchProduct(@QueryMap Map<String, Object> params);

    @RequestLine("GET /ajax/clone/catalogifs.ajax?" +
            "itemid={itemid}&" +
            "cond={cond}&" +
            "rpp={rpp}")
    CatalogItemsForSaleResult catalogItemsForSale(@QueryMap Map<String, Object> params);
}
