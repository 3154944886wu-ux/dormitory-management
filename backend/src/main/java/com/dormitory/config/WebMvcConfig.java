package com.dormitory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 上传文件不再作为无鉴权静态资源暴露，改由 {@code FileDownloadController} 按角色与归属提供。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
}
