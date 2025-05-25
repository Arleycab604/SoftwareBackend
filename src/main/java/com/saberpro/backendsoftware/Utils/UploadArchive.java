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

    public void eliminarArchivoDeSupabase(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + System.getenv("SUPABASE_SECRET_KEY"));

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                System.out.println("Error al eliminar archivo: " + fileUrl);
            } else {
                System.out.println("Archivo eliminado correctamente: " + fileUrl);
            }
        } catch (IOException e) {
            System.out.println("Excepción al eliminar archivo: " + fileUrl);
        }
    }

    public byte[] downloadFile(String fileName) throws IOException {
        String fileUrl = SUPABASE_URL + "/" + BUCKET_NAME + "/" + fileName;

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_SECRET_KEY);

        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return connection.getInputStream().readAllBytes();
        } else {
            throw new IOException("Error al descargar archivo: " + connection.getResponseMessage());
        }
    }

}
