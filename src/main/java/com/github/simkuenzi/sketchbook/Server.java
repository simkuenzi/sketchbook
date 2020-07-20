package com.github.simkuenzi.sketchbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simkuenzi.service.LocalEndpoint;
import com.github.simkuenzi.service.Registry;
import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.FileRenderer;
import io.javalin.plugin.rendering.JavalinRenderer;
import org.eclipse.jetty.http.HttpStatus;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Server {

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "9000"));
        String context = System.getProperty("com.github.simkuenzi.http.context", "/sketchbook");

        JavalinRenderer.register(renderer(), ".html");

        Javalin.create(config -> {
            config.contextPath = context;
            config.addStaticFiles("com/github/simkuenzi/sketchbook/static/");

            // Got those errors on the apache proxy with compression enabled. Related to the Issue below?
            // AH01435: Charset null not supported.  Consider aliasing it?, referer: http://pi/one-egg/
            // AH01436: No usable charset information; using configuration default, referer: http://pi/one-egg/
            config.compressionStrategy(CompressionStrategy.NONE);
        })

        // Workaround for https://github.com/tipsy/javalin/issues/1016
        // Aside from mangled up characters the wrong encoding caused apache proxy to fail on style.css.
        // Apache error log: AH01385: Zlib error -2 flushing zlib output buffer ((null))
        .before(ctx -> {
            if (ctx.res.getCharacterEncoding().equals("utf-8")) {
                ctx.res.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
        })
        .start(port)

        .get("/", ctx -> ctx.render("home.html", model(ctx,  sketchbook(ctx))))
        .post("/", ctx -> {
            Sketchbook sketchbook = sketchbook(ctx);
            SketchName newSketch = new FormSketchName(sketchbook, ctx.formParamMap());
            if (newSketch.getValidity() == NameValidity.VALID) {
                ctx.redirect(URLEncoder.encode(SketchId.forName(newSketch.getValue()).toString(), StandardCharsets.UTF_8));
            } else {
                ctx.render("home.html", model(ctx, sketchbook, newSketch));
            }
        })
        .get("/:id", ctx -> ctx.render("sketch.html",
                model(ctx, sketchbook(ctx), sketchbook(ctx).sketch(new SketchId(ctx.pathParam("id"))))))
        .post("/:id", ctx -> {
            Sketchbook sketchbook = sketchbook(ctx);
            ValidSketch sketch = sketchbook.sketch(new SketchId(ctx.pathParam("id")));
            if (ctx.formParamMap().containsKey("delete")) {
                sketch.delete();
                ctx.redirect(ctx.contextPath());
            } else {
                FormSketch formSketch = new FormSketch(sketchbook, new SketchId(ctx.pathParam("id")), ctx.formParamMap());
                formSketch.save(
                        s -> ctx.render("sketch.html", model(ctx, sketchbook, s)),
                        s -> ctx.redirect(URLEncoder.encode(s.getId().toString(), StandardCharsets.UTF_8)));
            }
        })
        .post("/api/:baseName", ctx -> {
            String content = new ObjectMapper().readTree(ctx.body()).get("content").asText();
            new IncomingSketch(ctx.pathParam("baseName"), content).save(sketchbook(ctx));
            ctx.status(HttpStatus.NO_CONTENT_204);
        });

        Registry.local.register("sketchbook", new LocalEndpoint(port, context));
    }

    private static Sketchbook sketchbook(Context ctx) throws IOException {
        Sketchbook sketchbook;
        Path pathFile = Path.of(System.getProperty("user.home"), "sketches");
        if (Files.exists(pathFile)) {
            List<String> lines = Files.readAllLines(pathFile);
            sketchbook = lines.size() > 0 ? new FilesystemSketchbook(Path.of(lines.get(0), user(ctx))) : new MissingSketchbook();
        } else {
            sketchbook = new MissingSketchbook();
        }
        return sketchbook;
    }

    private static Map<String, Object> model(Context ctx, Sketchbook sketchbook, Sketch sketch) throws IOException {
        Map<String, Object> vars = model(ctx, sketchbook);
        vars.put("sketch", sketch);
        return vars;
    }

    private static Map<String, Object> model(Context ctx, Sketchbook sketchbook) throws IOException {
        return model(ctx, sketchbook, new EmptySketchName());
    }

    private static Map<String, Object> model(Context ctx, Sketchbook sketchbook, SketchName newSketch) throws IOException {
        String username = user(ctx);
        Map<String, Object> vars = new HashMap<>();
        Properties versionProps = new Properties();
        versionProps.load(Server.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        vars.put("username", username);
        vars.put("sketchbook", sketchbook);
        vars.put("newSketch", newSketch);
        return vars;
    }

    private static String user(Context ctx) {
        return ctx.headerMap().getOrDefault("X-SK-Auth", System.getProperty("com.github.simkuenzi.dev.user", "anon"));
    }

    private static FileRenderer renderer() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/com/github/simkuenzi/sketchbook/templates/");
        templateResolver.setCacheable(false);
        templateResolver.setForceTemplateMode(true);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return (filePath, model, context) -> {
            WebContext thymeleafContext = new WebContext(context.req, context.res, context.req.getServletContext(), context.req.getLocale());
            thymeleafContext.setVariables(model);
            return templateEngine.process(filePath, thymeleafContext);
        };
    }
}
