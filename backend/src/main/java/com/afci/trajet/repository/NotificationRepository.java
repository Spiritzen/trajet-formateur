package com.afci.trajet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.Notification;

/**
 * Repository JPA pour la table NOTIFICATION.
 *
 * Permet de suivre les envois via Twilio / e-mail, etc.
 */
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Toutes les notifications liées à un ordre de mission,
     * triées de la plus récente à la plus ancienne (ou l'inverse selon le besoin).
     */
    List<Notification> findByIdOrdreMissionOrderByCreatedAtDesc(Integer idOrdreMission);

    /**
     * Recherche d'une notification via son identifiant provider (Twilio, Mailjet, ...).
     */
    Optional<Notification> findByProviderMsgId(String providerMsgId);

    /**
     * Notifications filtrées par canal (SMS, EMAIL) et état (PENDING, SENT, FAILED).
     */
    List<Notification> findByCanalAndEtat(String canal, String etat);

    /**
     * Dernières notifications créées (pour un tableau de bord, par exemple).
     */
    List<Notification> findTop50ByOrderByCreatedAtDesc();
}
