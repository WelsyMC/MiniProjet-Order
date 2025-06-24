package dev.yanallah.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    
    private Client client;
    
    @BeforeEach
    public void setUp() {
        client = new Client(1, "Dupont", "Jean", "jean.dupont@email.com", "0123456789", "123 Rue de la Paix");
    }
    
    @Test
    public void testClientCreation() {
        assertNotNull(client);
        assertEquals(1, client.getId());
        assertEquals("Dupont", client.getNom());
        assertEquals("Jean", client.getPrenom());
        assertEquals("jean.dupont@email.com", client.getEmail());
        assertEquals("0123456789", client.getTelephone());
        assertEquals("123 Rue de la Paix", client.getAdresse());
    }
    
    @Test
    public void testClientToString() {
        String expected = "Dupont Jean";
        assertEquals(expected, client.toString());
    }
    
    @Test
    public void testClientSetters() {
        client.setId(2);
        client.setNom("Martin");
        client.setPrenom("Marie");
        client.setEmail("marie.martin@email.com");
        client.setTelephone("0987654321");
        client.setAdresse("456 Avenue des Champs");
        
        assertEquals(2, client.getId());
        assertEquals("Martin", client.getNom());
        assertEquals("Marie", client.getPrenom());
        assertEquals("marie.martin@email.com", client.getEmail());
        assertEquals("0987654321", client.getTelephone());
        assertEquals("456 Avenue des Champs", client.getAdresse());
    }
    
    @Test
    public void testClientWithEmptyValues() {
        Client emptyClient = new Client(0, "", "", "", "", "");
        assertNotNull(emptyClient);
        assertEquals(0, emptyClient.getId());
        assertEquals("", emptyClient.getNom());
        assertEquals("", emptyClient.getPrenom());
        assertEquals(" ", emptyClient.toString()); // Espace entre nom et prénom vides
    }
    
    @Test
    public void testClientWithNullValues() {
        Client nullClient = new Client(0, null, null, null, null, null);
        assertNotNull(nullClient);
        assertNull(nullClient.getNom());
        assertNull(nullClient.getPrenom());
        assertEquals("null null", nullClient.toString());
    }
} 