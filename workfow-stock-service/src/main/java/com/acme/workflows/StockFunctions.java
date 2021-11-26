package com.acme.workflows;

import io.automatiko.engine.api.Functions;
import pl.piomin.samples.streams.stock.model.Order;

public class StockFunctions implements Functions {

    public static String getProductId(Order eventData) {

        return String.valueOf(eventData.getProductId());
    }
}
