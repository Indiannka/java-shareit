package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String HOST = "${shareit-server.url}";
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value(HOST) String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId, null);
    }

    public ResponseEntity<Object> getAllUserRequests(long userId, String[] sortBy) {
        Map<String, Object> parameters = Map.of("sortBy", sortBy);
        return get("?sortBy={sortBy}", userId, parameters);
    }

    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size, String[] sortBy) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "sortBy", sortBy
        );
        return get("/all?from={from}&size={size}&sortBy={sortBy}", userId, parameters);
    }
}