package com.example.storesaas.media;

import com.example.storesaas.common.BusinessException;
import com.example.storesaas.security.AuthContext;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioStorageService {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");
    private static final String PUBLIC_POLICY = """
             {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}
            """;

    @Value("${storage.minio.enabled:false}")
    private boolean enabled;
    @Value("${storage.minio.endpoint:http://localhost:9000}")
    private String endpoint;
    @Value("${storage.minio.public-endpoint:${storage.minio.endpoint:http://localhost:9000}}") // 不配置则使用 endpoint
    private String publicEndpoint;
    @Value("${storage.minio.access-key:minioadmin}")
    private String accessKey;
    @Value("${storage.minio.secret-key:minioadmin}")
    private String secretKey;
    @Value("${storage.minio.bucket:store-images}")
    private String bucket;

    private MinioClient client;

    @PostConstruct  // 初始化方法，在依赖注入完成后调用
    void initialize() {
        if (!enabled) {
            return;
        }
        try {
            client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucket)
                    .config(PUBLIC_POLICY.formatted(bucket))
                    .build());
        } catch (Exception ex) {
            throw new IllegalStateException("MinIO 初始化失败", ex);
        }
    }

    /**
     * 上传图片
     * @param file 文件
     * @param scene 场景
     * @return 图片URL
     */
    public String upload(MultipartFile file, String scene) {
        ensureEnabled();
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String extension = CONTENT_TYPES.get(contentType);
        if (file.isEmpty() || file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片不能为空且不能超过5MB");
        }
        if (extension == null) {
            throw new BusinessException("仅支持 JPG、PNG、WebP 图片");
        }

        String type = "product".equalsIgnoreCase(scene) ? "product" : "logo";
        String object = AuthContext.tenantId() + "/" + type + "/" + UUID.randomUUID() + "." + extension;
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());
            return publicEndpoint.replaceAll("/$", "") + "/" + bucket + "/" + object;
        } catch (Exception ex) {
            throw new BusinessException("图片上传失败");
        }
    }

    public void deleteUrl(String url) {
        if (!enabled || url == null || url.isBlank()) {
            return;
        }
        try {
            URI uri = URI.create(url);
            String prefix = "/" + bucket + "/";
            if (!uri.getPath().startsWith(prefix)) {
                return;
            }
            String object = uri.getPath().substring(prefix.length());
            if (!object.startsWith(AuthContext.tenantId() + "/")) {
                return;
            }
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(object).build());
        } catch (Exception ignored) {
            // 图片清理失败不影响已经成功的业务数据更新。
        }
    }

    /**
     * 确保图片存储服务已启用
     */
    private void ensureEnabled() {
        if (!enabled || client == null) {
            throw new BusinessException("图片存储未配置");
        }
    }
}
