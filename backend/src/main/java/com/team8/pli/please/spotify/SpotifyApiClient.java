package com.team8.pli.please.spotify;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SpotifyApiClient {

    private final OAuth2AuthorizedClientService clientService;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();

    private String token(Authentication authentication) {
        var client = clientService.loadAuthorizedClient("spotify", authentication.getName());
        if (client == null || client.getAccessToken() == null) throw new IllegalStateException("Spotify token missing");
        return client.getAccessToken().getTokenValue();
    }

    public Mono<Object> me(Authentication authentication) {
        return webClient.get().uri("/me")
                .headers(header -> header.setBearerAuth(token(authentication)))
                .retrieve().bodyToMono(Object.class);
    }

    public Mono<Object> myPlaylists(Authentication authentication, int limit, int offset) {
        return webClient.get().uri(b -> b.path("/me/playlists").queryParam("limit", limit).queryParam("offset", offset).build())
                .headers(header -> header.setBearerAuth(token(authentication)))
                .retrieve().bodyToMono(Object.class);
    }
}
