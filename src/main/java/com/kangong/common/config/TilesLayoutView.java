package com.kangong.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.Map;

public class TilesLayoutView extends InternalResourceView {

    private final String bodyPath;

    public TilesLayoutView(String layoutPath, String bodyPath) {
        super(layoutPath);
        this.bodyPath = bodyPath;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        request.setAttribute("bodyPath", bodyPath);
        super.renderMergedOutputModel(model, request, response);
    }
}
