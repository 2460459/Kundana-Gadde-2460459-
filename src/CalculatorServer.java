public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(port), 0);

        server.createContext("/", CalculatorServer::handleStatic);
        server.createContext("/api/calc", CalculatorServer::handleApi);
        server.createContext("/api/health", exchange -> {
            byte[] ok = "{\"status\":\"ok\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, ok.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(ok); }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Calculator server started on port " + port);
    }

    private static void handleStatic(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException {
        java.net.URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }

        java.nio.file.Path filePath = java.nio.file.Paths.get("web" + path).normalize();
        if (!java.nio.file.Files.exists(filePath) || java.nio.file.Files.isDirectory(filePath)) {
            byte[] notFound = "404 Not Found".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, notFound.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) {
                os.write(notFound);
            }
            return;
        }

        String contentType = guessContentType(filePath.toString());
        com.sun.net.httpserver.Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", contentType);
        byte[] bytes = java.nio.file.Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, bytes.length);
        try (java.io.OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void handleApi(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException {
        com.sun.net.httpserver.Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", "application/json; charset=utf-8");
        h.set("Access-Control-Allow-Origin", "*");

        String rawQuery = exchange.getRequestURI().getRawQuery();
        java.util.Map<String, String> q = queryToMap(rawQuery);
        String op = q.get("op");
        String aStr = q.get("a");
        String bStr = q.get("b");

        if (op == null || aStr == null || bStr == null) {
            byte[] bad = "{\"error\":\"missing parameters\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bad.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(bad); }
            return;
        }

        try {
            double a = Double.parseDouble(aStr);
            double b = Double.parseDouble(bStr);
            double res;
            switch (op) {
                case "add": res = Calculator.add(a,b); break;
                case "sub": res = Calculator.subtract(a,b); break;
                case "mul": res = Calculator.multiply(a,b); break;
                case "div": res = Calculator.divide(a,b); break;
                default:
                    byte[] bad = "{\"error\":\"unknown operation\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(400, bad.length);
                    try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(bad); }
                    return;
            }
            String json = "{\"result\": " + res + "}";
            byte[] out = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, out.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(out); }
        } catch (NumberFormatException e) {
            byte[] bad = "{\"error\":\"invalid number\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bad.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(bad); }
        } catch (IllegalArgumentException e) {
            String json = "{\"error\": \"" + e.getMessage().replace("\"","\\\"") + "\"}";
            byte[] out = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, out.length);
            try (java.io.OutputStream os = exchange.getResponseBody()) { os.write(out); }
        }
    }

    private static java.util.Map<String, String> queryToMap(String query) {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] parts = param.split("=", 2);
            if (parts.length == 2) {
                String key = urlDecode(parts[0]);
                String val = urlDecode(parts[1]);
                result.put(key, val);
            }
        }
        return result;
    }

    private static String urlDecode(String s) {
        try { return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8.name()); } catch (Exception e) { return s; }
    }

    private static String guessContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=utf-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (lower.endsWith(".css")) return "text/css; charset=utf-8";
        if (lower.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}
