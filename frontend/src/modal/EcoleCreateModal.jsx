// src/modal/EcoleCreateModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
// On réutilise le même style que pour l'édition
import "@/css/modal/EcoleEditModal.css";

/**
 * Modale de création d'une nouvelle école pour l'ADMIN.
 *
 * Props :
 *  - isOpen   : bool → indique si la modale est visible
 *  - onClose  : fonction → fermer la modale sans créer
 *  - onCreated: fonction → callback après succès de la création
 *               (ex : recharger la liste + fermer)
 */
export default function EcoleCreateModal({ isOpen, onClose, onCreated }) {
  // 🔧 Formulaire de création (école + référent)
  const [form, setForm] = useState({
    // Référent
    email: "",
    prenom: "",
    nom: "",
    telephone: "",

    // École
    nomEcole: "",
    adresseL1: "",
    adresseL2: "",
    codePostal: "",
    ville: "",
    paysCode: "FR",              // valeur par défaut
    niveauAccessibilite: "MOYENNE", // FACILE / MOYENNE / DIFFICILE
    infosAcces: "",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // À chaque ouverture, on réinitialise proprement le formulaire
  useEffect(() => {
    if (isOpen) {
      setForm({
        email: "",
        prenom: "",
        nom: "",
        telephone: "",
        nomEcole: "",
        adresseL1: "",
        adresseL2: "",
        codePostal: "",
        ville: "",
        paysCode: "FR",
        niveauAccessibilite: "MOYENNE",
        infosAcces: "",
      });
      setError(null);
    }
  }, [isOpen]);

  // Fermeture sur ESC
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

  // Clic sur le fond (backdrop) → fermeture
  function handleBackdropMouseDown(event) {
    if (event.target.classList.contains("ecole-modal-backdrop")) {
      onClose();
    }
  }

  // Gestion des champs du formulaire
  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  // Soumission → appel POST /api/admin/ecoles
  async function handleSubmit(e) {
    e.preventDefault();

    setSaving(true);
    setError(null);

    try {
      // Payload attendu par EcoleCreateRequest (back)
      const payload = {
        // --- Référent ---
        email: form.email,
        prenom: form.prenom,
        nom: form.nom,
        telephone: form.telephone,

        // Mot de passe initial FORCÉ côté back
        motDePasse: "Ecole123!",

        // --- École ---
        nomEcole: form.nomEcole,
        adresseL1: form.adresseL1,
        adresseL2: form.adresseL2 || null,
        codePostal: form.codePostal,
        ville: form.ville,
        paysCode: form.paysCode,

        // lat / lon laisses à null : ils seront calculés par le GeocodingService
        lat: null,
        lon: null,

        niveauAccessibilite: form.niveauAccessibilite || "MOYENNE",
        infosAcces: form.infosAcces || null,
      };

      await axiosClient.post("/admin/ecoles", payload);

      // Si tout s'est bien passé : on prévient le parent
      if (onCreated) {
        onCreated();
      }

    } catch (err) {
      console.error("Erreur lors de la création de l'école", err);
      const message =
        err.response?.data?.message ||
        "Impossible de créer l'école. Vérifie les champs ou réessaie plus tard.";
      setError(message);
    } finally {
      setSaving(false);
    }
  }

  // Si la modale n'est pas ouverte → on ne rend rien
  if (!isOpen) {
    return null;
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
          <h2 className="ecole-modal-title">Nouvelle école</h2>
          <p className="ecole-modal-subtitle">
            Création d&apos;une école et de son compte référent
          </p>
        </header>

        {/* FORMULAIRE SCROLLABLE */}
        <form className="ecole-modal-body" onSubmit={handleSubmit}>
          {/* Bloc ÉCOLE */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Informations établissement
            </h3>

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

              <div className="ecole-modal-field">
                <span className="label">Niveau d&apos;accessibilité</span>
                <select
                  name="niveauAccessibilite"
                  className="ecole-modal-input"
                  value={form.niveauAccessibilite}
                  onChange={handleChange}
                >
                  <option value="FACILE">FACILE</option>
                  <option value="MOYENNE">MOYENNE</option>
                  <option value="DIFFICILE">DIFFICILE</option>
                </select>
              </div>

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

          {/* Bloc RÉFÉRENT */}
          <section className="ecole-modal-section">
            <h3 className="ecole-modal-section-title">
              Compte référent (utilisateur ECOLE)
            </h3>

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
                <span className="label">Email</span>
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

              <div className="ecole-modal-field ecole-modal-field-full">
                <span className="label">Mot de passe initial</span>
                <div className="value">
                  <strong>Ecole123!</strong>{" "}
                  <span style={{ fontSize: "0.8rem", marginLeft: "6px" }}>
                    (imposé côté système, l&apos;école pourra le changer plus tard)
                  </span>
                </div>
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
              {saving ? "Création..." : "✅ Créer"}
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
