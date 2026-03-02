// src/modal/EcoleEtablissementEditModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css";

/**
 * Modale d'édition des informations d'établissement pour ECOLE.
 *
 * Utilise l'endpoint :
 *   PUT /api/ecole/mon-etablissement
 * avec re-géocodage côté back.
 */
export default function EcoleEtablissementEditModal({
  isOpen,
  monEtab,
  onClose,
  onSaved,
}) {
  const [form, setForm] = useState({
    nomEcole: "",
    adresseL1: "",
    adresseL2: "",
    codePostal: "",
    ville: "",
    paysCode: "FR",
    niveauAccessibilite: "",
    infosAcces: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Pré-remplissage
  useEffect(() => {
    if (isOpen && monEtab) {
      setForm({
        nomEcole: monEtab.nomEcole || "",
        adresseL1: monEtab.adresseL1 || "",
        adresseL2: monEtab.adresseL2 || "",
        codePostal: monEtab.codePostal || "",
        ville: monEtab.ville || "",
        paysCode: monEtab.paysCode || "FR",
        niveauAccessibilite: monEtab.niveauAccessibilite || "",
        infosAcces: monEtab.infosAcces || "",
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
      await axiosClient.put("/ecole/mon-etablissement", form);
      if (onSaved) onSaved();
    } catch (err) {
      console.error("Erreur update établissement ECOLE", err);
      const msg =
        err.response?.data?.message ||
        "Impossible de sauvegarder les informations de l'établissement.";
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
          <h3 className="ecole-modal-title">Modifier l&apos;établissement</h3>
        </header>

        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          <section className="ecole-modal-section">
            <h4 className="ecole-modal-section-title">Coordonnées</h4>

            <div className="ecole-modal-grid">
              <div className="ecole-modal-field">
                <span className="label">Nom de l&apos;école</span>
                <input
                  type="text"
                  name="nomEcole"
                  className="ecole-modal-input"
                  value={form.nomEcole}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Adresse (ligne 1)</span>
                <input
                  type="text"
                  name="adresseL1"
                  className="ecole-modal-input"
                  value={form.adresseL1}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Complément d&apos;adresse</span>
                <input
                  type="text"
                  name="adresseL2"
                  className="ecole-modal-input"
                  value={form.adresseL2}
                  onChange={handleChange}
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Code postal</span>
                <input
                  type="text"
                  name="codePostal"
                  className="ecole-modal-input"
                  value={form.codePostal}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Ville</span>
                <input
                  type="text"
                  name="ville"
                  className="ecole-modal-input"
                  value={form.ville}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Pays</span>
                <input
                  type="text"
                  name="paysCode"
                  className="ecole-modal-input"
                  value={form.paysCode}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
          </section>

          <section className="ecole-modal-section">
            <h4 className="ecole-modal-section-title">Accessibilité</h4>

            <div className="ecole-modal-grid">
              <div className="ecole-modal-field">
                <span className="label">Niveau d&apos;accessibilité</span>
                <select
                  name="niveauAccessibilite"
                  className="ecole-modal-input"
                  value={form.niveauAccessibilite}
                  onChange={handleChange}
                >
                  <option value="">(non renseigné)</option>
                  <option value="FACILE">FACILE</option>
                  <option value="MOYEN">MOYEN</option>
                  <option value="DIFFICILE">DIFFICILE</option>
                </select>
              </div>

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Infos accès / remarques</span>
                <textarea
                  name="infosAcces"
                  className="ecole-modal-textarea"
                  rows={3}
                  value={form.infosAcces}
                  onChange={handleChange}
                />
              </div>
            </div>
          </section>

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
