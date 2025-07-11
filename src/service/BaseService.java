package service;

import exception.DatabaseException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseService {
    protected FirebaseService firebaseService;
    private boolean isConnected = false;

    // Constructor
    public BaseService() {
        try {
            this.firebaseService = FirebaseService.getInstance();
            initializeFirebaseAsync();
        } catch (Exception e) {
            System.out.println("Firebase not available, running in local mode");
            this.firebaseService = null;
            this.isConnected = false;
        }
    }
    
    private void initializeFirebaseAsync() {
        if (firebaseService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    this.firebaseService.connect();
                    isConnected = true;
                } catch (Exception e) {
                    System.err.println("Failed to connect to database: " + e.getMessage());
                    isConnected = false;
                }
            });
        }
    }
    
    protected boolean isFirebaseConnected() {
        return isConnected;
    }

    // Abstract methods - must be implemented by subclasses
    public abstract void displayInfo();

    // Template method pattern
    public final void performDatabaseOperation(String operation, String collection,
                                               String documentId, Map<String, Object> data) {
        try {
            validateOperation(operation);
            executeOperation(operation, collection, documentId, data);
            logOperation(operation, collection, documentId);
        } catch (Exception e) {
            handleError(e);
        }
    }

    protected void validateOperation(String operation) throws Exception {
        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation cannot be null or empty");
        }
    }

    protected void executeOperation(String operation, String collection,
                                    String documentId, Map<String, Object> data) throws Exception {
        if (firebaseService == null) {
            System.out.println("Firebase not available, skipping operation: " + operation);
            return;
        }
        
        switch (operation.toLowerCase()) {
            case "save":
                firebaseService.save(collection, documentId, data);
                break;
            case "get":
                firebaseService.get(collection, documentId);
                break;
            case "update":
                firebaseService.update(collection, documentId, data);
                break;
            case "delete":
                firebaseService.delete(collection, documentId);
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }

    protected void logOperation(String operation, String collection, String documentId) {
        System.out.println("Operation completed: " + operation + " on " + collection + "/" + documentId);
    }

    protected void handleError(Exception e) {
        System.err.println("Error occurred: " + e.getMessage());
        e.printStackTrace();
    }
}