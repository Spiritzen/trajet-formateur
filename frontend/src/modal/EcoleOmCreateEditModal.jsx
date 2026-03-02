// src/modal/EcoleOmAffectationModal.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import "@/css/modal/EcoleEditModal.css"; // on réutilise le style des modales

export default function EcoleOmAffectationModal({
  isOpen,
  ordreMission,
  onClose,
  onSaved,
}) {
  const [candidats, setCandidats] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // =========================
  // Helpers formatage
  // =========================
  const formatDate = (value) => {
    if (!value) return "—";
    try {
      const d = new Date(value);
      return d.toLocaleDateString("fr-FR");
    } catch {
      return value;
    }
  };

  // On récupère la valeur brute du budget avec plusieurs fallbacks
  const getRawBudget = () => {
    if (!ordreMission) return null;

    // cas camelCase classique (DTO Java -> Jackson)
    if (ordreMission.coutTotalEstime !== undefined) {
      return ordreMission.coutTotalEstime;
    }
    // si jamais le backend renvoie encore du snake_case
    if (ordreMission.cout_total_estime !== undefined) {
      return ordreMission.cout_total_estime;
    }
    // fallback si on a changé de nom un jour
    if (ordreMission.cout !== undefined) {
      return ordreMission.cout;
    }
    return null;
  };

  const formatBudget = () => {
    const raw = getRawBudget();

    if (raw === null || raw === undefined || raw === "") {
      return "— €";
    }

    // raw peut être un nombre (220.0) ou une string ("220.00")
    const num = Number(raw);
    if (!Number.isNaN(num)) {
      return `${num.toFixed(2)} €`;
    }

    // au pire, on affiche brut
    return `${raw} €`;
  };

  // =========================
  // Chargement des candidats
  // =========================
  useEffect(() => {
    if (!isOpen || !ordreMission) return;

    const fetchCandidats = async () => {
      try {
        setLoading(true);
        setError(null);
        setSelectedId(null);

        const res = await axiosClient.get(
          `/ecole/om/${ordreMission.idOrdreMission}/candidats-formateurs`
        );
        setCandidats(res.data || []);
      } catch (err) {
        console.error("Erreur chargement candidats formateurs", err);
        setError(
          "Impossible de charger la liste des formateurs candidats pour cet ordre de mission."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchCandidats();
  }, [isOpen, ordreMission]);

  // =========================
  // Backdrop
  // =========================
  const handleBackdropMouseDown = (e) => {
    if (e.target.classList.contains("ecole-modal-backdrop")) {
      onClose();
    }
  };

  // =========================
  // Affectation
  // =========================
  const handleAffecter = async () => {
    if (!ordreMission || !selectedId) {
      setError("Merci de sélectionner un formateur.");
      return;
    }

    try {
      setSaving(true);
      setError(null);

      await axiosClient.put(
        `/ecole/om/${ordreMission.idOrdreMission}/affectation`,
        { idFormateur: selectedId }
      );

      if (onSaved) {
        onSaved();
      }
    } catch (err) {
      console.error("Erreur lors de l'affectation du formateur", err);
      const message =
        err.response?.data?.message ||
        "Impossible d'affecter ce formateur à l'ordre de mission.";
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  if (!isOpen || !ordreMission) return null;

  const dateDebut = formatDate(ordreMission.dateDebut);
  const dateFin = formatDate(ordreMission.dateFin);
  const budget = formatBudget();

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
            Affecter un formateur – {ordreMission.codeOrdre}
          </h2>

          {/* Récap mission */}
          <p className="ecole-modal-subtitle">
            {ordreMission.commentaire && ordreMission.commentaire.trim() !== ""
              ? ordreMission.commentaire
              : "Ordre de mission sans commentaire spécifique."}
          </p>
          <p className="ecole-modal-subtitle">
            Période : du {dateDebut} au {dateFin} — Budget : {budget}
          </p>

          <p className="ecole-modal-subtitle">
            Statut actuel : {ordreMission.statut}
          </p>
        </header>

        {/* CORPS */}
        <div className="ecole-modal-body">
          {loading && <div className="ecole-modal-error">Chargement...</div>}

          {error && <div className="ecole-modal-error">{error}</div>}

          {!loading && !error && candidats.length === 0 && (
            <div className="ecole-modal-error">
              Aucun formateur éligible trouvé pour cet ordre de mission.
            </div>
          )}

          {!loading && !error && candidats.length > 0 && (
            <table className="admin-right-ecoles-table">
              <thead>
                <tr>
                  <th>Formateur</th>
                  <th>Ville</th>
                  <th>Zone km</th>
                  <th>Distance</th>
                  <th>Commentaire</th>
                  <th>Choix</th>
                </tr>
              </thead>
              <tbody>
                {candidats.map((f) => (
                  <tr key={f.idFormateur} className="admin-ecoles-row">
                    <td>
                      {f.prenom} {f.nom}
                    </td>
                    <td>{f.ville || "—"}</td>
                    <td>{f.zoneKm != null ? `${f.zoneKm} km` : "—"}</td>
                    <td>
                      {f.distanceKm != null
                        ? `${f.distanceKm.toFixed(1)} km`
                        : "—"}
                    </td>
                    <td>
                      <div
                        style={{
                          maxHeight: "80px",
                          overflowY: "auto",
                          paddingRight: "4px",
                        }}
                      >
                        {f.commentaire && f.commentaire.trim() !== ""
                          ? f.commentaire
                          : "—"}
                      </div>
                    </td>
                    <td className="admin-ecoles-actions">
                      <input
                        type="radio"
                        name="formateurChoice"
                        checked={selectedId === f.idFormateur}
                        onChange={() => setSelectedId(f.idFormateur)}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* FOOTER */}
        <footer className="ecole-modal-footer">
          <button
            type="button"
            className="neumo-btn neumo-btn-success"
            onClick={handleAffecter}
            disabled={saving || !selectedId}
          >
            {saving ? "Affectation..." : "✅ Affecter ce formateur"}
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
      </div>
    </div>
  );
}
