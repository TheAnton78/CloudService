package ru.netology.cloudservice.components;

import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import java.security.MessageDigest;

public class ConverterMultipartToFile implements Converter<MultipartFile, File> {
    @Override
    public File convert(MultipartFile source) {
        try {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                StringBuilder stringBuilder1 = new StringBuilder();
                StringBuilder stringBuilder2 = new StringBuilder();
                try (InputStream inputStream = source.getInputStream()) {
                    byte[] byteBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(byteBuffer)) != -1) {
                        digest.update(byteBuffer, 0, bytesRead);
                    }
                }
                byte[] hashBytes = digest.digest();
                Stream.of(hashBytes).forEach(stringBuilder1::append);

                Stream.of(source.getResource().getContentAsByteArray()).forEach(stringBuilder2::append);

                return new File(stringBuilder1.toString(),
                        stringBuilder2.toString());
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to MyCustomFile", e);
        }
    }

    private String toStringConverter(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        Stream.of(bytes).forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
