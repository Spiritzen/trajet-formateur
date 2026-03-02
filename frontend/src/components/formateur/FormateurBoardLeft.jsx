// src/components/formateur/FormateurBoardLeft.jsx
import { NavLink } from "react-router-dom";

// ⚙️ Items du menu Formateur (dans l'ordre de ta maquette)
const items = [
  // 👉 on pointe vers /formateur, pas /formateur/profil
  { key: "profil",         label: "PROFIL",             to: "/formateur" },
  { key: "mes-om",         label: "MES OM",             to: "/formateur/om" },
  { key: "mes-trajets",    label: "MES TRAJETS",        to: "/formateur/trajets" },
  { key: "justificatifs",  label: "MES JUSTIFICATIFS",  to: "/formateur/justificatifs" },
  { key: "documents",      label: "MES DOCUMENTS",      to: "/formateur/documents" },
  { key: "securite",       label: "SECURITE",           to: "/formateur/securite" },
];

export default function FormateurBoardLeft() {
  return (
    <aside className="admin-board-left">
      <h2 className="admin-board-left-title">ESPACE FORMATEUR</h2>

      <nav className="admin-board-left-nav">
        {items.map((item) => (
          <NavLink
            key={item.key}
            to={item.to}
            className={({ isActive }) =>
              "admin-board-left-link" +
              (isActive ? " admin-board-left-link-active" : "")
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
