package io.quarkiverse.langchain4j.sample.chatbot;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.image.Image;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@jakarta.ws.rs.Path("/computer")
public class Computer {

    Robot robot;

    @PostConstruct
    public void init() throws AWTException {
        robot = new Robot();
    }

    Map<String, Path> screenshots = new HashMap<>();

    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("fetch/{key}")
    @jakarta.ws.rs.Produces("image/png")
    public InputStream fetch(String key) throws IOException {
        return Files.newInputStream(screenshots.get(key));
    }

    @Tool("Get a screenshot of the current screen")
    public Image takeScreenshot() {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);

            String uuid = java.util.UUID.randomUUID().toString();
            Path file = Path.of(uuid + ".png");
            ImageIO.write(screenFullImage, "png", file.toFile());

            screenshots.put(uuid, file);

            return Image.builder().url("https://localhost:8080/computer/fetch/" + uuid).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
