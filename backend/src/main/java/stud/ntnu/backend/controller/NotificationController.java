package stud.ntnu.backend.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

  private final SimpMessagingTemplate messagingTemplate;

  public NotificationController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void sendNotification(String topic, Object payload) {
    messagingTemplate.convertAndSend(topic, payload);
  }
}
