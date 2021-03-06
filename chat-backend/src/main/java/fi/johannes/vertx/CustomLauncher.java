package fi.johannes.vertx;

import fi.johannes.chat.ChatVerticle;
import fi.johannes.chat.history.ChatHistoryVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class CustomLauncher extends Launcher {
  public static final String APP_ENVIRONMENT_PROPERTY = "APP_ENVIRONMENT";
  protected static final Logger logger = LoggerFactory.getLogger(CustomLauncher.class);

  public CustomLauncher() {}

  private static final CLI cli = CLI.create("main")
    .addOption(new Option()
      .setShortName("env")
      .setLongName("environment")
      .setDefaultValue("dev")
    );

  public static void main(String[] args) {
    initDefaults(Arrays.asList(args));
    new CustomLauncher().dispatch(args);
  }

  public static void executeCommand(String cmd, String... args) {
    initDefaults(Arrays.asList(args));
    new CustomLauncher().execute(cmd, args);
  }

  public static void initDefaults(List<String> args) {
    CommandLine parse = cli.parse(args);
    String env = parse.getOptionValue("env");
    if (env != null && !env.isEmpty()) {
      System.setProperty(APP_ENVIRONMENT_PROPERTY, env);
      System.setProperty("vertx-config-path", String.format("conf/%s-application-conf.json", env));
    }
  }
}
