// src/modal/formateur/FormateurEditionProfil.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css"; // 👈 on réutilise le même style que pour les écoles

/**
 * Modale d'édition du profil FORMATEUR.
 *
 * Props :
 *  - isOpen : bool
 *  - profil : FormateurProfileResponse (GET /formateur/mon-compte)
 *  - onClose : () => void
 *  - onSaved : (profilMisAJour) => void
 */
export default function FormateurEditionProfil({ isOpen, profil, onClose, onSaved }) {
  const [form, setForm] = useState({
    telephone: "",
    adresseL1: "",
    adresseL2: "",
    codePostal: "",
    ville: "",
    paysCode: "FR",
    zoneKm: "",
    vehiculePerso: false,
    permis: false,
    commentaire: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // -------------------------------------------------------
  // Pré-remplissage quand la modale s'ouvre
  // -------------------------------------------------------
  useEffect(() => {
    if (isOpen && profil) {
      setForm({
        telephone: profil.telephone || "",
        adresseL1: profil.adresseL1 || "",
        adresseL2: profil.adresseL2 || "",
        codePostal: profil.codePostal || "",
        ville: profil.ville || "",
        paysCode: profil.paysCode || "FR",
        zoneKm: profil.zoneKm != null ? profil.zoneKm : "",
        vehiculePerso: !!profil.vehiculePerso,
        permis: !!profil.permis,
        commentaire: profil.commentaire || "",
      });
      setError(null);
    }
  }, [isOpen, profil]);

  // -------------------------------------------------------
  // Fermeture sur ESC
  // -------------------------------------------------------
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

  // -------------------------------------------------------
  // Clic sur le fond (backdrop) → fermeture
  // -------------------------------------------------------
  function handleBackdropMouseDown(event) {
    if (event.target.classList.contains("ecole-modal-backdrop")) {
      onClose();
    }
  }

  // -------------------------------------------------------
  // Gestion champs
  // -------------------------------------------------------
  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  function handleCheckboxChange(e) {
    const { name, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: checked,
    }));
  }

  // -------------------------------------------------------
  // Submit → PUT /api/formateur/mon-compte?idUser=...
  // -------------------------------------------------------
  async function handleSubmit(e) {
    e.preventDefault();

    if (!profil || !profil.idUser) {
      setError("Identifiant utilisateur manquant pour ce formateur.");
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        telephone: form.telephone || null,
        adresseL1: form.adresseL1 || null,
        adresseL2: form.adresseL2 || null,
        codePostal: form.codePostal || null,
        ville: form.ville || null,
        paysCode: form.paysCode || "FR",
        zoneKm: form.zoneKm !== "" ? Number(form.zoneKm) : null,
        vehiculePerso: form.vehiculePerso,
        permis: form.permis,
        commentaire:
          form.commentaire && form.commentaire.trim() !== ""
            ? form.commentaire.trim()
            : null,
      };

      const res = await axiosClient.put("/formateur/mon-compte", payload, {
        params: { idUser: profil.idUser },
      });

      if (onSaved) {
        onSaved(res.data);
      }
    } catch (err) {
      console.error("Erreur mise à jour profil formateur", err);
      const message =
        err.response?.data?.message ||
        "Impossible de sauvegarder les modifications du profil formateur.";
      setError(message);
    } finally {
      setSaving(false);
    }
  }

  // -------------------------------------------------------
  // Si modale fermée ou pas de profil → rien
  // -------------------------------------------------------
  if (!isOpen || !profil) {
    return null;
  }

  // -------------------------------------------------------
  // Rendu JSX – même structure que EcoleEditModal
  // -------------------------------------------------------
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
            {profil.prenom} {profil.nom}
          </h2>
          <p className="ecole-modal-subtitle">
            Profil formateur · {form.ville || "Ville inconnue"}
            {form.codePostal ? ` - ${form.codePostal}` : ""}
          </p>
        </header>

        {/* FORMULAIRE */}
        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          {/* === Coordonnées & adresse === */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Coordonnées & adresse
            </h3>

            <div className="ecole-modal-grid">
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
                <span className="label">Adresse (ligne 1)</span>
                <input
                  type="text"
                  name="adresseL1"
                  className="ecole-modal-input"
                  value={form.adresseL1}
                  onChange={handleChange}
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
                />
              </div>
            </div>
          </section>

          {/* === Mobilité === */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">Mobilité</h3>

            <div className="ecole-modal-grid">
              <div className="ecole-modal-field">
                <span className="label">Zone kilométrique (km)</span>
                <input
                  type="number"
                  name="zoneKm"
                  className="ecole-modal-input"
                  min={1}
                  max={500}
                  value={form.zoneKm}
                  onChange={handleChange}
                />
              </div>

              <div className="ecole-modal-field">
                <span className="label">Véhicule personnel</span>
                <label className="ecole-modal-checkbox">
                  <input
                    type="checkbox"
                    name="vehiculePerso"
                    checked={form.vehiculePerso}
                    onChange={handleCheckboxChange}
                  />{" "}
                  Oui
                </label>
              </div>

              <div className="ecole-modal-field">
                <span className="label">Permis de conduire</span>
                <label className="ecole-modal-checkbox">
                  <input
                    type="checkbox"
                    name="permis"
                    checked={form.permis}
                    onChange={handleCheckboxChange}
                  />{" "}
                  Oui
                </label>
              </div>

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Commentaire formateur</span>
                <textarea
                  name="commentaire"
                  className="ecole-modal-textarea"
                  rows={4}
                  value={form.commentaire}
                  onChange={handleChange}
                />
              </div>
            </div>
          </section>

          {/* ERREUR éventuelle */}
          {error && <div className="ecole-modal-error">{error}</div>}

          {/* FOOTER */}
          <footer className="ecole-modal-footer">
            <button
              type="submit"
              className="neumo-btn neumo-btn-success"
              disabled={saving}
            >
              {saving ? "Sauvegarde..." : "💾 Enregistrer"}
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
