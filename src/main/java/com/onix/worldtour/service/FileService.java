package com.onix.worldtour.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    FileProperties properties;

    @EventListener
    public void init(ApplicationReadyEvent event) {

        // initialize Firebase

        try {

            ClassPathResource serviceAccount = new ClassPathResource("serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(properties.getBucketName())
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    public String getImageUrl(String name) {
        return String.format(properties.getImageUrl(), name);
    }

    public String save(MultipartFile file, String folder) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(file.getOriginalFilename());
        String objectName = properties.getBucketFolderName() + (!folder.equals("") ? "/" + folder + "/" + name : "/" + name);

        bucket.create(objectName, file.getBytes(), file.getContentType());
        return objectName;
    }

    public String save(String url, String folder) throws IOException {
        URL fileUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.connect();

        String fileName = getFileNameFromUrl(url);
        String fileType = getFileNameExtension(fileName);
        String name = generateFileName(fileName);
        String firebaseFileName = properties.getBucketFolderName() + (!folder.equals("") ? "/" + folder + "/" + name : "/" + name);

        Bucket bucket = StorageClient.getInstance().bucket();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), firebaseFileName).build();
        byte[] fileBytes = null;

        try (InputStream inputStream = connection.getInputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            fileBytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileBytes != null) {
            bucket.create(blobInfo.getName(), fileBytes, fileType);
        }

        return firebaseFileName;
    }

    private String getFileNameFromUrl(String url) {
        String[] pathSegments = url.split("/");
        String fileName = pathSegments[pathSegments.length - 1];
        return fileName;
    }

    private String getFileNameExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {

        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(originalFileName);

        bucket.create(name, bytes);

        return name;
    }

    public void delete(String name) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        if (StringUtils.isEmpty(name)) {
            throw new IOException("invalid file name");
        }

        Blob blob = bucket.get(name);

        if (blob == null) {
            throw new IOException("file not found");
        }

        blob.delete();
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    private String getFileName(String originalFileName) {
        if (originalFileName == null) {
            return null;
        } else {
            int extensionPos = originalFileName.lastIndexOf(46);
            return extensionPos == -1 ? originalFileName : originalFileName.substring(0, extensionPos);
        }
    }

    private String generateFileName(String originalFileName) {
        String fileName = getFileName(originalFileName);
        String fileExtension = getExtension(originalFileName);
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString();
        return String.format("%s_%s_%s.%s", fileName, formattedDateTime, uuid, fileExtension);
    }

    private byte[] getByteArrays(BufferedImage bufferedImage, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, format, baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            baos.close();
        }
    }
}
