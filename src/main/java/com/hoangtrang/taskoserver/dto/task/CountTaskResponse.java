package com.hoangtrang.taskoserver.dto.task;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CountTaskResponse{
    long inbox;
    long today;
    long overdue;
    long upcoming;
}

