package com.saberpro.backendsoftware.Utils;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class UploadArchive {

    private final SupabaseProperties supabaseProperties;
    private final S3Client s3Client;
    private static final Region REGION = Region.US_EAST_2;

    public UploadArchive(SupabaseProperties supabaseProperties) {
        this.supabaseProperties = supabaseProperties;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                supabaseProperties.getAccessKey(),
                supabaseProperties.getSecretKey()
        );

        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(supabaseProperties.getUrl()))
                .region(REGION)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
    public String uploadFile(String localFilePath, String bucketName) throws IOException {
        Path path = Path.of(localFilePath);
        if (!Files.exists(path)) {
            throw new IOException("El archivo no existe: " + localFilePath);
        }

        String key = path.getFileName().toString();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream")
                .build();

        try {
            s3Client.putObject(putRequest, path);
            System.out.println("Archivo subido correctamente: " + key);
        } catch (S3Exception e) {
            System.err.println("Error al subir archivo: " + e.awsErrorDetails().errorMessage());
            throw e;
        }

        return supabaseProperties.getUrl() + "/" + bucketName + "/" + key;
    }

    public void eliminarArchivoDeSupabase(String fileName, String bucketName) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("Archivo eliminado correctamente: " + fileName);
        } catch (S3Exception e) {
            System.err.println("Error al eliminar archivo: " + e.awsErrorDetails().errorMessage());
        }
    }

    public byte[] downloadFile(String fileName, String bucketName) throws IOException {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return s3Client.getObject(getRequest).readAllBytes();
        } catch (S3Exception e) {
            System.err.println("Error al descargar archivo: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    // Métodos de acceso a buckets específicos (opcional)
    public String getBucketPropuestas() {
        return supabaseProperties.getBucketPropuestas();
    }

    public String getBucketEvidencias() {
        return supabaseProperties.getBucketEvidencias();
    }

    public byte[] downloadFileByUrl(String url) throws IOException {
        URL downloadUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream()) {
                return inputStream.readAllBytes();
            }
        } else {
            throw new IOException("Error al descargar archivo: código HTTP " + responseCode);
        }
    }
}

