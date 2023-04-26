package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.UrlRequest;
import com.onix.worldtour.dto.model.UploadedFileDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.FileService;
import com.onix.worldtour.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {
    @Autowired
    public FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Response> upload(@RequestParam("file") MultipartFile[] files, @RequestParam(value = "folder", defaultValue = "") String folder) {
        try {
            List<UploadedFileDto> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                log.info("FileController::upload file {}", file.getOriginalFilename());
                String fileName = fileService.save(file, folder);
                String url = fileService.getImageUrl(fileName);

                uploadedFiles.add(new UploadedFileDto().setFileName(fileName).setUrl(url));
            }
            Response<Object> response = Response.ok().setPayload(uploadedFiles);
            log.info("FileController::upload response {}", ValueMapper.jsonAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response<Object> errorResponse = Response.badRequest().setErrors(e.getMessage());
            log.error("FileController::upload error response {}", ValueMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload-from-url")
    public ResponseEntity<Response> uploadFromUrl(@RequestBody UrlRequest urlRequest) {
        try {
            log.info("FileController::uploadFromUrl url {}", urlRequest.getUrl());
            String fileName = fileService.save(urlRequest.getUrl(), urlRequest.getFolder());
            String fileUrl = fileService.getImageUrl(fileName);

            UploadedFileDto uploadedFile = new UploadedFileDto().setFileName(fileName).setUrl(fileUrl);
            Response<Object> response = Response.ok().setPayload(uploadedFile);
            log.info("FileController::uploadFromUrl response {}", ValueMapper.jsonAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response<Object> errorResponse = Response.badRequest().setErrors(e.getMessage());
            log.error("FileController::uploadFromUrl error response {}", ValueMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
