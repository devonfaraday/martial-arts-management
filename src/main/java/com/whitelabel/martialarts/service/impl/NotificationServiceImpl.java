package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Notification;
import com.whitelabel.martialarts.repository.NotificationRepository;
import com.whitelabel.martialarts.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification updateNotification(Long id, Notification notification) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        existingNotification.setMessage(notification.getMessage());
        existingNotification.setType(notification.getType());
        existingNotification.setRead(notification.isRead());
        existingNotification.setAppUser(notification.getAppUser());
        return notificationRepository.save(existingNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
