package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Note;
import com.whitelabel.martialarts.repository.NoteRepository;
import com.whitelabel.martialarts.service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public List<Note> getNotesByStudentId(Long studentId) {
        return noteRepository.findByStudentId(studentId);
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with ID: " + id));
    }

    // In NoteServiceImpl.java
@Override
public Note createNote(Note note) {
    if (note.getContent() == null || note.getContent().trim().isEmpty()) {
        throw new IllegalArgumentException("Note content cannot be empty");
    }
    return noteRepository.save(note);
}


    @Override
    public Note updateNote(Long id, Note updatedNote) {
        // Fetch the existing note
        Note existingNote = getNoteById(id);

        // Update fields
        existingNote.setContent(updatedNote.getContent());
        existingNote.setCreatedAt(updatedNote.getCreatedAt());

        // Save the updated note
        return noteRepository.save(existingNote);
    }

    @Override
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Note not found with ID: " + id);
        }
        noteRepository.deleteById(id);
    }
}
