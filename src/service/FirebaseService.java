// service/FirebaseService.java
package service;

import interface_.DatabaseConnectable;
import exception.DatabaseException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseService implements DatabaseConnectable {
    private static FirebaseService instance;
    private Firestore firestore;

    private FirebaseService() {
        // Private constructor for Singleton pattern
    }

    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    @Override
    public void connect() throws Exception {
        try {
            // Initialize Firebase only if it's not already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream("./Codevia Firebase Admin SDK.json");
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://codevia-873bc-default-rtdb.firebaseio.com")
                    .build();
                
                FirebaseApp.initializeApp(options);
                
                System.out.println("Firebase Admin SDK initialized successfully!");
            }

            // Get Firestore instance
            this.firestore = FirestoreClient.getFirestore();
            System.out.println("Firestore initialized successfully");

        } catch (IOException e) {
            throw new DatabaseException("Failed to read Firebase service account file: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to connect to Firebase: " + e.getMessage());
        }
    }

    @Override
    public void save(String collection, String documentId, Map<String, Object> data) throws Exception {
        try {
            if (firestore == null) {
                throw new DatabaseException("Firestore not initialized. Call connect() first.");
            }
            
            DocumentReference docRef = firestore.collection(collection).document(documentId);
            docRef.set(data).get();
            
            System.out.println("Successfully saved to " + collection + "/" + documentId);

        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseException("Failed to save data: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> get(String collection, String documentId) throws Exception {
        try {
            if (firestore == null) {
                throw new DatabaseException("Firestore not initialized. Call connect() first.");
            }
            
            DocumentReference docRef = firestore.collection(collection).document(documentId);
            DocumentSnapshot document = docRef.get().get();
            
            if (document.exists()) {
                System.out.println("Successfully retrieved from " + collection + "/" + documentId);
                return document.getData();
            } else {
                System.out.println("Document not found: " + collection + "/" + documentId);
                return null;
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseException("Failed to get data: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAll(String collection) throws Exception {
        try {
            if (firestore == null) {
                throw new DatabaseException("Firestore not initialized. Call connect() first.");
            }
            
            QuerySnapshot querySnapshot = firestore.collection(collection).get().get();
            List<Map<String, Object>> results = new ArrayList<>();
            
            querySnapshot.getDocuments().forEach(doc -> {
                Map<String, Object> data = doc.getData();
                if (data != null) {
                    data.put("id", doc.getId()); // Add document ID to the data
                    results.add(data);
                }
            });
            
            System.out.println("Successfully retrieved " + results.size() + " documents from " + collection);
            return results;

        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseException("Failed to get all data: " + e.getMessage());
        }
    }

    @Override
    public void update(String collection, String documentId, Map<String, Object> data) throws Exception {
        try {
            if (firestore == null) {
                throw new DatabaseException("Firestore not initialized. Call connect() first.");
            }
            
            DocumentReference docRef = firestore.collection(collection).document(documentId);
            docRef.update(data).get();
            
            System.out.println("Successfully updated " + collection + "/" + documentId);

        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseException("Failed to update data: " + e.getMessage());
        }
    }

    @Override
    public void delete(String collection, String documentId) throws Exception {
        try {
            if (firestore == null) {
                throw new DatabaseException("Firestore not initialized. Call connect() first.");
            }
            
            DocumentReference docRef = firestore.collection(collection).document(documentId);
            docRef.delete().get();
            
            System.out.println("Successfully deleted " + collection + "/" + documentId);

        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseException("Failed to delete data: " + e.getMessage());
        }
    }
}