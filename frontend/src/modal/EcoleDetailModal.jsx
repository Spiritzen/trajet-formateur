// src/modal/EcoleDetailModal.jsx
import { useEffect } from "react";
import "@/css/modal/EcoleDetailModal.css";

/**
 * Modale de détail d'une école pour l'ADMIN.
 *
 * Props :
 *  - isOpen : bool -> indique si la modale est visible
 *  - ecole  : objet EcoleDetailAdminResponse (backend)
 *  - onClose: fonction -> fermer la modale
 *  - onEdit : fonction -> déclencher l'édition (à brancher plus tard)
 */
export default function EcoleDetailModal({ isOpen, ecole, onClose, onEdit }) {
  // === Hook d'effet : gestion de la touche ESC ===
  // On installe l'écouteur uniquement quand la modale est ouverte.
  useEffect(() => {
    if (!isOpen) return;

    function handleKeyDown(e) {
      if (e.key === "Escape") {
        onClose();
      }
    }

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  // Si la modale n'est pas ouverte ou qu'on n'a pas encore les données, on ne rend rien.
  if (!isOpen || !ecole) {
    return null;
  }

  // Clic sur le fond (backdrop) -> fermeture
  function handleBackdropMouseDown(event) {
    if (event.target.classList.contains("ecole-modal-backdrop")) {
      onClose();
    }
  }

  return (
    <div
      className="ecole-modal-backdrop"
      onMouseDown={handleBackdropMouseDown}
    >
      <div
        className="ecole-modal-card"
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* HEADER */}
        <header className="ecole-modal-header">
          <h2 className="ecole-modal-title">
            {ecole.nomEcole || "École (sans nom)"}
          </h2>
          <p className="ecole-modal-subtitle">
            {ecole.ville || "Ville inconnue"}{" "}
            {ecole.codePostal ? `- ${ecole.codePostal}` : ""}
          </p>
        </header>

        {/* CONTENU SCROLLABLE */}
        <div className="ecole-modal-body">
          {/* Bloc infos école */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Informations établissement
            </h3>

            <div className="ecole-modal-grid">
              <div className="ecole-modal-field">
                <span className="label">Nom de l&apos;école</span>
                <span className="value">{ecole.nomEcole || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Adresse (ligne 1)</span>
                <span className="value">{ecole.adresseL1 || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Complément d&apos;adresse</span>
                <span className="value">{ecole.adresseL2 || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Code postal</span>
                <span className="value">{ecole.codePostal || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Ville</span>
                <span className="value">{ecole.ville || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Pays</span>
                <span className="value">{ecole.paysCode || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Niveau d&apos;accessibilité</span>
                <span className="value">
                  {ecole.niveauAccessibilite || "Non renseigné"}
                </span>
              </div>

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Infos accès / remarques</span>
                <span className="value">
                  {ecole.infosAcces || "Non renseigné"}
                </span>
              </div>

              {(ecole.lat != null || ecole.lon != null) && (
                <div className="ecole-modal-field ecole-modal-field-full">
                  <span className="label">Coordonnées GPS</span>
                  <span className="value">
                    {ecole.lat != null ? ecole.lat : "?"} ,{" "}
                    {ecole.lon != null ? ecole.lon : "?"}
                  </span>
                </div>
              )}
            </div>
          </section>

          {/* Bloc infos référent */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Compte référent (utilisateur ECOLE)
            </h3>

            <div className="ecole-modal-grid">
              <div className="ecole-modal-field">
                <span className="label">Prénom</span>
                <span className="value">{ecole.prenomReferent || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Nom</span>
                <span className="value">{ecole.nomReferent || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Email</span>
                <span className="value">{ecole.emailReferent || "—"}</span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Téléphone</span>
                <span className="value">
                  {ecole.telephoneReferent || "Non renseigné"}
                </span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Id utilisateur référent</span>
                <span className="value">
                  {ecole.idUserReferent != null
                    ? `#${ecole.idUserReferent}`
                    : "—"}
                </span>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Id école</span>
                <span className="value">
                  {ecole.idEcole != null ? `#${ecole.idEcole}` : "—"}
                </span>
              </div>
            </div>
          </section>
        </div>

        {/* FOOTER BOUTONS */}
        <footer className="ecole-modal-footer">
          <button
            type="button"
            className="neumo-btn neumo-btn-default"
            onClick={onEdit}
          >
            ✏️ Éditer
          </button>

          <button
            type="button"
            className="neumo-btn neumo-btn-danger"
            onClick={onClose}
          >
            Fermer
          </button>
        </footer>
      </div>
    </div>
  );
}
