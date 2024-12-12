document.addEventListener("DOMContentLoaded", () => {
  const popup = document.getElementById("popup-discount");
  const closeButton = document.getElementById("close-popup");

  // Cek apakah popup sudah pernah ditampilkan sebelumnya
  const popupShown = localStorage.getItem("popupShown");

  if (!popupShown) {
    // Tampilkan popup setelah 3 detik
    setTimeout(() => {
      popup.style.display = "flex";
      // Tandai bahwa popup sudah ditampilkan
      localStorage.setItem("popupShown", "true");
    }, 2000);
  }

  // Sembunyikan popup saat tombol "Tutup" diklik
  closeButton.addEventListener("click", () => {
    popup.style.display = "none";
  });
});
