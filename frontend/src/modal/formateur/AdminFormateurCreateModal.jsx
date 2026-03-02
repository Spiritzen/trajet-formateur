// src/modal/formateur/AdminFormateurCreateModal.jsx
import { useState, useEffect } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css";   // on réutilise le style édition école
import "@/css/modal/EcoleDetailModal.css"; // mêmes grilles, footer, etc.

const ZONE_KM_OPTIONS = [5, 10, 20, 50, 100, 200, 500];

export default function AdminFormateurCreateModal({ isOpen, onClose, onSaved }) {
  const [form, setForm] = useState({
    nomAffichage: "",
    prenom: "",
    nom: "",
    email: "",
    telephone: "",
    zoneKm: 20,
    adresseL1: "",
    adresseL2: "",
    codePostal: "",
    ville: "",
    paysCode: "FR",
    commentaire: "",
    permis: false,
    vehiculePerso: false,
  });

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Reset du formulaire à l’ouverture
  useEffect(() => {
    if (isOpen) {
      setForm({
        nomAffichage: "",
        prenom: "",
        nom: "",
        email: "",
        telephone: "",
        zoneKm: 20,
        adresseL1: "",
        adresseL2: "",
        codePostal: "",
        ville: "",
        paysCode: "FR",
        commentaire: "",
        permis: false,
        vehiculePerso: false,
      });
      setError(null);
      setSubmitting(false);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  // Helpers de mise à jour des champs texte / select
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Helpers de mise à jour des checkboxes
  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: checked,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      // On prépare le payload aligné avec AdminFormateurCreateRequest
      const payload = {
        nomAffichage: form.nomAffichage || `${form.prenom} ${form.nom}`.trim(),
        prenom: form.prenom || null,
        nom: form.nom || null,
        email: form.email,
        telephone: form.telephone || null,
        zoneKm: form.zoneKm != null ? Number(form.zoneKm) : null,
        adresseL1: form.adresseL1 || null,
        adresseL2: form.adresseL2 || null,
        codePostal: form.codePostal || null,
        ville: form.ville || null,
        paysCode: form.paysCode || "FR",
        commentaire: form.commentaire || null,
        permis: form.permis,
        vehiculePerso: form.vehiculePerso,
      };

      await axiosClient.post("/admin/formateurs", payload);

      if (typeof onSaved === "function") {
        onSaved();
      }
    } catch (err) {
      console.error("Erreur création formateur", err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Impossible de créer le formateur.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const stopClick = (e) => e.stopPropagation();

  return (
    <div className="ecole-modal-backdrop" onClick={onClose}>
      <div className="ecole-modal-card" onClick={stopClick}>
        {/* HEADER */}
        <div className="ecole-modal-header">
          <h2 className="ecole-modal-title">Nouveau formateur</h2>
          <p className="ecole-modal-subtitle">
            Création du compte utilisateur + fiche formateur (lat/lon via géocoding).
          </p>
        </div>

        {/* CORPS */}
        <form onSubmit={handleSubmit} className="ecole-modal-body">
          {/* SECTION IDENTITÉ */}
          <div className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">Identité & compte</h3>

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
                <span className="label">Email (login)</span>
                <input
                  type="email"
                  name="email"
                  className="ecole-modal-input"
                  value={form.email}
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

              {/* Zone KM + Permis / Véhicule perso */}
              <div className="ecole-modal-field">
                <span className="label">Zone kilométrique</span>
                <select
                  name="zoneKm"
                  className="ecole-modal-select"
                  value={form.zoneKm}
                  onChange={handleChange}
                  required
                >
                  <option value="">Choisir...</option>
                  {ZONE_KM_OPTIONS.map((km) => (
                    <option key={km} value={km}>
                      {km} km
                    </option>
                  ))}
                </select>
              </div>

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Permis & véhicule</span>
                <div
                  style={{
                    display: "flex",
                    gap: "18px",
                    alignItems: "center",
                    padding: "6px 10px",
                  }}
                >
                  <label style={{ display: "flex", gap: "6px", alignItems: "center" }}>
                    <input
                      type="checkbox"
                      name="permis"
                      checked={form.permis}
                      onChange={handleCheckboxChange}
                    />
                    <span>Permis B</span>
                  </label>

                  <label style={{ display: "flex", gap: "6px", alignItems: "center" }}>
                    <input
                      type="checkbox"
                      name="vehiculePerso"
                      checked={form.vehiculePerso}
                      onChange={handleCheckboxChange}
                    />
                    <span>Véhicule personnel</span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          {/* SECTION ADRESSE */}
          <div className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">Adresse (géocodage)</h3>
            <div className="ecole-modal-grid">
              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Adresse ligne 1</span>
                <input
                  type="text"
                  name="adresseL1"
                  className="ecole-modal-input"
                  value={form.adresseL1}
                  onChange={handleChange}
                />
              </div>

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Adresse ligne 2</span>
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
                  placeholder="FR"
                />
              </div>
            </div>
          </div>

          {/* SECTION COMMENTAIRE */}
          <div className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">Commentaire formateur</h3>
            <div className="ecole-modal-grid">
              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Commentaire / spécialités</span>
                <textarea
                  name="commentaire"
                  className="ecole-modal-textarea"
                  value={form.commentaire}
                  onChange={handleChange}
                  placeholder="Notes internes : spécialités, dispo, préférences de déplacement..."
                />
              </div>
            </div>
          </div>

          {error && (
            <div
              style={{
                marginTop: "8px",
                marginBottom: "4px",
                padding: "8px 10px",
                borderRadius: "12px",
                background: "#fee2e2",
                color: "#7f1d1d",
                fontSize: "0.85rem",
              }}
            >
              {error}
            </div>
          )}

          {/* FOOTER */}
          <div className="ecole-modal-footer">
            <button
              type="button"
              className="neumo-btn neumo-btn-default"
              onClick={onClose}
              disabled={submitting}
            >
              Annuler
            </button>
            <button
              type="submit"
              className="neumo-btn neumo-btn-success"
              disabled={submitting}
            >
              {submitting ? "Création..." : "Créer le formateur"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
