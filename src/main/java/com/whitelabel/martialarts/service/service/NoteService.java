package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Note;
import java.util.List;

public interface NoteService {
    List<Note> getNotesByStudentId(Long studentId);
    Note getNoteById(Long id);
    Note createNote(Note note);
    Note updateNote(Long id, Note note);
    void deleteNote(Long id);
}
