// src/modal/EcoleEditModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css";

/**
 * Modale d'édition d'une école pour l'ADMIN.
 *
 * Permet :
 *  - de modifier les informations de l'ÉTABLISSEMENT (école)
 *  - de modifier les informations du RÉFÉRENT (utilisateur ECOLE)
 *
 * Fallback GPS :
 *  - le front n'envoie PAS lat/lon ;
 *  - le backend recalcule systématiquement lat/lon à partir de l'adresse
 *    (via GeocodingService : adresse complète puis fallback CP + ville).
 *
 * Props :
 *  - isOpen       : bool → visibilité de la modale
 *  - initialEcole : EcoleDetailAdminResponse (backend) pour pré-remplir le formulaire
 *  - onClose      : fonction → fermer la modale sans sauvegarder
 *  - onSaved      : fonction → callback après succès de la sauvegarde
 *                    (ex : recharger la liste + recharger le détail)
 */
export default function EcoleEditModal({
  isOpen,
  initialEcole,
  onClose,
  onSaved,
}) {
  // ⚠ Hooks toujours au début du composant (jamais dans un if)
  const [form, setForm] = useState({
    // === ÉTABLISSEMENT ===
    idEcole: null,
    nomEcole: "",
    adresseL1: "",
    adresseL2: "",
    codePostal: "",
    ville: "",
    paysCode: "",
    niveauAccessibilite: "", // FACILE / MOYENNE / DIFFICILE
    infosAcces: "",

    // === RÉFÉRENT ECOLE (utilisateur) ===
    emailReferent: "",
    prenomReferent: "",
    nomReferent: "",
    telephoneReferent: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // ------------------------------------------------------------------
  // Quand la modale s'ouvre ou que l'école change → pré-remplir le form
  // ------------------------------------------------------------------
  useEffect(() => {
    if (isOpen && initialEcole) {
      setForm({
        // École
        idEcole: initialEcole.idEcole ?? null,
        nomEcole: initialEcole.nomEcole ?? "",
        adresseL1: initialEcole.adresseL1 ?? "",
        adresseL2: initialEcole.adresseL2 ?? "",
        codePostal: initialEcole.codePostal ?? "",
        ville: initialEcole.ville ?? "",
        paysCode: initialEcole.paysCode ?? "",
        // Si non renseigné, on peut laisser chaîne vide → backend appliquera
        // éventuellement une valeur par défaut ou laissera NULL.
        niveauAccessibilite: initialEcole.niveauAccessibilite ?? "",
        infosAcces: initialEcole.infosAcces ?? "",

        // Référent
        emailReferent: initialEcole.emailReferent ?? "",
        prenomReferent: initialEcole.prenomReferent ?? "",
        nomReferent: initialEcole.nomReferent ?? "",
        telephoneReferent: initialEcole.telephoneReferent ?? "",
      });
      setError(null);
    }
  }, [isOpen, initialEcole]);

  // ------------------------------------------------------------------
  // Fermeture sur ESC
  // ------------------------------------------------------------------
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

  // ------------------------------------------------------------------
  // Clic sur le fond (backdrop) → fermeture
  // ------------------------------------------------------------------
  function handleBackdropMouseDown(event) {
    if (event.target.classList.contains("ecole-modal-backdrop")) {
      onClose();
    }
  }

  // ------------------------------------------------------------------
  // Gestion des champs du formulaire
  // ------------------------------------------------------------------
  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  // ------------------------------------------------------------------
  // Soumission → appel PUT /api/admin/ecoles/{idEcole}
  //
  // On envoie un DTO EcoleUpdateAdminRequest :
  //  - infos établissement
  //  - infos référent
  //  - SANS lat/lon (recalculés côté back)
  // ------------------------------------------------------------------
  async function handleSubmit(e) {
    e.preventDefault();
    if (!form.idEcole) {
      setError("Identifiant d'école manquant.");
      return;
    }

    setSaving(true);
    setError(null);

    try {
      // Normalisation du niveau d'accessibilité
      let niveau = form.niveauAccessibilite
        ? form.niveauAccessibilite.toUpperCase()
        : null;

      const allowed = ["FACILE", "MOYENNE", "DIFFICILE"];
      if (niveau && !allowed.includes(niveau)) {
        // Si une valeur inattendue arrive, on force à null
        niveau = null;
      }

      // DTO attendu par EcoleService.updateEcole(id, EcoleUpdateAdminRequest)
      const payload = {
        // === ÉTABLISSEMENT ===
        nomEcole: form.nomEcole,
        adresseL1: form.adresseL1,
        adresseL2: form.adresseL2 || null,
        codePostal: form.codePostal,
        ville: form.ville,
        paysCode: form.paysCode,
        niveauAccessibilite: niveau, // FACILE / MOYENNE / DIFFICILE / null
        infosAcces: form.infosAcces || null,

        // === RÉFÉRENT ECOLE ===
        emailReferent: form.emailReferent,
        prenomReferent: form.prenomReferent,
        nomReferent: form.nomReferent,
        telephoneReferent: form.telephoneReferent || null,
      };

      await axiosClient.put(`/admin/ecoles/${form.idEcole}`, payload);

      // On prévient le parent que la sauvegarde est OK (rechargement etc.)
      if (onSaved) {
        onSaved();
      }
    } catch (err) {
      console.error("Erreur lors de la mise à jour de l'école", err);
      const message =
        err.response?.data?.message ||
        "Impossible de sauvegarder les modifications.";
      setError(message);
    } finally {
      setSaving(false);
    }
  }

  // ------------------------------------------------------------------
  // Si la modale n'est pas ouverte ou qu'on n'a pas de données : rien
  // ------------------------------------------------------------------
  if (!isOpen || !initialEcole) {
    return null;
  }

  // ------------------------------------------------------------------
  // Rendu JSX
  // ------------------------------------------------------------------
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
            {form.nomEcole || "École (sans nom)"}
          </h2>
          <p className="ecole-modal-subtitle">
            {form.ville || "Ville inconnue"}{" "}
            {form.codePostal ? `- ${form.codePostal}` : ""}
          </p>
        </header>

        {/* FORMULAIRE SCROLLABLE */}
        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          {/* === Informations établissement === */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Informations établissement
            </h3>

            <div className="ecole-modal-grid">
              {/* Nom école */}
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

              {/* Adresse L1 */}
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

              {/* Adresse L2 */}
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

              {/* Code postal */}
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

              {/* Ville */}
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

              {/* Pays */}
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

              {/* Niveau d'accessibilité (select FACILE / MOYENNE / DIFFICILE) */}
              <div className="ecole-modal-field">
                <span className="label">Niveau d&apos;accessibilité</span>
                <select
                  name="niveauAccessibilite"
                  className="ecole-modal-input"
                  value={form.niveauAccessibilite || ""}
                  onChange={handleChange}
                >
                  <option value="">Non renseigné</option>
                  <option value="FACILE">Facile</option>
                  <option value="MOYENNE">Moyenne</option>
                  <option value="DIFFICILE">Difficile</option>
                </select>
              </div>

              {/* Infos d'accès */}
              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Infos accès / remarques</span>
                <textarea
                  name="infosAcces"
                  className="ecole-modal-textarea"
                  value={form.infosAcces}
                  onChange={handleChange}
                  rows={3}
                />
              </div>
            </div>
          </section>

          {/* === Compte référent (éditable maintenant) === */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Compte référent (utilisateur ECOLE)
            </h3>

            <div className="ecole-modal-grid">
              {/* Prénom référent */}
              <div className="ecole-modal-field">
                <span className="label">Prénom</span>
                <input
                  type="text"
                  name="prenomReferent"
                  className="ecole-modal-input"
                  value={form.prenomReferent}
                  onChange={handleChange}
                />
              </div>

              {/* Nom référent */}
              <div className="ecole-modal-field">
                <span className="label">Nom</span>
                <input
                  type="text"
                  name="nomReferent"
                  className="ecole-modal-input"
                  value={form.nomReferent}
                  onChange={handleChange}
                />
              </div>

              {/* Email référent */}
              <div className="ecole-modal-field">
                <span className="label">Email</span>
                <input
                  type="email"
                  name="emailReferent"
                  className="ecole-modal-input"
                  value={form.emailReferent}
                  onChange={handleChange}
                />
              </div>

              {/* Téléphone référent */}
              <div className="ecole-modal-field">
                <span className="label">Téléphone</span>
                <input
                  type="text"
                  name="telephoneReferent"
                  className="ecole-modal-input"
                  value={form.telephoneReferent}
                  onChange={handleChange}
                />
              </div>
            </div>
          </section>

          {/* Message d'erreur éventuel */}
          {error && <div className="ecole-modal-error">{error}</div>}

          {/* FOOTER BOUTONS */}
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
