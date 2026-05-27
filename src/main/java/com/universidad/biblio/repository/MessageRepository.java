package com.universidad.biblio.repository;

import com.universidad.biblio.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdOrderBySentAtDesc(int senderId);

    List<Message> findByReceiverIdOrderBySentAtDesc(int receiverId);

    long countByReceiverIdAndReadFalse(int receiverId);

    Optional<Message> findByIdAndReceiverId(Long id, int receiverId);
}
