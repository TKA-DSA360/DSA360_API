package com.dsa360.api.annotatios;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class MultipartFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final List<String> ACCEPTED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File is empty or null")
                   .addConstraintViolation();
            return false;
        }

        String contentType = file.getContentType();
        if (!ACCEPTED_CONTENT_TYPES.contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid file type. Only JPEG/JPG and PNG are allowed")
                   .addConstraintViolation();
            return false;
        }

        long fileSizeInKB = file.getSize() / 1024;
        if (fileSizeInKB < 5 || fileSizeInKB > 1024) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid file size. File size should be between 5 KB and 1 MB")
                   .addConstraintViolation();
            return false;
        }
        

        return true;
    }
}
