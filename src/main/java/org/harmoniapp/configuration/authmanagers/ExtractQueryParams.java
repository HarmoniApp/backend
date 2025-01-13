package org.harmoniapp.configuration.authmanagers;

import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for extracting query parameters from a request.
 */
public interface ExtractQueryParams {

    /**
     * Extracts query parameters from the given RequestAuthorizationContext.
     *
     * @param ctx the RequestAuthorizationContext containing the request
     * @return a map of query parameter names and their corresponding values
     */
    default Map<String, String> getQueryParams(RequestAuthorizationContext ctx) {
        String queryStr = ctx.getRequest().getQueryString();
        if (queryStr == null) {
            return new HashMap<>();
        }
        List<String> paramsList = Arrays.stream(queryStr.split("&", -1)).toList();
        Map<String, String> paramsMap = new HashMap<>();
        for (String param : paramsList) {
            String[] split = param.split("=");
            paramsMap.put(split[0], split[1]);
        }
        return paramsMap;
    }
}
