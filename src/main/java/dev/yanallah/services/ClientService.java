package dev.yanallah.services;

import dev.yanallah.MiniProject;
import dev.yanallah.database.Database;
import dev.yanallah.models.Client;

import java.util.List;

public class ClientService {
    private static ClientService instance;
    private final Database database;
    private final Observable<List<Client>> clientsObservable;

    private ClientService() {
        this.database = MiniProject.getInstance().getDatabase();
        this.clientsObservable = new Observable<>();
        loadClients();
    }

    public static ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public Observable<List<Client>> getClientsObservable() {
        return clientsObservable;
    }

    public void addClient(Client client) {
        database.addClient(client);
        loadClients(); // Recharger et notifier les observateurs
    }

    public void updateClient(Client client) {
        // TODO: Implémenter la mise à jour dans la base de données si nécessaire
        loadClients();
    }

    public void deleteClient(int clientId) {
        // TODO: Implémenter la suppression dans la base de données si nécessaire
        loadClients();
    }

    public Client getClientById(int id) {
        return database.getClientById(id);
    }

    public void refreshClients() {
        loadClients();
    }

    private void loadClients() {
        List<Client> clients = database.getAllClients();
        clientsObservable.setValue(clients);
    }
} 