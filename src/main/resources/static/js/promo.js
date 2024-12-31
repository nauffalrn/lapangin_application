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

  const popup = document.getElementById("popup-discount");
  const closeButton = document.getElementById("close-popup");
  const claimButton = document.getElementById("claim-promo"); // Tombol statis
  const promoContainer = document.getElementById("promo-container"); // Container dinamis

  let promoToClaim = null; // Variabel untuk menyimpan promo yang akan diklaim

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

  // Fungsi untuk mengklaim promo
  function claimPromo(kodePromo) {
    if (!csrfToken || !csrfHeader) {
      alert("Token CSRF tidak ditemukan. Silakan muat ulang halaman.");
      console.warn("CSRF Token atau Header tidak ditemukan.");
      return;
    }

    console.log("Mengirim permintaan klaim promo dengan kodePromo:", kodePromo);

    fetch("/api/booking/claim", {
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
          // Optional: Reload halaman atau update UI
          location.reload();
        } else {
          alert(data.message || "Gagal mengklaim promo.");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Terjadi kesalahan saat mengklaim promo.");
      });
  }

  // Fetch available promos dari backend
  fetch("/api/booking/promos/available", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "X-Requested-With": "XMLHttpRequest",
    },
    credentials: "same-origin",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((promoList) => {
      if (promoList.length > 0) {
        console.log("Available promos:", promoList); // Debugging

        // Iterasi setiap promo dan tambahkan ke dalam container
        promoList.forEach((promo) => {
          const promoItem = document.createElement("div");
          promoItem.className = "promo-item";

          promoItem.innerHTML = `
            <h3>${promo.kodePromo}</h3>
            <p>Diskon: ${promo.diskonPersen}%</p>
            <p>Periode: ${new Date(promo.tanggalMulai).toLocaleDateString()} - ${new Date(promo.tanggalSelesai).toLocaleDateString()}</p>
          `;

          promoContainer.appendChild(promoItem);
        });

        // Set promoToClaim sebagai promo pertama
        promoToClaim = promoList[0].kodePromo;

        // Tampilkan popup promo
        popup.style.display = "flex";
      } else {
        // Jika tidak ada promo yang tersedia, jangan tampilkan popup
        popup.style.display = "none";
      }
    })
    .catch((error) => {
      console.error("Error fetching promos:", error);
    });

  // Event listener untuk tombol tutup popup
  closeButton.addEventListener("click", () => {
    popup.style.display = "none";
    console.log("Popup ditutup."); // Debugging
  });

  // Event listener untuk tombol klaim promo statis
  claimButton.addEventListener("click", () => {
    if (promoToClaim) {
      claimPromo(promoToClaim);
    } else {
      alert("Tidak ada promo yang tersedia untuk diklaim.");
    }
  });
});
