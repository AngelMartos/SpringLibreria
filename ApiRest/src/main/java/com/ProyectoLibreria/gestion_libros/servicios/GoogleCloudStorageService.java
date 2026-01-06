package com.ProyectoLibreria.gestion_libros.servicios;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName = "imagenesgestionlibros"; 

    public GoogleCloudStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Bucket bucket = storage.get(bucketName);
        
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        return blob.getMediaLink(); // Devuelve la URL pública del archivo subido
    }
}
