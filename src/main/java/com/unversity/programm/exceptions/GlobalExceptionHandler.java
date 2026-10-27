package com.unversity.programm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StudentFoundException.class)
    public ResponseEntity<String>handleStudentFound(StudentFoundException studentFoundException){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(String.format("Такой студент c id = %s уже существует",studentFoundException.getMessage()));
    }


    @ExceptionHandler(OneOrManyNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleOneOrManyStudentsNotFound(OneOrManyNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", " Не найдены",
                        "missingIds", exception.getMissingIds()));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException notFoundException){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(notFoundException.getMessage());
    }

    @ExceptionHandler(AlreadyInBaseException.class)
    public ResponseEntity<String> handleAlreadyInBaseException(AlreadyInBaseException alreadyInBaseException){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(alreadyInBaseException.getMessage());
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException conflictException){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(conflictException.getMessage());
    }
    @ExceptionHandler(GroupAlreadyInCourseException.class)
    public ResponseEntity<String> handleGroupAlreadyInCourse(GroupAlreadyInCourseException groupAlreadyInCourse){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(groupAlreadyInCourse.getMessage());
    }
    @ExceptionHandler(GroupDeletionException.class)
    public ResponseEntity<String> handleGroupDeletionException(GroupDeletionException groupDeletion){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(groupDeletion.getMessage());
    }



}
