package com.perch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Properties specific to JHipster.
 * <p>
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "jhipster", ignoreUnknownFields = false)
public class JHipsterProperties extends CommonProperties {
  private final Gateway gateway = new Gateway();

  public Gateway getGateway() {
    return gateway;
  }

  public static class Gateway {

    private final RateLimiting rateLimiting = new RateLimiting();
    private Map<String, List<String>> authorizedMicroservicesEndpoints = new LinkedHashMap<>();

    public RateLimiting getRateLimiting() {
      return rateLimiting;
    }

    public Map<String, List<String>> getAuthorizedMicroservicesEndpoints() {
      return authorizedMicroservicesEndpoints;
    }

    public void setAuthorizedMicroservicesEndpoints(Map<String, List<String>> authorizedMicroservicesEndpoints) {
      this.authorizedMicroservicesEndpoints = authorizedMicroservicesEndpoints;
    }

    public static class RateLimiting {

      private boolean enabled = false;

      private long limit = 100000L;

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }

      public long getLimit() {
        return this.limit;
      }

      public void setLimit(long limit) {
        this.limit = limit;
      }
    }
  }
}
