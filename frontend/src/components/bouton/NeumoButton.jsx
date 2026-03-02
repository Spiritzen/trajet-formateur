// src/components/bouton/NeumoButton.jsx

export default function NeumoButton({
  children,
  type = "button",
  onClick,
  variant = "default", // default | success | danger
  size = "md", // sm | md | lg
  className = "",
  ...rest
}) {
  const classes = [
    "neumo-btn",
    `neumo-btn-${variant}`,
    `neumo-btn-${size}`,
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button type={type} className={classes} onClick={onClick} {...rest}>
      {children}
    </button>
  );
}
