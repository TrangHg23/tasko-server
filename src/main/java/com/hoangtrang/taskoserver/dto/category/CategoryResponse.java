package com.hoangtrang.taskoserver.dto.category;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(UUID id, String name, OffsetDateTime createdAt) {}
