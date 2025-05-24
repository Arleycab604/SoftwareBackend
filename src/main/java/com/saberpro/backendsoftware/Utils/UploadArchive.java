package com.saberpro.backendsoftware.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class UploadArchive {
    private static UploadArchive uploadArchiveToSupabase;

    public static UploadArchive getInstance() {
        if (uploadArchiveToSupabase == null) {
            uploadArchiveToSupabase = new UploadArchive();
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
    public String uploadFile(String localFilePath) throws IOException {
        File file = new File(localFilePath);
        if (!file.exists()) throw new IOException("El archivo no existe: " + localFilePath);

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String uploadUrl = SUPABASE_URL + "/" + BUCKET_NAME + "/" + file.getName();

        URL url = new URL(uploadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_SECRET_KEY);
        connection.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(fileBytes);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            System.out.println("Archivo subido correctamente a Supabase S3.");
            return uploadUrl; // ← Aquí retornamos la URL del archivo
        } else {
            throw new IOException("Error al subir archivo: " + connection.getResponseMessage());
        }
    }

}
