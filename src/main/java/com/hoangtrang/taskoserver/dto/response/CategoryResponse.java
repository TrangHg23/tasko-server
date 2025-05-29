package com.hoangtrang.taskoserver.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(UUID id, String name, OffsetDateTime createdAt) {}
