package cc.eevee.gradle.plugin.internal;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.utils.ILogger;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class LoggerWrapper implements ILogger {

  // Mapping from ILogger method call to gradle log level.
  private static final LogLevel ILOGGER_ERROR = LogLevel.ERROR;
  private static final LogLevel ILOGGER_WARNING = LogLevel.WARN;
  private static final LogLevel ILOGGER_INFO = LogLevel.LIFECYCLE;
  private static final LogLevel ILOGGER_VERBOSE = LogLevel.INFO;

  private final Logger logger;
  private String tag;

  @NonNull
  public static com.android.build.gradle.internal.LoggerWrapper getLogger(@NonNull Class<?> klass) {
    return new com.android.build.gradle.internal.LoggerWrapper(Logging.getLogger(klass));
  }

  public LoggerWrapper(@NonNull Logger logger) {
    this.logger = logger;
  }

  public LoggerWrapper tag(@Nullable String tag) {
    this.tag = tag;
    return this;
  }

  @Override
  public void error(@Nullable Throwable throwable, @Nullable String s, Object... objects) {
    if (!logger.isEnabled(ILOGGER_ERROR)) {
      return;
    }

    s = format(s, objects);

    if (throwable == null) {
      logger.log(ILOGGER_ERROR, s);
    } else {
      logger.log(ILOGGER_ERROR, s, throwable);
    }
  }

  @Override
  public void warning(@NonNull String s, Object... objects) {
    log(ILOGGER_WARNING, s, objects);
  }

  @Override
  public void info(@NonNull String s, Object... objects) {
    log(ILOGGER_INFO, s, objects);
  }

  @Override
  public void verbose(@NonNull String s, Object... objects) {
    log(ILOGGER_VERBOSE, s, objects);
  }

  private void log(@NonNull LogLevel logLevel, @NonNull String s, @Nullable Object[] objects){
    if (!logger.isEnabled(logLevel)) {
      return;
    }
    logger.log(logLevel, format(s, objects));
  }

  private String format(@Nullable String s, @Nullable Object[] objects) {
    if (s == null) {
      s = "[no message defined]";
    } else if (objects != null && objects.length > 0) {
      s = String.format(s, objects);
    }
    if (tag != null) {
      s = tag + " " + s;
    }
    return s;
  }
}
