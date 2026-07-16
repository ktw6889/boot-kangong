package com.kangong.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TilesConfiguration implements WebMvcConfigurer {

    @Bean
    public ViewResolver layoutViewResolver() {
        return (viewName, locale) -> {
            if (viewName.startsWith("kims:")) {
                String bodyPath = "/views" + viewName.substring(5) + ".jsp";
                return new TilesLayoutView("/views/tilesLayout/baseLayout.jsp", bodyPath);
            }
            if (viewName.startsWith("/login/")) {
                String bodyPath = "/views" + viewName + ".jsp";
                return new TilesLayoutView("/views/tilesLayout/loginLayout.jsp", bodyPath);
            }
            return null;
        };
    }
}
