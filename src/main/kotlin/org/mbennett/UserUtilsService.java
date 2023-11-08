package org.mbennett;

import io.quarkus.runtime.Startup;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.security.Principal;

@Startup
@ApplicationScoped
public class UserUtilsService {
    private static final Logger LOG = Logger.getLogger(UserUtilsService.class);

    @Inject
    SecurityIdentity securityIdentity;

    private static SecurityIdentity instance;

    public static Principal getCustomUser() {
        LOG.info(instance.getPrincipal());
        return instance.getPrincipal();
    }

    @PostConstruct
    private void setUp() {
        instance = this.securityIdentity;
    }
}

