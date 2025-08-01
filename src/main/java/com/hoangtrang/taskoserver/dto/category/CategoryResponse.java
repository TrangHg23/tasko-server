package com.hoangtrang.taskoserver.dto.category;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, long taskCount) {}