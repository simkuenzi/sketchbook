package com.github.simkuenzi.sketchbook;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.FileRenderer;
import io.javalin.plugin.rendering.JavalinRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class Server {

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "9001"));
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
            SketchName newSketch = new FormSketchName(ctx);
            if (newSketch.isValid()) {
                ctx.redirect(newSketch.getName());
            } else {
                ctx.render("home.html", model(ctx, sketchbook(ctx), newSketch));
            }
        })
        .get("/:name", ctx -> ctx.render("sketch.html",
                model(ctx, sketchbook(ctx),  sketchbook(ctx).sketch(ctx.pathParam("name")))))
        .post("/:name", ctx -> {
            Sketch sketch = sketchbook(ctx).sketch(ctx.pathParam("name"));
            if (ctx.formParamMap().containsKey("delete")) {
                sketch.delete();
                ctx.redirect(ctx.contextPath());
            } else {
                sketch.save(ctx.formParam("content"));
                ctx.render("sketch.html", model(ctx, sketchbook(ctx), sketch));
            }
        });
    }

    private static Sketchbook sketchbook(Context ctx) throws IOException {
        Sketchbook sketchbook;
        Path pathFile = Path.of(System.getProperty("user.home"), "sketches");
        if (Files.exists(pathFile)) {
            List<String> lines = Files.readAllLines(pathFile);
            sketchbook = lines.size() > 0 ? new FilesystemSketchbook(Path.of(lines.get(0), username(ctx))) : new MissingSketchbook();
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
        String username = username(ctx);
        Map<String, Object> vars = new HashMap<>();
        Properties versionProps = new Properties();
        versionProps.load(Server.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        vars.put("username", username);
        vars.put("sketchbook", sketchbook);
        vars.put("newSketch", newSketch);
        return vars;
    }

    private static String username(Context ctx) {
        return ctx.headerMap().getOrDefault("X-SK-Auth", "anon");
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
