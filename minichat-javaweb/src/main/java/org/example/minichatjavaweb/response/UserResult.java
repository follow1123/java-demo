package org.example.minichatjavaweb.response;

import lombok.Builder;

@Builder
public record UserResult(Integer id, String username) {
}
