// Fungsi untuk mengambil CSRF Token dari Meta Tags
function getCsrfTokens() {
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");
  return { csrfToken, csrfHeader };
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
