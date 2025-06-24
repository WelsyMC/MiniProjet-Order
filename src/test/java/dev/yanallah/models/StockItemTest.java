package dev.yanallah.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class StockItemTest {
    
    private StockItem stockItem;
    
    @BeforeEach
    public void setUp() {
        stockItem = new StockItem(1, "Ordinateur portable", 15, 899.99);
    }
    
    @Test
    public void testStockItemCreation() {
        assertNotNull(stockItem);
        assertEquals(1, stockItem.getId());
        assertEquals("Ordinateur portable", stockItem.getName());
        assertEquals(15, stockItem.getQuantityInStock());
        assertEquals(899.99, stockItem.getPrice(), 0.01);
    }
    
    @Test
    public void testStockItemToString() {
        String expected = "Ordinateur portable (" + stockItem.getQuantityInStock() + ")";
        assertEquals(expected, stockItem.toString());
    }
    
    @Test
    public void testStockItemSetters() {
        stockItem.setId(2);
        stockItem.setName("Souris optique");
        stockItem.setQuantityInStock(50);
        stockItem.setPrice(25.99);
        
        assertEquals(2, stockItem.getId());
        assertEquals("Souris optique", stockItem.getName());
        assertEquals(50, stockItem.getQuantityInStock());
        assertEquals(25.99, stockItem.getPrice(), 0.01);
    }
    
    @Test
    public void testZeroQuantity() {
        stockItem.setQuantityInStock(0);
        assertEquals(0, stockItem.getQuantityInStock());
    }
    
    @Test
    public void testNegativeQuantityShouldBeAllowed() {
        // Dans certains cas, on peut avoir des quantités négatives (commandes en attente)
        stockItem.setQuantityInStock(-5);
        assertEquals(-5, stockItem.getQuantityInStock());
    }
    
    @Test
    public void testZeroPrice() {
        stockItem.setPrice(0.0);
        assertEquals(0.0, stockItem.getPrice(), 0.01);
    }
    
    @Test
    public void testLargeQuantity() {
        stockItem.setQuantityInStock(1000000);
        assertEquals(1000000, stockItem.getQuantityInStock());
    }
    
    @Test
    public void testHighPrice() {
        stockItem.setPrice(99999.99);
        assertEquals(99999.99, stockItem.getPrice(), 0.01);
    }
    
    @Test
    public void testEmptyName() {
        stockItem.setName("");
        assertEquals("", stockItem.getName());
        assertEquals(" (15)", stockItem.toString());
    }
    
    @Test
    public void testNullName() {
        stockItem.setName(null);
        assertNull(stockItem.getName());
        assertEquals("null (" + stockItem.getQuantityInStock() + ")", stockItem.toString());
    }
} 