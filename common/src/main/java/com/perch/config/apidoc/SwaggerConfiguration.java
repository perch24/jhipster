package com.perch.config.apidoc;

import com.perch.config.CommonProperties;
import com.perch.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Springfox Swagger configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue. In that
 * case, you can use a specific Spring profile for this class, so that only front-end developers
 * have access to the Swagger view.
 */
@Configuration
@EnableSwagger2
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
@Profile(Constants.SPRING_PROFILE_SWAGGER)
public class SwaggerConfiguration {

  public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
  private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

  /**
   * Swagger Springfox configuration.
   *
   * @param jHipsterProperties the properties of the application
   * @return the Swagger Springfox configuration
   */
  @Bean
  public Docket swaggerSpringfoxDocket(CommonProperties jHipsterProperties) {
    log.debug("Starting Swagger");
    StopWatch watch = new StopWatch();
    watch.start();
    Contact contact = new Contact(
      jHipsterProperties.getSwagger().getContactName(),
      jHipsterProperties.getSwagger().getContactUrl(),
      jHipsterProperties.getSwagger().getContactEmail());

    ApiInfo apiInfo = new ApiInfo(
      jHipsterProperties.getSwagger().getTitle(),
      jHipsterProperties.getSwagger().getDescription(),
      jHipsterProperties.getSwagger().getVersion(),
      jHipsterProperties.getSwagger().getTermsOfServiceUrl(),
      contact,
      jHipsterProperties.getSwagger().getLicense(),
      jHipsterProperties.getSwagger().getLicenseUrl());

    Docket docket = new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo)
      .forCodeGeneration(true)
      .genericModelSubstitutes(ResponseEntity.class)
      .select()
      .paths(regex(DEFAULT_INCLUDE_PATTERN))
      .build();
    watch.stop();
    log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
    return docket;
  }
}