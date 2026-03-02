// src/modal/EcoleResponsableAccessibiliteEditModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css";

/**
 * Modale de création / édition du Responsable Accessibilité.
 *
 * Backend :
 *   - GET  /api/ecole/mon-responsable-accessibilite
 *       → 200 + ResponsableAccessibiliteDto
 *   - PUT  /api/ecole/mon-responsable-accessibilite
 *       body: { nom, prenom, fonction, telephone, email, plageHoraire }
 *
 * Le back se charge de créer ou mettre à jour le responsable
 * pour l'école associée à l'utilisateur connecté.
 */
export default function EcoleResponsableAccessibiliteEditModal({
  isOpen,
  monEtab,
  onClose,
  onSaved,
}) {
  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    fonction: "",
    telephone: "",
    email: "",
    plageHoraire: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // -------------------------------------------------------
  // Pré-remplissage à l'ouverture de la modale
  // -------------------------------------------------------
  useEffect(() => {
    if (!isOpen) return;

    async function initForm() {
      setError(null);

      // 1) Si le DTO global monEtab contient déjà les infos du responsable,
      //    on les utilise en priorité.
      if (monEtab && monEtab.responsableId) {
        setForm({
          nom: monEtab.responsableNom || "",
          prenom: monEtab.responsablePrenom || "",
          fonction: monEtab.responsableFonction || "",
          telephone: monEtab.responsableTelephone || "",
          email: monEtab.responsableEmail || "",
          plageHoraire: monEtab.responsablePlageHoraire || "",
        });
        return;
      }

      // 2) Sinon, on interroge l'endpoint dédié GET /mon-responsable-accessibilite
      try {
        const res = await axiosClient.get(
          "/ecole/mon-responsable-accessibilite"
        );

        // 204 No Content → aucun responsable encore défini
        if (res.status === 204 || !res.data) {
          setForm({
            nom: "",
            prenom: "",
            fonction: "",
            telephone: "",
            email: "",
            plageHoraire: "",
          });
          return;
        }

        const dto = res.data; // ResponsableAccessibiliteDto
        setForm({
          nom: dto.nom || "",
          prenom: dto.prenom || "",
          fonction: dto.fonction || "",
          telephone: dto.telephone || "",
          email: dto.email || "",
          plageHoraire: dto.plageHoraire || "",
        });
      } catch (err) {
        console.error(
          "Erreur lors du chargement du responsable accessibilité",
          err
        );
        // En cas d'erreur, on laisse le formulaire vide (création)
        setForm({
          nom: "",
          prenom: "",
          fonction: "",
          telephone: "",
          email: "",
          plageHoraire: "",
        });
      }
    }

    initForm();
  }, [isOpen, monEtab]);

  if (!isOpen) return null;

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    setError(null);

    try {
      await axiosClient.put("/ecole/mon-responsable-accessibilite", form);
      if (onSaved) onSaved();
    } catch (err) {
      console.error("Erreur update responsable accessibilité", err);
      const msg =
        err.response?.data?.message ||
        "Impossible de sauvegarder le responsable accessibilité.";
      setError(msg);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="ecole-modal-backdrop" onMouseDown={onClose}>
      <div
        className="ecole-modal-card"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <header className="ecole-modal-header">
          <h3 className="ecole-modal-title">
            Responsable accessibilité de l&apos;établissement
          </h3>
        </header>

        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          <div className="ecole-modal-grid">
            <div className="ecole-modal-field">
              <span className="label">Nom</span>
              <input
                type="text"
                name="nom"
                className="ecole-modal-input"
                value={form.nom}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ecole-modal-field">
              <span className="label">Prénom</span>
              <input
                type="text"
                name="prenom"
                className="ecole-modal-input"
                value={form.prenom}
                onChange={handleChange}
                required
              />
            </div>

            <div className="ecole-modal-field">
              <span className="label">Fonction</span>
              <input
                type="text"
                name="fonction"
                className="ecole-modal-input"
                value={form.fonction}
                onChange={handleChange}
              />
            </div>

            <div className="ecole-modal-field">
              <span className="label">Téléphone</span>
              <input
                type="text"
                name="telephone"
                className="ecole-modal-input"
                value={form.telephone}
                onChange={handleChange}
              />
            </div>

            <div className="ecole-modal-field">
              <span className="label">Email</span>
              <input
                type="email"
                name="email"
                className="ecole-modal-input"
                value={form.email}
                onChange={handleChange}
              />
            </div>

            <div className="ecole-modal-field ecole-modal-field-full">
              <span className="label">Plage horaire</span>
              <input
                type="text"
                name="plageHoraire"
                className="ecole-modal-input"
                placeholder="Ex : Lun - Jeu : 9h-12h / 14h-16h"
                value={form.plageHoraire}
                onChange={handleChange}
              />
            </div>
          </div>

          {error && <div className="ecole-modal-error">{error}</div>}

          <footer className="ecole-modal-footer">
            <button
              type="submit"
              className="neumo-btn neumo-btn-success"
              disabled={saving}
            >
              {saving ? "Sauvegarde..." : "💾 Sauvegarder"}
            </button>
            <button
              type="button"
              className="neumo-btn neumo-btn-danger"
              onClick={onClose}
              disabled={saving}
            >
              Fermer
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
}
