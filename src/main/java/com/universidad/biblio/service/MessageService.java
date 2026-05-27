package com.universidad.biblio.service;

import com.universidad.biblio.model.Message;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.MessageRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> list() {
        return messageRepository.findAll();
    }

    public List<Message> bySender(int userId) {
        return messageRepository.findBySenderIdOrderBySentAtDesc(userId);
    }

    public List<Message> byReceiver(int userId) {
        return messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
    }

    public Message send(int senderId, int receiverId, String subject, String content) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Usuario emisor no encontrado"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Usuario receptor no encontrado"));

        Message message = new Message(subject, content, new Date(), false, sender, receiver);
        return messageRepository.save(message);
    }

    public Message markAsRead(Long id, int receiverId) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        if (message.getReceiver() == null || !message.getReceiver().getId().equals(receiverId)) {
            throw new RuntimeException("No autorizado para marcar este mensaje");
        }
        message.setRead(true);
        return messageRepository.save(message);
    }

    public long unreadCount(int receiverId) {
        return messageRepository.countByReceiverIdAndReadFalse(receiverId);
    }
}
