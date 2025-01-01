// Fungsi untuk mengambil CSRF Token dari Meta Tags
function getCsrfTokens() {
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");
  return { csrfToken, csrfHeader };
}

// Fungsi untuk mendapatkan promo yang dimiliki customer
async function getCustomerPromos() {
  const { csrfToken, csrfHeader } = getCsrfTokens();

  try {
    const response = await fetch("/api/booking/promos/customer", {
      method: "GET",
      headers: {
        [csrfHeader]: csrfToken,
        "Content-Type": "application/json",
      },
    });
    const promos = await response.json();
    const promoSelect = document.getElementById("promoSelect");

    promos.forEach(promo => {
      const option = document.createElement("option");
      option.value = promo.kodePromo;
      option.text = `${promo.kodePromo} - Diskon ${promo.diskonPersen}%`;
      promoSelect.appendChild(option);
    });
  } catch (error) {
    console.error("Error fetching customer promos:", error);
  }
}

// Fungsi untuk menerapkan promo
async function applyPromo() {
  const promoCode = document.getElementById("promoSelect").value;
  const bookingId = document.getElementById("cancelBookingButton").dataset.bookingId;
  const { csrfToken, csrfHeader } = getCsrfTokens();

  if (!promoCode) {
    alert("Pilih promo terlebih dahulu.");
    return;
  }

  try {
    const response = await fetch(`/api/booking/apply-promo?bookingId=${bookingId}&kodePromo=${promoCode}`, {
      method: "POST",
      headers: {
        [csrfHeader]: csrfToken,
        "Content-Type": "application/json",
      },
    });
    const result = await response.json();
    if (response.ok) {
      alert("Promo berhasil diterapkan!");
      document.querySelector(".original-price").style.textDecoration = "line-through";
      document.querySelector(".discounted-price").style.display = "inline";
      document.querySelector(".discounted-price").textContent = `Rp. ${result.totalPrice}`;
    } else {
      alert(result.message || "Gagal menerapkan promo.");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Terjadi kesalahan sistem.");
  }
}

// Handle Payment Form Submission
document.getElementById("paymentForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const form = e.target;
  const fileInput = document.getElementById("paymentProof");
  const file = fileInput.files[0];

  // Client-side file size validation (Optional)
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    alert("Ukuran file terlalu besar. Maksimum 5MB.");
    return;
  }

  const formData = new FormData(form);
  const { csrfToken, csrfHeader } = getCsrfTokens();

  try {
    const response = await fetch(form.action, {
      method: "POST",
      headers: {
        [csrfHeader]: csrfToken,
      },
      body: formData,
    });
    const result = await response.json();
    if (result.success) {
      alert("Pembayaran berhasil dikonfirmasi!");
      window.location.href = "/dashboard";
    } else {
      alert(result.message || "Gagal mengupload bukti pembayaran.");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Terjadi kesalahan sistem.");
  }
});

// Handle Cancel Booking
document.getElementById("cancelBookingButton").addEventListener("click", () => {
  const bookingId = document.getElementById("cancelBookingButton").dataset.bookingId;
  if (confirm("Apakah Anda yakin ingin membatalkan booking ini?")) {
    const { csrfToken, csrfHeader } = getCsrfTokens();

    fetch(`/api/booking/cancel/${bookingId}`, {
      method: "DELETE",
      headers: {
        [csrfHeader]: csrfToken,
      },
    })
      .then((response) => {
        if (response.ok) {
          alert("Booking berhasil dibatalkan.");
          window.location.href = "/dashboard";
        } else {
          return response.json().then((data) => {
            throw new Error(data.message || "Gagal membatalkan booking.");
          });
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert(`Gagal membatalkan booking: ${error.message}`);
      });
  }
});

// Panggil fungsi untuk mendapatkan promo yang dimiliki customer saat halaman dimuat
document.addEventListener("DOMContentLoaded", getCustomerPromos);
