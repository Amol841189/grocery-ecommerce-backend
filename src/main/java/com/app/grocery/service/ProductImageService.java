package com.app.grocery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageService {

    // =====================================================
    // UPLOAD DIRECTORY
    // =====================================================

    @Value("${file.upload-dir}")
    private String uploadDir;


    // =====================================================
    // ALLOWED IMAGE EXTENSIONS
    // =====================================================

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    ".png",
                    ".jpg",
                    ".jpeg",
                    ".webp"
            );


    // =====================================================
    // ALLOWED MIME TYPES
    // =====================================================

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/png",
                    "image/jpeg",
                    "image/webp"
            );


    // =====================================================
    // SAVE PRODUCT IMAGE
    // =====================================================

    public String saveProductImage(
            MultipartFile file,
            String category,
            String subCategory,
            String brand,
            String productName
    ) throws IOException {

        // -------------------------------------------------
        // 1. VALIDATE IMAGE
        // -------------------------------------------------

        validateImage(file);


        // -------------------------------------------------
        // 2. GET FILE EXTENSION
        // -------------------------------------------------

        String extension =
                getExtension(
                        file.getOriginalFilename()
                );


        // -------------------------------------------------
        // 3. GENERATE UNIQUE ID
        // -------------------------------------------------

        String uniqueId =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10);


        // -------------------------------------------------
        // 4. CREATE FILE NAME
        // -------------------------------------------------

        String fileName =
                slugify(productName)
                        + "-"
                        + uniqueId
                        + extension;


        // -------------------------------------------------
        // 5. CREATE DIRECTORY
        // -------------------------------------------------

        Path directory =
                Paths.get(
                        uploadDir,
                        slugify(category),
                        slugify(subCategory),
                        slugify(brand)
                );


        /*
         * createDirectories()
         *
         * Creates all missing directories automatically.
         *
         * Example:
         *
         * uploads/
         *   products/
         *     dairy-bread-eggs/
         *       cheese/
         *         amul/
         */

        Files.createDirectories(directory);


        // -------------------------------------------------
        // 6. CREATE COMPLETE FILE PATH
        // -------------------------------------------------

        Path filePath =
                directory.resolve(fileName);


        // -------------------------------------------------
        // 7. SAVE FILE
        // -------------------------------------------------

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // -------------------------------------------------
        // 8. RETURN RELATIVE PATH
        // -------------------------------------------------

        return "/" + filePath
                        .toString()
                        .replace("\\", "/");
    }


    // =====================================================
    // IMAGE VALIDATION
    // =====================================================

    private void validateImage(
            MultipartFile file
    ) {

        // -------------------------------------------------
        // CHECK FILE EXISTS
        // -------------------------------------------------

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Product image is required"
            );
        }


        // -------------------------------------------------
        // GET ORIGINAL FILE NAME
        // -------------------------------------------------

        String originalFilename =
                file.getOriginalFilename();


        if (originalFilename == null ||
                originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid image file"
            );
        }


        // -------------------------------------------------
        // CHECK EXTENSION
        // -------------------------------------------------

        String extension =
                getExtension(originalFilename);


        if (!ALLOWED_EXTENSIONS.contains(
                extension
        )) {

            throw new IllegalArgumentException(
                    "Invalid image format. " +
                    "Only PNG, JPG, JPEG and WEBP " +
                    "images are allowed"
            );
        }


        // -------------------------------------------------
        // CHECK MIME TYPE
        // -------------------------------------------------

        String contentType =
                file.getContentType();


        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase()
                )) {

            throw new IllegalArgumentException(
                    "Invalid image type. " +
                    "Only PNG, JPG, JPEG and WEBP " +
                    "images are allowed"
            );
        }
    }


    // =====================================================
    // GET FILE EXTENSION
    // =====================================================

    private String getExtension(
            String filename
    ) {

        if (filename == null ||
                !filename.contains(".")) {

            return "";
        }


        return filename
                .substring(
                        filename.lastIndexOf(".")
                )
                .toLowerCase();
    }


    // =====================================================
    // CREATE SAFE NAME
    // =====================================================

    private String slugify(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return "unknown";
        }


        return value
                .toLowerCase()
                .trim()
                .replaceAll(
                        "[^a-z0-9]+",
                        "-"
                )
                .replaceAll(
                        "^-|-$",
                        ""
                );
    }
}