package com.acme.workflows;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.piomin.samples.streams.stock.model.Order;
import pl.piomin.samples.streams.stock.model.Transaction;
import pl.piomin.samples.streams.stock.model.TransactionTotal;

@ApplicationScoped
public class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    private static long transactionId = 0;

    public Transaction process(List<Order> buyOrders, List<Order> sellOrders) {

        for (Order buy : buyOrders) {

            for (Order sell : sellOrders) {

                Transaction transaction = execute(buy, sell);

                if (transaction != null) {
                    return transaction;
                }
            }
        }

        return null;
    }

    public TransactionTotal calculate(List<Transaction> transactions, Transaction transaction) {
        transactions.add(transaction);

        int count = transactions.size();
        int amount = 0;
        int productCount = 0;

        for (Transaction tran : transactions) {
            amount = amount + tran.getAmount();
            productCount = productCount + tran.getAmount() * tran.getPrice();
        }
        return new TransactionTotal(count, amount, productCount);

    }

    public void removeOutdated(List<Order> buyOrders, List<Order> sellOrders) {
        int removedBuy = 0;
        int removedSell = 0;
        Iterator<Order> buyIt = buyOrders.iterator();

        while (buyIt.hasNext()) {
            Order order = (Order) buyIt.next();
            if (order.getCreationDate().isBefore(LocalDateTime.now().minusSeconds(10))) {
                buyIt.remove();
                removedBuy++;
            }
        }

        Iterator<Order> sellIt = sellOrders.iterator();

        while (sellIt.hasNext()) {
            Order order = (Order) sellIt.next();
            if (order.getCreationDate().isBefore(LocalDateTime.now().minusSeconds(10))) {
                sellIt.remove();
                removedSell++;
            }
        }

        LOGGER.info("Removing outdated orders (sell {}, buy {})", removedSell, removedBuy);
    }

    private Transaction execute(Order orderBuy, Order orderSell) {
        if (orderBuy.getAmount() >= orderSell.getAmount()) {
            int count = Math.min(orderBuy.getProductCount(), orderSell.getProductCount());
            //            log.info("Executed: orderBuy={}, orderSell={}", orderBuy.getId(), orderSell.getId());
            boolean allowed = matchOrders(orderBuy, orderSell, count);
            if (!allowed)
                return null;
            else
                return new Transaction(
                        ++transactionId,
                        orderBuy.getId(),
                        orderSell.getId(),
                        count,
                        (orderBuy.getAmount() + orderSell.getAmount()) / 2,
                        LocalDateTime.now(),
                        "NEW");
        } else {
            return null;
        }
    }

    private boolean matchOrders(Order buyOrder, Order sellOrder, int amount) {
        if (buyOrder == null || sellOrder == null)
            return false;
        int buyAvailableCount = buyOrder.getProductCount() - buyOrder.getRealizedCount();
        int sellAvailableCount = sellOrder.getProductCount() - sellOrder.getRealizedCount();
        if (buyAvailableCount >= amount && sellAvailableCount >= amount) {
            buyOrder.setRealizedCount(buyOrder.getRealizedCount() + amount);
            sellOrder.setRealizedCount(sellOrder.getRealizedCount() + amount);
            return true;
        } else {
            return false;
        }
    }
}
