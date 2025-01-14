package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Feedback;
import com.whitelabel.martialarts.repository.FeedbackRepository;
import com.whitelabel.martialarts.service.service.FeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Long id, Feedback feedback) {
        Feedback existingFeedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        existingFeedback.setContent(feedback.getContent());
        existingFeedback.setRating(feedback.getRating());
        existingFeedback.setStudent(feedback.getStudent());
        existingFeedback.setStaff(feedback.getStaff());
        return feedbackRepository.save(existingFeedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
