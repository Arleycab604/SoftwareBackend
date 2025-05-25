package com.saberpro.backendsoftware.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    private String accessKey;
    private String secretKey;
    private String bucketPropuestas;
    private String bucketEvidencias;
    private String url;
}
