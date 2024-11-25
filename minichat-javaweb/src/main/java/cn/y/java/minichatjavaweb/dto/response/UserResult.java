package cn.y.java.minichatjavaweb.dto.response;

import lombok.Builder;

@Builder
public record UserResult(Integer id, String username) {}
