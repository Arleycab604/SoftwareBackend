package com.saberpro.backendsoftware.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class UploadArchiveToSupabase {
    private static UploadArchiveToSupabase uploadArchiveToSupabase;

    public static UploadArchiveToSupabase getInstance() {
        if (uploadArchiveToSupabase == null) {
            uploadArchiveToSupabase = new UploadArchiveToSupabase();
        }
        return uploadArchiveToSupabase;
    }

    private static final String SUPABASE_URL = System.getenv("SUPABASE_URL"); // e.g., https://hubzlyyshzyfcukabszn.supabase.co/storage/v1/s3
    private static final String SUPABASE_SECRET_KEY = System.getenv("SUPABASE_SECRET_KEY");
    private static final String BUCKET_NAME = "propuestas-de-mejora";

    /**
     * Sube un archivo local a Supabase usando la API S3
     * @param localFilePath Ruta local del archivo
     */
    public void uploadFile(String localFilePath) throws IOException {
        File file = new File(localFilePath);
        if (!file.exists()) {
            throw new IOException("El archivo no existe: " + localFilePath);
        }

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // Construye URL estilo S3: <base>/<bucket>/<path>
        String uploadUrl = SUPABASE_URL + "/" + BUCKET_NAME + "/" + file.getName();
        URL url = new URL(uploadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT"); // PUT para subir o reemplazar
        connection.setDoOutput(true);

        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_SECRET_KEY);
        connection.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(fileBytes);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Código de respuesta: " + responseCode);

        if (responseCode >= 200 && responseCode < 300) {
            System.out.println("Archivo subido correctamente a Supabase S3.");
        } else {
            System.err.println("Error al subir archivo: " + connection.getResponseMessage());
        }
    }
}
