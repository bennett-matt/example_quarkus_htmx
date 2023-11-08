package org.mbennett;

import io.quarkus.qute.TemplateGlobal;
import org.jboss.logging.Logger;

import java.security.Principal;

@TemplateGlobal
public class Globals {
    @TemplateGlobal(name = "currentUser")
    static Principal user() {
        return UserUtilsService.getCustomUser();
    }
}
