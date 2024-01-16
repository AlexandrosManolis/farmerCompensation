package gr.hua.dit.farmerCompensation.config;

import gr.hua.dit.farmerCompensation.entity.DeclarationForm;
import gr.hua.dit.farmerCompensation.entity.RequestForRole;
import gr.hua.dit.farmerCompensation.entity.Role;
import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Role.class);
        config.exposeIdsFor(DeclarationForm.class);
        config.exposeIdsFor(RequestForRole.class);
    }
}