package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Feedback;
import java.util.List;

public interface FeedbackService {
    List<Feedback> getAllFeedback();
    Feedback getFeedbackById(Long id);
    Feedback createFeedback(Feedback feedback);
    Feedback updateFeedback(Long id, Feedback feedback);
    void deleteFeedback(Long id);
}
