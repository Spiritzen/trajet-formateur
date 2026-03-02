// src/modal/formateur/AdminFormateurDetailModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleDetailModal.css";

export default function AdminFormateurDetailModal({
  isOpen,
  formateur,
  onClose,
}) {
  const [detail, setDetail] = useState(null); // AdminFormateurDetailResponse
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && formateur?.idFormateur != null) {
      const loadDetail = async () => {
        setLoading(true);
        setError(null);
        try {
          const res = await axiosClient.get(
            `/admin/formateurs/${formateur.idFormateur}`
          );
          setDetail(res.data);
        } catch (err) {
          console.error("Erreur chargement détail formateur", err);
          const msg =
            err?.response?.data?.message ||
            err?.response?.data?.error ||
            "Impossible de charger le détail du formateur.";
          setError(msg);
        } finally {
          setLoading(false);
        }
      };

      loadDetail();
    } else if (!isOpen) {
      setDetail(null);
      setError(null);
    }
  }, [isOpen, formateur]);

  if (!isOpen) return null;
  if (!formateur?.idFormateur) return null;

  const stopClick = (e) => e.stopPropagation();

  const formatBool = (v) =>
    v === true ? "Oui" : v === false ? "Non" : "—";

  const formatDateTime = (value) => {
    if (!value) return "—";
    try {
      return new Date(value).toLocaleString("fr-FR");
    } catch {
      return String(value);
    }
  };

  const d = detail;

  return (
    <div className="ecole-modal-backdrop" onClick={onClose}>
      <div className="ecole-modal-card" onClick={stopClick}>
        <div className="ecole-modal-header">
          <h2 className="ecole-modal-title">Détail formateur</h2>
          <p className="ecole-modal-subtitle">
            Vue complète : compte utilisateur + fiche formateur + géocodage.
          </p>
        </div>

        <div className="ecole-modal-body">
          {loading && <p>Chargement des informations...</p>}
          {error && (
            <p
              style={{
                padding: "8px 10px",
                borderRadius: "10px",
                background: "#fee2e2",
                color: "#7f1d1d",
                fontSize: "0.85rem",
              }}
            >
              {error}
            </p>
          )}

          {!loading && !error && d && (
            <>
              {/* SECTION UTILISATEUR */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">Compte utilisateur</h3>
                <div className="ecole-modal-grid">
                  <div className="ecole-modal-field">
                    <span className="label">Prénom</span>
                    <div className="value">{d.prenom || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Nom</span>
                    <div className="value">{d.nom || "—"}</div>
                  </div>
                  <div className="ecole-modal-field ecole-modal-field-full">
                    <span className="label">Email (login)</span>
                    <div className="value">{d.email || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Téléphone</span>
                    <div className="value">{d.telephone || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Compte actif</span>
                    <div className="value">{formatBool(d.actifUser)}</div>
                  </div>
                </div>
              </div>

              {/* SECTION ADRESSE + GEO */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">Adresse & géocodage</h3>
                <div className="ecole-modal-grid">
                  <div className="ecole-modal-field ecole-modal-field-full">
                    <span className="label">Adresse ligne 1</span>
                    <div className="value">{d.adresseL1 || "—"}</div>
                  </div>
                  <div className="ecole-modal-field ecole-modal-field-full">
                    <span className="label">Adresse ligne 2</span>
                    <div className="value">{d.adresseL2 || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Code postal</span>
                    <div className="value">{d.codePostal || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Ville</span>
                    <div className="value">{d.ville || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Pays</span>
                    <div className="value">{d.paysCode || "—"}</div>
                  </div>

                  <div className="ecole-modal-field">
                    <span className="label">Latitude</span>
                    <div className="value">
                      {d.lat != null ? String(d.lat) : "—"}
                    </div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Longitude</span>
                    <div className="value">
                      {d.lon != null ? String(d.lon) : "—"}
                    </div>
                  </div>
                </div>
              </div>

              {/* SECTION FORMATEUR */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">Fiche formateur</h3>
                <div className="ecole-modal-grid">
                  <div className="ecole-modal-field ecole-modal-field-full">
                    <span className="label">Nom d&apos;affichage</span>
                    <div className="value">{d.nomAffichage || "—"}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Zone KM</span>
                    <div className="value">
                      {d.zoneKm != null ? `${d.zoneKm} km` : "—"}
                    </div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Permis B</span>
                    <div className="value">{formatBool(d.permis)}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Véhicule personnel</span>
                    <div className="value">{formatBool(d.vehiculePerso)}</div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Actif (fiche formateur)</span>
                    <div className="value">{formatBool(d.actifFormateur)}</div>
                  </div>

                  <div className="ecole-modal-field ecole-modal-field-full">
                    <span className="label">Commentaire formateur</span>
                    <div className="value">
                      {d.commentaire && d.commentaire.trim() !== ""
                        ? d.commentaire
                        : "—"}
                    </div>
                  </div>
                </div>
              </div>

              {/* SECTION MÉTA */}
              <div className="ecole-modal-section">
                <h3 className="ecole-modal-section-title">Meta & historique</h3>
                <div className="ecole-modal-grid">
                  <div className="ecole-modal-field">
                    <span className="label">Création compte</span>
                    <div className="value">
                      {formatDateTime(d.createdAtUser)}
                    </div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Dernière mise à jour compte</span>
                    <div className="value">
                      {formatDateTime(d.updatedAtUser)}
                    </div>
                  </div>

                  <div className="ecole-modal-field">
                    <span className="label">Création fiche formateur</span>
                    <div className="value">
                      {formatDateTime(d.createdAtFormateur)}
                    </div>
                  </div>
                  <div className="ecole-modal-field">
                    <span className="label">Dernière mise à jour fiche</span>
                    <div className="value">
                      {formatDateTime(d.updatedAtFormateur)}
                    </div>
                  </div>

                  <div className="ecole-modal-field">
                    <span className="label">Date de désactivation</span>
                    <div className="value">
                      {formatDateTime(d.deletedAtFormateur)}
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>

        <div className="ecole-modal-footer">
          <button
            type="button"
            className="neumo-btn neumo-btn-default"
            onClick={onClose}
          >
            Fermer
          </button>
        </div>
      </div>
    </div>
  );
}
