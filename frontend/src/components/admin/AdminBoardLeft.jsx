// src/components/admin/AdminBoardLeft.jsx
import { NavLink } from "react-router-dom";

const items = [
  { key: "ecoles", label: "ECOLES", to: "/admin/ecoles" },
  { key: "formateurs", label: "FORMATEURS", to: "/admin/formateurs" },
  { key: "om", label: "OM", to: "/admin/om" },
  { key: "trajets", label: "TRAJETS", to: "/admin/trajets" },
  { key: "compte", label: "COMPTE", to: "/admin/compte" },
  { key: "securite", label: "SECURITE", to: "/admin/securite" },
];

export default function AdminBoardLeft() {
  return (
    <aside className="admin-board-left">
      <h2 className="admin-board-left-title">ADMIN DASH</h2>

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
