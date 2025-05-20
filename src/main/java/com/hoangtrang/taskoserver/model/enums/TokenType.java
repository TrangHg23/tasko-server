package com.hoangtrang.taskoserver.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    ACCESS(1),
    REFRESH(24 * 7);

    private final int hoursToExpire;
}
