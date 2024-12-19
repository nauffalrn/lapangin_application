/**
 * Mengambil nilai cookie berdasarkan nama
 * @param {string} name Nama cookie
 * @returns {string|null} Nilai cookie atau null jika tidak ditemukan
 */
function getCookie(name) {
  const nameEQ = name + "=";
  const ca = document.cookie.split(";");
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i].trim();
    if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
  }
  return null;
}

document.addEventListener("DOMContentLoaded", () => {
  console.log("promo.js telah dimuat."); // Debugging

  const urlParams = new URLSearchParams(window.location.search);
  const showPromo = urlParams.get("promo") === "true";
  console.log("showPromo:", showPromo); // Debugging

  const popup = document.getElementById("popup-discount");
  const closeButton = document.getElementById("close-popup");
  const claimButton = document.getElementById("claim-promo");

  // Ambil token CSRF dari meta tag
  const csrfTokenElement = document.querySelector('meta[name="_csrf"]');
  const csrfHeaderElement = document.querySelector('meta[name="_csrf_header"]');

  const csrfToken = csrfTokenElement
    ? csrfTokenElement.getAttribute("content")
    : null;
  const csrfHeader = csrfHeaderElement
    ? csrfHeaderElement.getAttribute("content")
    : null;

  console.log("CSRF Token:", csrfToken); // Debugging
  console.log("CSRF Header:", csrfHeader); // Debugging

  if (showPromo && popup && closeButton && claimButton) {
    console.log("Menampilkan popup promo."); // Debugging
    popup.style.display = "flex";

    closeButton.addEventListener("click", () => {
      popup.style.display = "none";
      console.log("Popup ditutup."); // Debugging
    });

    claimButton.addEventListener("click", () => {
      if (!csrfToken || !csrfHeader) {
        alert("Token CSRF tidak ditemukan. Silakan muat ulang halaman.");
        console.warn("CSRF Token atau Header tidak ditemukan.");
        return;
      }

      const kodePromo = "Lapangin2024"; // Tetapkan kode promo yang benar
      console.log(
        "Mengirim permintaan klaim promo dengan kodePromo:", kodePromo
      );

      fetch("/booking/claim", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken, // Sertakan token CSRF
        },
        body: JSON.stringify({ kodePromo }), // Gunakan kodePromo yang benar
        credentials: "same-origin", // Sertakan kredensial
      })
        .then((response) => {
          console.log("Response Status:", response.status); // Debugging
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then((data) => {
          console.log("Response Data:", data); // Debugging
          if (data.success) {
            alert("Promo berhasil diklaim!");
            popup.style.display = "none";
          } else {
            alert(data.message || "Gagal mengklaim promo.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("Terjadi kesalahan saat mengklaim promo.");
        });
    });
  }
});
