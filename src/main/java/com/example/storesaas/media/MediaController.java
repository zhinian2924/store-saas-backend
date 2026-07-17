package com.example.storesaas.media;

import cn.dev33.satoken.stp.StpUtil;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.media.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/store/media")
public class MediaController {
    private final MinioStorageService storageService;

    public MediaController(MinioStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/images")
    public ApiResponse<UploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "logo") String scene) {
        StpUtil.checkPermissionOr(Permissions.STORE_UPDATE, Permissions.PRODUCT_ADD, Permissions.PRODUCT_UPDATE);
        return ApiResponse.ok(new UploadVO(storageService.upload(file, scene)));
    }
}
