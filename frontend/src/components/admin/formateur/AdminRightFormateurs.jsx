// src/components/admin/formateur/AdminRightFormateurs.jsx
import { useEffect, useState } from "react";
import axiosClient from "@/api/axiosClient";
import NeumoButton from "@/components/bouton/NeumoButton";

import AdminFormateurCreateModal from "@/modal/formateur/AdminFormateurCreateModal";
import AdminFormateurEditModal from "@/modal/formateur/AdminFormateurEditModal";
import AdminFormateurDeleteModal from "@/modal/formateur/AdminFormateurDeleteModal";
import AdminFormateurDetailModal from "@/modal/formateur/AdminFormateurDetailModal";

/**
 * Panneau droit "Gestion des formateurs" côté ADMIN.
 *
 * Routes backend :
 *  - GET    /api/admin/formateurs?search=&page=&size=
 *  - GET    /api/admin/formateurs/{idFormateur}
 *  - POST   /api/admin/formateurs
 *  - PUT    /api/admin/formateurs/{idFormateur}
 *  - DELETE /api/admin/formateurs/{idFormateur}
 */
export default function AdminRightFormateurs() {
  // ==========================
  // STATE PRINCIPAL
  // ==========================

  const [pageData, setPageData] = useState({
    items: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false,
  });

  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const size = 10;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // ==========================
  // MODALES
  // ==========================

  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);

  const [selectedFormateur, setSelectedFormateur] = useState(null);

  // ==========================
  // HELPERS
  // ==========================

  const formatBoolean = (value) => {
    if (value === true) return "Oui";
    if (value === false) return "Non";
    return "—";
  };

  const getVilleFromItem = (item) => {
    return item.ville || "—";
  };

  const getNomFromItem = (item) => {
    const prenom = item.prenom || "";
    const nom = item.nom || "";
    const full = `${prenom} ${nom}`.trim();
    return full || "—";
  };

  // ==========================
  // API : liste des formateurs
  // ==========================

  /**
   * Charge la page `pageToLoad` en tenant compte d'un éventuel `searchValue`.
   * - Si `searchValue` est fourni, on l'utilise (cas du onChange).
   * - Sinon, on prend la valeur du state `search`.
   */
  const fetchFormateurs = async (pageToLoad = 0, searchValue = null) => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        page: pageToLoad,
        size,
      };

      const effectiveSearch =
        searchValue !== null ? searchValue : search;

      if (effectiveSearch && effectiveSearch.trim() !== "") {
        params.search = effectiveSearch.trim();
      }

      const res = await axiosClient.get("/admin/formateurs", { params });
      const data = res.data || {};

      const currentPage = data.page ?? pageToLoad;
      const totalPages = data.totalPages ?? 0;

      setPageData({
        items: data.items || [],
        page: currentPage,
        size: data.size ?? size,
        totalElements: data.totalElements ?? 0,
        totalPages,
        hasNext: currentPage < totalPages - 1,
        hasPrevious: currentPage > 0,
      });

      setPage(currentPage);
    } catch (err) {
      console.error("Erreur chargement formateurs (admin)", err);
      setError("Impossible de charger la liste des formateurs.");
    } finally {
      setLoading(false);
    }
  };

  // Chargement initial
  useEffect(() => {
    fetchFormateurs(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ==========================
  // RECHERCHE (AUTO)
  // ==========================

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);

    // 🔎 Recherche auto : à chaque changement on relance sur la page 0
    fetchFormateurs(0, value);
  };

  // ==========================
  // PAGINATION
  // ==========================

  const handlePrevPage = () => {
    if (loading) return;
    if (!pageData.hasPrevious) return;
    const newPage = Math.max(0, page - 1);
    fetchFormateurs(newPage);
  };

  const handleNextPage = () => {
    if (loading) return;
    if (!pageData.hasNext) return;
    const newPage = page + 1;
    fetchFormateurs(newPage);
  };

  // ==========================
  // OUVERTURE DES MODALES
  // ==========================

  const openCreateModal = () => {
    setSelectedFormateur(null);
    setCreateModalOpen(true);
  };

  const openEditModal = (item) => {
    setSelectedFormateur(item);
    setEditModalOpen(true);
  };

  const openDeleteModal = (item) => {
    setSelectedFormateur(item);
    setDeleteModalOpen(true);
  };

  const openDetailModal = (item) => {
    setSelectedFormateur(item);
    setDetailModalOpen(true);
  };

  const closeAllModals = () => {
    setCreateModalOpen(false);
    setEditModalOpen(false);
    setDeleteModalOpen(false);
    setDetailModalOpen(false);
    setSelectedFormateur(null);
  };

  const handleAfterMutate = () => {
    closeAllModals();
    // On recharge la page courante avec le même search
    fetchFormateurs(page);
  };

  // ==========================
  // PLANÈTE : ouvrir l’adresse du formateur
  // ==========================

  const openInMaps = async (item) => {
    if (!item?.idFormateur) return;

    try {
      const res = await axiosClient.get(`/admin/formateurs/${item.idFormateur}`);
      const detail = res.data;

      if (detail.lat != null && detail.lon != null) {
        const url = `https://www.google.com/maps/search/?api=1&query=${detail.lat},${detail.lon}`;
        window.open(url, "_blank", "noopener,noreferrer");
        return;
      }

      const parts = [];
      if (detail.adresseL1) parts.push(detail.adresseL1);
      if (detail.codePostal) parts.push(detail.codePostal);
      if (detail.ville) parts.push(detail.ville);
      parts.push(detail.paysCode || "France");

      const query = encodeURIComponent(parts.join(" "));
      const url = `https://www.google.com/maps/search/?api=1&query=${query}`;
      window.open(url, "_blank", "noopener,noreferrer");
    } catch (err) {
      console.error("Erreur ouverture Maps pour formateur", err);
    }
  };

  // ==========================
  // RENDU
  // ==========================

  return (
    <section className="admin-right-ecoles">
      {/* HEADER : titre + recherche + bouton ajout */}
      <div className="admin-right-ecoles-header">
        <h2 className="admin-right-ecoles-title">GESTION DES FORMATEURS</h2>

        <div className="admin-right-ecoles-top-actions">
          {/* Barre de recherche pleine largeur (comme ÉCOLES) */}
          <input
            type="text"
            className="admin-ecoles-search"
            placeholder="recherche : nom / ville / email / commentaire"
            value={search}
            onChange={handleSearchChange}
          />

          <NeumoButton variant="success" onClick={openCreateModal}>
            + Ajouter un formateur
          </NeumoButton>
        </div>
      </div>

      {/* TABLEAU LISTE FORMATEURS */}
      <div className="admin-right-ecoles-table-wrapper">
        {loading && <div className="admin-ecoles-status">Chargement...</div>}

        {error && <div className="admin-ecoles-status error">{error}</div>}

        {!loading && !error && (
          <>
            <table className="admin-right-ecoles-table">
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Commentaire formateur</th>
                  <th>Ville</th>
                  <th>Permis</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {(!pageData.items || pageData.items.length === 0) ? (
                  <tr>
                    <td colSpan={5} className="admin-ecoles-empty">
                      Aucun formateur trouvé.
                    </td>
                  </tr>
                ) : (
                  pageData.items.map((item) => (
                    <tr key={item.idFormateur} className="admin-ecoles-row">
                      {/* NOM */}
                      <td>{getNomFromItem(item)}</td>

                      {/* COMMENTAIRE */}
                      <td>
                        <div
                          className="om-comment-cell"
                          style={{
                            maxHeight: "80px",
                            overflowY: "auto",
                            paddingRight: "4px",
                          }}
                        >
                          {item.commentaire && item.commentaire.trim() !== ""
                            ? item.commentaire
                            : "—"}
                        </div>
                      </td>

                      {/* VILLE */}
                      <td>{getVilleFromItem(item)}</td>

                      {/* PERMIS */}
                      <td>{formatBoolean(item.permis)}</td>

                      {/* ACTIONS */}
                      <td className="admin-ecoles-actions">
                        <button
                          type="button"
                          className="icon-btn"
                          title="Voir le détail du formateur"
                          onClick={() => openDetailModal(item)}
                        >
                          👁
                        </button>

                        <button
                          type="button"
                          className="icon-btn"
                          title="Modifier le formateur"
                          onClick={() => openEditModal(item)}
                        >
                          ✏️
                        </button>

                        <button
                          type="button"
                          className="icon-btn"
                          title="Ouvrir la localisation du formateur"
                          onClick={() => openInMaps(item)}
                        >
                          🌍
                        </button>

                        <button
                          type="button"
                          className="icon-btn icon-danger"
                          title="Désactiver le formateur"
                          onClick={() => openDeleteModal(item)}
                        >
                          ✖
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </>
        )}
      </div>

      {/* PAGINATION */}
      <div className="admin-right-ecoles-pagination">
        <button
          className="icon-btn"
          type="button"
          onClick={handlePrevPage}
          disabled={loading || !pageData.hasPrevious}
        >
          ◀
        </button>

        <span className="admin-right-ecoles-page-info">
          {pageData.totalPages > 0
            ? `${pageData.page + 1} / ${pageData.totalPages}`
            : "0 / 0"}
        </span>

        <button
          className="icon-btn"
          type="button"
          onClick={handleNextPage}
          disabled={loading || !pageData.hasNext}
        >
          ▶
        </button>
      </div>

      {/* MODALES */}
      <AdminFormateurCreateModal
        isOpen={createModalOpen}
        onClose={closeAllModals}
        onSaved={handleAfterMutate}
      />

      <AdminFormateurEditModal
        isOpen={editModalOpen}
        formateur={selectedFormateur}
        onClose={closeAllModals}
        onSaved={handleAfterMutate}
      />

      <AdminFormateurDetailModal
        isOpen={detailModalOpen}
        formateur={selectedFormateur}
        onClose={closeAllModals}
      />

      <AdminFormateurDeleteModal
        isOpen={deleteModalOpen}
        formateur={selectedFormateur}
        onClose={closeAllModals}
        onDeleted={handleAfterMutate}
      />
    </section>
  );
}
