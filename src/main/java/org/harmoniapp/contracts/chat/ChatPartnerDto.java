package org.harmoniapp.contracts.chat;

/**
 * Data Transfer Object representing a chat partner.
 *
 * @param partnerId   the unique identifier of the chat partner
 * @param partnerType the type of the chat partner
 */
public record ChatPartnerDto(
        Long partnerId,
        String partnerType) {
}
