package org.harmoniapp.services.chat;

import org.harmoniapp.contracts.chat.ChatPartnerDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.contracts.chat.TranslationRequestDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> getChatHistory(ChatRequestDto chatRequestDto, TranslationRequestDto translationRequestDto);
    List<ChatPartnerDto> getAllChatPartners(long userId);
    String getLastMessageByUsersId(ChatRequestDto chatRequestDto);
    MessageDto createMessage(MessageDto messageDto);
    List<MessageDto> markAllMessagesAsRead(ChatRequestDto chatRequestDto);
}
