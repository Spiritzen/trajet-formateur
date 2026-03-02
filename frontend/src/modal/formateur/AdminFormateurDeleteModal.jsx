// src/modal/formateur/AdminFormateurDeleteModal.jsx
import { useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleDetailModal.css";
import "@/css/modal/EcoleDeleteModal.css";

export default function AdminFormateurDeleteModal({
  isOpen,
  formateur,
  onClose,
  onDeleted,
}) {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen || !formateur?.idFormateur) return null;

  const stopClick = (e) => e.stopPropagation();

  const handleConfirmDelete = async () => {
    setSubmitting(true);
    setError(null);
    try {
      await axiosClient.delete(`/admin/formateurs/${formateur.idFormateur}`);

      if (typeof onDeleted === "function") {
        onDeleted();
      }
    } catch (err) {
      console.error("Erreur soft delete formateur", err);
      const msg =
        err?.response?.status === 409
          ? "Impossible de désactiver ce formateur : des contraintes existent (OM, trajets...)."
          : err?.response?.data?.message ||
            err?.response?.data?.error ||
            "Erreur lors de la désactivation du formateur.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const displayName =
    formateur.nomAffichage ||
    `${formateur.prenom || ""} ${formateur.nom || ""}`.trim() ||
    `#${formateur.idFormateur}`;

  return (
    <div className="ecole-modal-backdrop" onClick={onClose}>
      <div className="ecole-modal-card ecole-delete-card" onClick={stopClick}>
        <div className="ecole-modal-header">
          <h2 className="ecole-delete-title">Désactiver ce formateur ?</h2>
          <p className="ecole-modal-subtitle">
            Cette opération applique un <strong>soft delete</strong> : le compte reste en
            base mais n&apos;est plus actif.
          </p>
        </div>

        <div className="ecole-modal-body ecole-delete-body">
          <p className="ecole-delete-warning-text">
            Vous êtes sur le point de désactiver le formateur suivant :
          </p>

          <ul className="ecole-delete-list">
            <li>
              <strong>Formateur :</strong> {displayName}
            </li>
            {formateur.email && (
              <li>
                <strong>Email :</strong> {formateur.email}
              </li>
            )}
            {formateur.ville && (
              <li>
                <strong>Ville :</strong> {formateur.ville}
              </li>
            )}
          </ul>

          <p className="ecole-delete-subwarning">
            Le formateur ne pourra plus se connecter. Les liens avec les ordres de
            mission existants restent toutefois en base pour l&apos;historique.
          </p>

          {error && <div className="ecole-delete-error">{error}</div>}
        </div>

        <div className="ecole-modal-footer ecole-delete-footer">
          <button
            type="button"
            className="neumo-btn neumo-btn-default"
            onClick={onClose}
            disabled={submitting}
          >
            Annuler
          </button>
          <button
            type="button"
            className="neumo-btn neumo-btn-danger"
            onClick={handleConfirmDelete}
            disabled={submitting}
          >
            {submitting ? "Désactivation..." : "Confirmer la désactivation"}
          </button>
        </div>
      </div>
    </div>
  );
}
