package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.model.Task;
import com.hoangtrang.taskoserver.model.enums.DueType;
import com.hoangtrang.taskoserver.util.AppTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TaskDueTimeHelper {

    public static void setNoDueDate(Task task) {
        task.setDueType(DueType.NONE);
        task.setDueAtUtc(null);
    }

    public static void setDueDate(Task task, LocalDate date) {
        if (date == null) {
            throw new AppException(ErrorStatus.INVALID_DUE_DATE);
        }

        task.setDueType(DueType.DATE);

        OffsetDateTime endOfDayUtc = date
                .atTime(23, 59, 59)
                .atZone(AppTime.APP_ZONE)     // VN time
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime();

        task.setDueAtUtc(endOfDayUtc);
    }

    public static void setDueDateTime(Task task, OffsetDateTime dateTime) {
        System.out.println("dateTime = " + dateTime);
        if (dateTime == null) {
            throw new AppException(ErrorStatus.INVALID_DUE_DATE_TIME);
        }
        task.setDueType(DueType.DATE_TIME);
        task.setDueAtUtc(dateTime.withOffsetSameInstant(ZoneOffset.UTC));
    }


    public static void setDueTime(Task task, DueType dueType, LocalDate dueDate, OffsetDateTime dueDateTime) {
        if (dueType == null || dueType == DueType.NONE) {
            setNoDueDate(task);
        } else if (dueType == DueType.DATE) {
            setDueDate(task, dueDate);
        } else if (dueType == DueType.DATE_TIME) {
            setDueDateTime(task, dueDateTime);
        }
    }


    public static LocalDate extractDueDate(Task task) {
        if (task.getDueType() == null || task.getDueType() == DueType.NONE) {
            return null;
        }
        if (task.getDueAtUtc() == null) {
            return null;
        }

        return task.getDueAtUtc()
                .atZoneSameInstant(AppTime.APP_ZONE)
                .toLocalDate();
    }


    public static OffsetDateTime extractDueDateTime(Task task) {
        if (task.getDueType() == DueType.DATE_TIME && task.getDueAtUtc() != null) {
            return task.getDueAtUtc();
        }
        return null;
    }

    public static OffsetDateTime startOfDayUtc(LocalDate date) {
        return date.atStartOfDay(AppTime.APP_ZONE)          // start of day in user’s timezone
                .withZoneSameInstant(ZoneOffset.UTC) // convert to UTC
                .toOffsetDateTime();
    }

    public static OffsetDateTime endOfDayUtc(LocalDate date) {
        return date.plusDays(1).atStartOfDay(AppTime.APP_ZONE)  // start of next day
                .withZoneSameInstant(ZoneOffset.UTC) // convert to UTC
                .toOffsetDateTime();
    }

}