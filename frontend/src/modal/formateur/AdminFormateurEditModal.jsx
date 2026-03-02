// src/modal/formateur/AdminFormateurEditModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css";
import "@/css/modal/EcoleDetailModal.css";

const ZONE_KM_OPTIONS = [5, 10, 20, 50, 100, 200, 500];

/**
 * Modale d'édition d'un formateur côté ADMIN.
 *
 * Props :
 *  - isOpen : bool
 *  - formateur : item de la liste (AdminFormateurListItemResponse)
 *  - onClose() : fermeture simple
 *  - onSaved() : callback après succès du PUT
 */
export default function AdminFormateurEditModal({
  isOpen,
  formateur,
  onClose,
  onSaved,
}) {
  // Form local
  const [form, setForm] = useState({
    idFormateur: null,
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
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Pré-remplissage du formulaire à l'ouverture,
  // à partir du formateur fourni + éventuellement d'un GET détail.
  useEffect(() => {
    if (!isOpen) return;

    setError(null);
    setSubmitting(false);

    // Si pas de formateur sélectionné, on ne peut pas éditer
    if (!formateur || !formateur.idFormateur) {
      return;
    }

    // Chargement éventuel des détails depuis le backend
    const fetchDetail = async () => {
      try {
        setLoading(true);

        // On appelle le détail pour récupérer l'adresse complète, zoneKm, etc.
        const res = await axiosClient.get(
          `/admin/formateurs/${formateur.idFormateur}`
        );
        const data = res.data || {};

        // On aligne les champs avec AdminFormateurDetailResponse
        setForm({
          idFormateur: data.idFormateur ?? formateur.idFormateur,
          nomAffichage: data.nomAffichage ?? formateur.nomAffichage ?? "",
          prenom: data.prenom ?? formateur.prenom ?? "",
          nom: data.nom ?? formateur.nom ?? "",
          email: data.email ?? formateur.email ?? "",
          telephone: data.telephone ?? formateur.telephone ?? "",
          zoneKm:
            data.zoneKm != null
              ? Number(data.zoneKm)
              : formateur.zoneKm != null
              ? Number(formateur.zoneKm)
              : 20,
          adresseL1: data.adresseL1 ?? "",
          adresseL2: data.adresseL2 ?? "",
          codePostal: data.codePostal ?? "",
          ville: data.ville ?? formateur.ville ?? "",
          paysCode: data.paysCode ?? "FR",
          commentaire: data.commentaire ?? formateur.commentaire ?? "",
          permis:
            typeof data.permis === "boolean"
              ? data.permis
              : !!formateur.permis,
          vehiculePerso:
            typeof data.vehiculePerso === "boolean"
              ? data.vehiculePerso
              : !!formateur.vehiculePerso,
        });
      } catch (err) {
        console.error("Erreur chargement détail formateur (edit)", err);
        setError(
          "Impossible de charger le détail du formateur. Vérifie le backend."
        );

        // En fallback, on pré-remplit juste avec l'item de liste
        setForm((prev) => ({
          ...prev,
          idFormateur: formateur.idFormateur,
          nomAffichage: formateur.nomAffichage ?? "",
          prenom: formateur.prenom ?? "",
          nom: formateur.nom ?? "",
          email: formateur.email ?? "",
          telephone: formateur.telephone ?? "",
          zoneKm:
            formateur.zoneKm != null ? Number(formateur.zoneKm) : prev.zoneKm,
          ville: formateur.ville ?? "",
          commentaire: formateur.commentaire ?? "",
        }));
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [isOpen, formateur]);

  if (!isOpen) return null;

  const stopClick = (e) => e.stopPropagation();

  // Handlers
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: checked,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.idFormateur) return;

    setSubmitting(true);
    setError(null);

    try {
      // Payload aligné avec AdminFormateurUpdateRequest
      const payload = {
        nomAffichage:
          form.nomAffichage || `${form.prenom} ${form.nom}`.trim() || null,
        prenom: form.prenom || null,
        nom: form.nom || null,
        email: form.email || null,
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

      await axiosClient.put(
        `/admin/formateurs/${form.idFormateur}`,
        payload
      );

      if (typeof onSaved === "function") {
        onSaved();
      }
    } catch (err) {
      console.error("Erreur mise à jour formateur", err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Impossible de mettre à jour le formateur.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="ecole-modal-backdrop" onClick={onClose}>
      <div className="ecole-modal-card" onClick={stopClick}>
        {/* HEADER */}
        <div className="ecole-modal-header">
          <h2 className="ecole-modal-title">Modifier le formateur</h2>
          <p className="ecole-modal-subtitle">
            Mise à jour du compte utilisateur et de la fiche formateur.
          </p>
        </div>

        {/* FORM */}
        <form onSubmit={handleSubmit} className="ecole-modal-body">
          {loading ? (
            <div className="admin-ecoles-status">Chargement...</div>
          ) : (
            <>
              {/* IDENTITÉ */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">
                  Identité & compte
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
                    <span className="label">Zone kilométrique</span>
                    <select
                      name="zoneKm"
                      className="ecole-modal-select"
                      value={form.zoneKm}
                      onChange={handleChange}
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
                      <label
                        style={{
                          display: "flex",
                          gap: "6px",
                          alignItems: "center",
                        }}
                      >
                        <input
                          type="checkbox"
                          name="permis"
                          checked={!!form.permis}
                          onChange={handleCheckboxChange}
                        />
                        <span>Permis B</span>
                      </label>

                      <label
                        style={{
                          display: "flex",
                          gap: "6px",
                          alignItems: "center",
                        }}
                      >
                        <input
                          type="checkbox"
                          name="vehiculePerso"
                          checked={!!form.vehiculePerso}
                          onChange={handleCheckboxChange}
                        />
                        <span>Véhicule personnel</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              {/* ADRESSE */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">
                  Adresse (géocodage)
                </h3>
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

              {/* COMMENTAIRE */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">
                  Commentaire formateur
                </h3>
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
            </>
          )}

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
              disabled={submitting || loading}
            >
              {submitting ? "Enregistrement..." : "Enregistrer les modifications"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
