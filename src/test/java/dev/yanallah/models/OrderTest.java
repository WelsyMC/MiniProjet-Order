package dev.yanallah.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class OrderTest {
    
    private Order order;
    private Client client;
    private StockItem stockItem;
    private OrderItem orderItem;
    private LocalDateTime testDate;
    
    @BeforeEach
    public void setUp() {
        testDate = LocalDateTime.of(2024, 1, 15, 10, 30);
        client = new Client(1, "Dupont", "Jean", "jean.dupont@email.com", "0123456789", "123 Rue de la Paix");
        stockItem = new StockItem(1, "Ordinateur portable", 15, 899.99);
        orderItem = new OrderItem(1, 1, 1, 2, 899.99);
        order = new Order(1, 1, testDate, OrderStatus.CREATED);
        order.setClient(client);
    }
    
    @Test
    public void testOrderCreation() {
        assertNotNull(order);
        assertEquals(1, order.getId());
        assertEquals(1, order.getClientId());
        assertEquals(testDate, order.getOrderDate());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }
    
    @Test
    public void testOrderSetters() {
        LocalDateTime newDate = LocalDateTime.of(2024, 2, 20, 14, 45);
        order.setId(2);
        order.setClientId(2);
        order.setStatus(OrderStatus.PREPARING);
        
        assertEquals(2, order.getId());
        assertEquals(2, order.getClientId());
        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }
    
    @Test
    public void testAddItem() {
        assertTrue(order.getItems().isEmpty());
        order.addItem(orderItem);
        assertEquals(1, order.getItems().size());
        assertTrue(order.getItems().contains(orderItem));
    }
    
    @Test
    public void testAddMultipleItems() {
        OrderItem item2 = new OrderItem(2, 1, 2, 1, 25.99);
        order.addItem(orderItem);
        order.addItem(item2);
        assertEquals(2, order.getItems().size());
    }
    
    @Test
    public void testTotalAmountEmpty() {
        assertEquals(0.0, order.getTotalAmount(), 0.01);
    }
    
    @Test
    public void testTotalAmountWithOneItem() {
        order.addItem(orderItem);
        double expectedTotal = 2 * 899.99; // quantité * prix
        assertEquals(expectedTotal, order.getTotalAmount(), 0.01);
    }
    
    @Test
    public void testTotalAmountWithMultipleItems() {
        OrderItem item2 = new OrderItem(2, 1, 2, 3, 25.99);
        order.addItem(orderItem);
        order.addItem(item2);
        
        double expectedTotal = (2 * 899.99) + (3 * 25.99);
        assertEquals(expectedTotal, order.getTotalAmount(), 0.01);
    }
    
    @Test
    public void testClientAssociation() {
        assertEquals(client, order.getClient());
        assertEquals("Dupont Jean", order.getClient().toString());
    }
    
    @Test
    public void testOrderStatuses() {
        order.setStatus(OrderStatus.CREATED);
        assertEquals(OrderStatus.CREATED, order.getStatus());
        
        order.setStatus(OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        
        order.setStatus(OrderStatus.SENT);
        assertEquals(OrderStatus.SENT, order.getStatus());
        
        order.setStatus(OrderStatus.RECEIVED);
        assertEquals(OrderStatus.RECEIVED, order.getStatus());
        
        order.setStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
    
    @Test
    public void testRemoveItem() {
        order.addItem(orderItem);
        assertEquals(1, order.getItems().size());
        
        order.getItems().remove(orderItem);
        assertEquals(0, order.getItems().size());
    }
    
    @Test
    public void testClearItems() {
        order.addItem(orderItem);
        OrderItem item2 = new OrderItem(2, 1, 2, 1, 25.99);
        order.addItem(item2);
        assertEquals(2, order.getItems().size());
        
        order.getItems().clear();
        assertEquals(0, order.getItems().size());
        assertEquals(0.0, order.getTotalAmount(), 0.01);
    }
} 