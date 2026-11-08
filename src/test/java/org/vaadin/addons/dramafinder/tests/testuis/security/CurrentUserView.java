package org.vaadin.addons.dramafinder.tests.testuis.security;

import java.util.stream.Collectors;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/** Shows who the server thinks is signed in (issue #216). */
@Route("security/current-user")
public class CurrentUserView extends Main {

    public CurrentUserView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            add(new Span("Not signed in"));
        } else {
            add(new Span("Signed in as " + authentication.getName()));
            add(new Span("Authorities: " + authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).sorted().collect(Collectors.joining(","))));
        }
    }
}
