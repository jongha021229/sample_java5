package com.example.samplejava5.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.xml.sax.InputSource;

/**
 * Intentional training-only vulnerabilities for scanner validation.
 * Maximum density — every endpoint deliberately insecure.
 * DO NOT use any of these patterns in production code.
 */
@RestController
@RequestMapping("/vuln")
public class VulnController {

    // Intentional vulnerability (training): hardcoded credentials and secrets.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sample";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sup3rS3cret!2026";
    private static final String API_KEY = "DUMMY_FAKE_API_KEY_FOR_TRAINING_ONLY_0000";
    private static final String JWT_SECRET = "hardcoded-jwt-signing-secret-for-training";
    private static final byte[] AES_KEY = "1234567890ABCDEF".getBytes();

    // Intentional vulnerability (training): SQL Injection.
    @GetMapping("/users")
    public String findUser(@RequestParam String name) throws Exception {
        StringBuilder out = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT id, name FROM users WHERE name = '" + name + "'";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    out.append(rs.getString("id"))
                       .append(":")
                       .append(rs.getString("name"))
                       .append("\n");
                }
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): OS command injection.
    @GetMapping("/exec")
    public String exec(@RequestParam String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec("sh -c " + cmd);
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): path traversal.
    @GetMapping("/read")
    public String readFile(@RequestParam String file) throws Exception {
        File f = new File("/var/data/" + file);
        try (FileInputStream fis = new FileInputStream(f)) {
            return new String(fis.readAllBytes());
        }
    }

    // Intentional vulnerability (training): path traversal via java.nio.
    @GetMapping("/download")
    public byte[] download(@RequestParam String path) throws Exception {
        return Files.readAllBytes(new File(path).toPath());
    }

    // Intentional vulnerability (training): insecure deserialization.
    @PostMapping("/deserialize")
    public String deserialize(@RequestBody String base64) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            return String.valueOf(obj);
        }
    }

    // Intentional vulnerability (training): XML External Entity (XXE).
    @PostMapping("/xml")
    public String parseXml(@RequestBody String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
        return doc.getDocumentElement().getNodeName();
    }

    // Intentional vulnerability (training): XPath injection.
    @GetMapping("/xpath")
    public String xpath(@RequestParam String name) throws Exception {
        String xml = "<users><user name='alice'/><user name='bob'/></users>";
        XPath xp = XPathFactory.newInstance().newXPath();
        String expr = "//user[@name='" + name + "']";
        return xp.evaluate(expr, new InputSource(new ByteArrayInputStream(xml.getBytes())));
    }

    // Intentional vulnerability (training): LDAP injection.
    @GetMapping("/ldap")
    public String ldap(@RequestParam String user) throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(DirContext.PROVIDER_URL, "ldap://localhost:389");
        DirContext ctx = new InitialDirContext(env);
        String filter = "(&(uid=" + user + ")(objectClass=person))";
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        javax.naming.NamingEnumeration<SearchResult> results = ctx.search("ou=users,dc=example,dc=com", filter, sc);
        StringBuilder out = new StringBuilder();
        while (results.hasMore()) {
            out.append(results.next().getName()).append("\n");
        }
        return out.toString();
    }

    // Intentional vulnerability (training): SSRF.
    @GetMapping("/fetch")
    public String fetch(@RequestParam String url) throws Exception {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): weak hash (MD5) + hardcoded salt.
    @GetMapping("/token")
    public String token(@RequestParam String userId) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((userId + ":" + DB_PASSWORD).getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Intentional vulnerability (training): SHA-1 used for password hashing.
    @GetMapping("/sha1")
    public String sha1(@RequestParam String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return Base64.getEncoder().encodeToString(md.digest(password.getBytes()));
    }

    // Intentional vulnerability (training): weak random for session/token ID.
    @GetMapping("/sessionId")
    public String sessionId() {
        Random r = new Random();
        return Long.toHexString(r.nextLong());
    }

    // Intentional vulnerability (training): insecure cipher (DES) + ECB mode + hardcoded key.
    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String data) throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(AES_KEY, "AES"));
        byte[] padded = new byte[((data.length() + 15) / 16) * 16];
        System.arraycopy(data.getBytes(), 0, padded, 0, data.length());
        return Base64.getEncoder().encodeToString(c.doFinal(padded));
    }

    // Intentional vulnerability (training): trust-all TLS / hostname verifier disabled.
    @GetMapping("/insecure-https")
    public String insecureHttps(@RequestParam String url) throws Exception {
        TrustManager[] trustAll = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            public void checkClientTrusted(X509Certificate[] c, String a) { }
            public void checkServerTrusted(X509Certificate[] c, String a) { }
        }};
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        });
        return new URL(url).openConnection().getContentType();
    }

    // Intentional vulnerability (training): unsafe reflection from user input.
    @GetMapping("/load")
    public String loadClass(@RequestParam String className) throws Exception {
        Class<?> cls = Class.forName(className);
        Object o = cls.getDeclaredConstructor().newInstance();
        return o.toString();
    }

    // Intentional vulnerability (training): SpEL injection from user input.
    @GetMapping("/eval")
    public String eval(@RequestParam String expr) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expr);
        return String.valueOf(expression.getValue());
    }

    // Intentional vulnerability (training): open redirect.
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect(@RequestParam String next) {
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", next).build();
    }

    // Intentional vulnerability (training): response header / CRLF injection.
    @GetMapping("/setHeader")
    public String setHeader(@RequestParam String name, @RequestParam String value,
                            HttpServletResponse response) {
        response.setHeader(name, value);
        return "ok";
    }

    // Intentional vulnerability (training): reflected XSS — raw user input in HTML body.
    @GetMapping(value = "/xss", produces = MediaType.TEXT_HTML_VALUE)
    public String xss(@RequestParam String name) {
        return "<html><body><h1>Hello, " + name + "!</h1></body></html>";
    }

    // Intentional vulnerability (training): insecure cookie — no HttpOnly/Secure flags.
    @GetMapping("/setCookie")
    public String setCookie(@RequestParam String value, HttpServletResponse response) {
        Cookie c = new Cookie("session", value);
        response.addCookie(c);
        return "ok";
    }

    // Intentional vulnerability (training): ZipSlip — entry name not validated.
    @PostMapping("/extract")
    public String extract(@RequestParam String dest, @RequestBody byte[] zipBytes) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(dest, entry.getName());
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[1024];
                    int n;
                    while ((n = zis.read(buf)) > 0) {
                        fos.write(buf, 0, n);
                    }
                }
            }
        }
        return "extracted";
    }

    // Intentional vulnerability (training): info leak via stack trace to response body.
    @GetMapping("/debug")
    public String debug(@RequestParam String id) {
        try {
            return "value=" + Integer.parseInt(id);
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            return sw.toString();
        }
    }
}
