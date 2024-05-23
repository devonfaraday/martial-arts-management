package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.ClassSession;
import java.util.List;

public interface ClassSessionService {
    List<ClassSession> getAllClassSessions();
    ClassSession getClassSessionById(Long id);
    ClassSession createClassSession(ClassSession classSession);
    ClassSession updateClassSession(Long id, ClassSession classSession);
    void deleteClassSession(Long id);
}
