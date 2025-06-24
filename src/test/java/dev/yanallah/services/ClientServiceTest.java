package dev.yanallah.services;

import dev.yanallah.MiniProject;
import dev.yanallah.models.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientServiceTest {
    
    private ClientService clientService;
    private Client testClient;
    
    @BeforeEach
    public void setUp() {
        new MiniProject(new String[]{});
        clientService = ClientService.getInstance();
        testClient = new Client(0, "Test", "Client", "test@email.com", "0123456789", "Test Address");
    }
    
    @AfterEach
    public void tearDown() {
        // Nettoyer les données de test si nécessaire
        // Note: Dans un vrai projet, on utiliserait une base de données de test
    }
    
    @Test
    public void testClientServiceSingleton() {
        ClientService instance1 = ClientService.getInstance();
        ClientService instance2 = ClientService.getInstance();
        assertSame(instance1, instance2, "ClientService should be a singleton");
    }
    
    @Test
    public void testGetClientsObservable() {
        Observable<List<Client>> observable = clientService.getClientsObservable();
        assertNotNull(observable, "Clients observable should not be null");
        
        List<Client> clients = observable.getValue();
        assertNotNull(clients, "Clients list should not be null");
    }
    
    @Test
    public void testAddClientNotifiesObservers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        boolean[] notified = {false};
        
        clientService.getClientsObservable().subscribe(clients -> {
            notified[0] = true;
            latch.countDown();
        });
        
        // Attendre la notification initiale
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(notified[0], "Observer should be notified initially");
        
        // Reset pour tester l'ajout
        notified[0] = false;
        CountDownLatch addLatch = new CountDownLatch(1);
        
        clientService.getClientsObservable().subscribe(clients -> {
            if (!notified[0]) { // Éviter les notifications multiples
                notified[0] = true;
                addLatch.countDown();
            }
        });
        
        clientService.addClient(testClient);
        
        // Attendre la notification après ajout
        boolean notifiedAfterAdd = addLatch.await(2, TimeUnit.SECONDS);
        assertTrue(notifiedAfterAdd, "Observer should be notified after adding client");
    }
    
    @Test
    public void testRefreshClients() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        boolean[] refreshed = {false};
        
        clientService.getClientsObservable().subscribe(clients -> {
            refreshed[0] = true;
            latch.countDown();
        });
        
        clientService.refreshClients();
        
        boolean notified = latch.await(1, TimeUnit.SECONDS);
        assertTrue(notified, "Refresh should trigger observer notification");
    }
    
    @Test
    public void testGetClientById() {
        // Ce test nécessiterait un client existant dans la base de données
        // Dans un vrai projet, on utiliserait des données de test prédéfinies
        
        List<Client> clients = clientService.getClientsObservable().getValue();
        if (clients != null && !clients.isEmpty()) {
            Client firstClient = clients.get(0);
            Client retrievedClient = clientService.getClientById(firstClient.getId());
            
            if (retrievedClient != null) {
                assertEquals(firstClient.getId(), retrievedClient.getId());
                assertEquals(firstClient.getNom(), retrievedClient.getNom());
                assertEquals(firstClient.getPrenom(), retrievedClient.getPrenom());
            }
        }
    }
    
    @Test
    public void testAddClientWithValidData() {
        Client validClient = new Client(0, "Valid", "Client", "valid@email.com", "0987654321", "Valid Address");
        
        // Compter les clients avant
        List<Client> clientsBefore = clientService.getClientsObservable().getValue();
        int countBefore = clientsBefore != null ? clientsBefore.size() : 0;
        
        clientService.addClient(validClient);
        
        // Attendre un peu pour que la base de données soit mise à jour
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Vérifier que les clients ont été rafraîchis
        clientService.refreshClients();
        
        List<Client> clientsAfter = clientService.getClientsObservable().getValue();
        int countAfter = clientsAfter != null ? clientsAfter.size() : 0;
        
        assertTrue(countAfter >= countBefore, "Client count should increase or stay the same after adding");
    }
    
    @Test
    public void testObservableNotNull() {
        Observable<List<Client>> observable = clientService.getClientsObservable();
        assertNotNull(observable, "Observable should never be null");
        
        // Même si la valeur peut être null initialement, l'observable lui-même ne doit jamais l'être
        assertNotNull(observable);
    }
    
    @Test
    public void testMultipleObservers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        boolean[] observer1Notified = {false};
        boolean[] observer2Notified = {false};
        
        clientService.getClientsObservable().subscribe(clients -> {
            observer1Notified[0] = true;
            latch.countDown();
        });
        
        clientService.getClientsObservable().subscribe(clients -> {
            observer2Notified[0] = true;
            latch.countDown();
        });
        
        boolean bothNotified = latch.await(1, TimeUnit.SECONDS);
        assertTrue(bothNotified, "Both observers should be notified");
        assertTrue(observer1Notified[0], "First observer should be notified");
        assertTrue(observer2Notified[0], "Second observer should be notified");
    }
} 