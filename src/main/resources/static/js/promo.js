document.addEventListener("DOMContentLoaded", () => {
  const popup = document.getElementById("popup-discount");
  const closeButton = document.getElementById("close-popup");

  if (popup && closeButton) {
    const popupShown = localStorage.getItem("popupShown");

    if (!popupShown) {
      // Tampilkan popup setelah 2 detik
      setTimeout(() => {
        popup.style.display = "flex";
        localStorage.setItem("popupShown", "true");
      }, 2000);
    }

    // Sembunyikan popup saat tombol "Tutup" diklik
    closeButton.addEventListener("click", () => {
      popup.style.display = "none";
    });
  }
});
