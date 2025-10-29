package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JSONDatabase {
    private final File database;
    private final Map<String, JsonElement> storage;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();


    public JSONDatabase() {
        Map<String, JsonElement> storageTemp;
        database = new File(System.getProperty("user.dir") + "/src/server/data/db.json");
        if (database.exists()) {
            try (Reader reader = new FileReader(database)) {
                Type type = new TypeToken<Map<String, JsonElement>>() {}.getType();
                storageTemp = new Gson().fromJson(reader, type);
            } catch (IOException e) {
                System.out.println("Error al leer el archivo, iniciando base vacía.");
                storageTemp = new HashMap<>();
            }
        } else {
            storageTemp = new HashMap<>();
        }
        storage = storageTemp;
    }

    public Response get(List<String> keyPath) {
        readLock.lock();
        try {
            if (keyPath.isEmpty()) {
                return Response.error("Empty key path");
            }

            JsonElement current = storage.get(keyPath.getFirst());
            if (current == null) {
                return Response.error("No such key");
            }

            for (int i = 1; i < keyPath.size(); i++) {
                if (!current.isJsonObject()) {
                    return Response.error("No such key");
                }
                JsonObject obj = current.getAsJsonObject();
                current = obj.get(keyPath.get(i));
                if (current == null) {
                    return Response.error("No such key");
                }
            }
            return Response.okWithValue(current);
        } finally {
            readLock.unlock();
        }
    }

    public Response set(List<String> keyPath, JsonElement value) {
        writeLock.lock();
        try {
            if (keyPath.isEmpty()) {
                return Response.error("Empty key path");
            }

            String rootKey = keyPath.getFirst();
            JsonElement current = storage.get(rootKey);

            if (keyPath.size() == 1) {
                storage.put(rootKey, value);
            } else {
                JsonObject rootObj;
                if (current == null || !current.isJsonObject()) {
                    rootObj = new JsonObject();
                    storage.put(rootKey, rootObj);
                } else {
                    rootObj = current.getAsJsonObject();
                }

                JsonObject obj = rootObj;
                for (int i = 1; i < keyPath.size() - 1; i++) {
                    String key = keyPath.get(i);
                    JsonElement next = obj.get(key);
                    if (next == null || !next.isJsonObject()) {
                        JsonObject newObj = new JsonObject();
                        obj.add(key, newObj);
                        obj = newObj;
                    } else {
                        obj = next.getAsJsonObject();
                    }
                }

                obj.add(keyPath.getLast(), value);
            }

            persist();
            return Response.ok();
        } finally {
            writeLock.unlock();
        }
    }


    public Response delete(List<String> keyPath) {
        writeLock.lock();
        try {
            if (keyPath.isEmpty()) {
                return Response.error("Empty key path");
            }
            if (keyPath.size() == 1) {
                String rootKey = keyPath.getFirst();
                if (!storage.containsKey(rootKey)) {
                    return Response.error("No such key");
                }
                storage.remove(rootKey);
                persist();
                return Response.ok();
            }

            JsonElement current = storage.get(keyPath.getFirst());
            if (current == null || !current.isJsonObject()) {
                return Response.error("No such key");
            }

            JsonObject obj = current.getAsJsonObject();
            for (int i = 1; i < keyPath.size() - 1; i++) {
                String key = keyPath.get(i);
                JsonElement next = obj.get(key);
                if (next == null || !next.isJsonObject()) {
                    return Response.error("No such key");
                }
                obj = next.getAsJsonObject();
            }

            obj.remove(keyPath.getLast());
            persist();
            return Response.ok();
        } finally {
            writeLock.unlock();
        }
    }

    private void persist() {
        try (Writer writer = new FileWriter(database)) {
            new Gson().toJson(storage, writer);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo.");
        }
    }
}