package cl.duoc.demomicroservicio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER_NAME = "X-API-KEY";
    private final String apiKey;

    public ApiKeyFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) Permitir el preflight (no exigir API key)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // añade headers CORS por si el CorsFilter no ejecuta antes
            addCorsHeaders(request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // (Opcional) aplica API key solo a /api/**
        // if (!request.getRequestURI().startsWith("/api/")) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        String requestApiKey = request.getHeader(API_KEY_HEADER_NAME);
        if (apiKey != null && apiKey.equals(requestApiKey)) {
            filterChain.doFilter(request, response);
        } else {
            // 2) En 401, devuelve también headers CORS para que el browser pueda leer la respuesta
            addCorsHeaders(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("API Key inválida");
        }
    }

    private void addCorsHeaders(HttpServletRequest req, HttpServletResponse res) {
        String origin = req.getHeader("Origin");
        if (origin != null) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
            res.setHeader("Access-Control-Allow-Headers", "X-API-KEY, Content-Type, Authorization, X-Requested-With");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            // res.setHeader("Access-Control-Allow-Credentials", "false"); // si aplica
        }
    }
}
