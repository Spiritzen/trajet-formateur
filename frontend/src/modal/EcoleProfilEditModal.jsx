// src/modal/EcoleProfilEditModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css"; // tu peux créer ce fichier ou réutiliser celui des modales admin

/**
 * Modale d'édition du PROFIL de l'utilisateur ECOLE.
 *
 * Utilise l'endpoint :
 *   PUT /api/ecole/mon-profil
 */
export default function EcoleProfilEditModal({
  isOpen,
  monEtab,
  onClose,
  onSaved,
}) {
  const [form, setForm] = useState({
    prenom: "",
    nom: "",
    telephone: "",
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Pré-remplir les champs dès qu'on ouvre la modale
  useEffect(() => {
    if (isOpen && monEtab) {
      setForm({
        prenom: monEtab.prenom || "",
        nom: monEtab.nom || "",
        telephone: monEtab.telephone || "",
      });
      setError(null);
    }
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
      await axiosClient.put("/ecole/mon-profil", form);
      if (onSaved) onSaved();
    } catch (err) {
      console.error("Erreur update profil ECOLE", err);
      const msg =
        err.response?.data?.message ||
        "Impossible de sauvegarder les informations de profil.";
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
          <h3 className="ecole-modal-title">Modifier mes informations</h3>
        </header>

        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          <div className="ecole-modal-grid">
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
              <span className="label">Téléphone</span>
              <input
                type="text"
                name="telephone"
                className="ecole-modal-input"
                value={form.telephone}
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
