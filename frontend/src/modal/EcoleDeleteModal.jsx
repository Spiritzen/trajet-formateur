// src/modal/EcoleDeleteModal.jsx
import "@/css/modal/EcoleDeleteModal.css";

/**
 * Modale de confirmation de suppression d'une école.
 *
 * Props :
 *  - isOpen       : bool → visibilité de la modale
 *  - ecole        : objet EcoleResponse (nomEcole, ville, codePostal, nomReferent...)
 *  - loading      : bool → état de la requête DELETE
 *  - errorMessage : string|null → message d'erreur (ex: 409)
 *  - onCancel     : fonction → fermer la modale sans supprimer
 *  - onConfirm    : fonction → exécuter la suppression
 */
export default function EcoleDeleteModal({
  isOpen,
  ecole,
  loading,
  errorMessage,
  onCancel,
  onConfirm,
}) {
  if (!isOpen || !ecole) {
    return null;
  }

  const labelNom = ecole.nomEcole || "École (sans nom)";
  const labelVille = ecole.ville || "ville inconnue";
  const labelCp = ecole.codePostal || "";

  return (
    <div className="ecole-modal-backdrop" onMouseDown={onCancel}>
      <div
        className="ecole-modal-card ecole-delete-card"
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* HEADER */}
        <header className="ecole-modal-header">
          <h2 className="ecole-delete-title">
            Supprimer cette école ?
          </h2>
          <p className="ecole-modal-subtitle">
            {labelNom} — {labelVille} {labelCp && `(${labelCp})`}
          </p>
        </header>

        {/* CONTENU */}
        <div className="ecole-modal-body ecole-delete-body">
          <p className="ecole-delete-warning-text">
            Vous êtes sur le point de supprimer cette école de la plateforme.
          </p>

          <ul className="ecole-delete-list">
            <li>
              <strong>Les données de l&apos;école</strong> seront supprimées
              définitivement.
            </li>
            <li>
              Le compte <strong>référent ECOLE</strong> n&apos;est pas supprimé
              automatiquement.
            </li>
            <li>
              Si des <strong>ordres de mission, trajets, signatures</strong> ou
              autres données sont encore rattachés à cette école, la base de
              données peut refuser la suppression.
            </li>
          </ul>

          <p className="ecole-delete-subwarning">
            En cas de blocage, un message vous indiquera qu&apos;il reste des
            données liées à cette école. Vous pourrez alors traiter ces données
            avant de réessayer.
          </p>

          {errorMessage && (
            <div className="ecole-delete-error">
              {errorMessage}
            </div>
          )}
        </div>

        {/* FOOTER BOUTONS */}
        <footer className="ecole-modal-footer ecole-delete-footer">
          <button
            type="button"
            className="neumo-btn neumo-btn-default"
            onClick={onCancel}
            disabled={loading}
          >
            Annuler
          </button>

          <button
            type="button"
            className="neumo-btn neumo-btn-danger"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? "Suppression..." : "🗑 Supprimer l'école"}
          </button>
        </footer>
      </div>
    </div>
  );
}
