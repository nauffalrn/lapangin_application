document.addEventListener("DOMContentLoaded", () => {
  fetchHistory();
});

/**
 * Mengambil history booking dari backend dan menampilkannya.
 */
function fetchHistory() {
  fetch("/api/booking/history")
    .then(response => response.json())
    .then(data => {
      const historyContent = document.getElementById("historyContent");
      if (data.length === 0) {
        historyContent.innerHTML = '<div class="empty-state"><p>Anda belum memiliki history booking.</p></div>';
        return;
      }

      const now = new Date();
      let table = `
        <table class="table table-bordered table-striped table-hover">
          <thead class="thead-dark bg-primary text-white">
            <tr>
              <th>No</th>
              <th>Nama Lapangan</th>
              <th>Lokasi</th>
              <th>Tanggal Booking</th>
              <th>Waktu</th>
              <th>Rating & Review</th>
            </tr>
          </thead>
          <tbody>
      `;

      data.forEach((booking, index) => {
        const bookingEndTime = new Date(booking.bookingDate);
        bookingEndTime.setHours(booking.jamSelesai, 0, 0, 0);

        if (now > bookingEndTime) {
          const hasReview = booking.review !== null;
          table += `
            <tr>
              <td>${index + 1}</td>
              <td>${booking.lapangan.namaLapangan}</td>
              <td>${booking.lapangan.alamatLapangan}</td>
              <td>${formatDateTime(booking.bookingDate)}</td>
              <td>${booking.jamMulai}:00 - ${booking.jamSelesai}:00</td>
              <td>
                ${
                  hasReview
                    ? `
                      <span>⭐ ${booking.review.rating}</span>
                      <p>${booking.review.komentar}</p>
                      <small>Oleh: ${booking.review.username} pada ${formatDateTime(
                        booking.review.tanggalReview
                      )}</small>
                    `
                    : `
                      <button class="btn btn-primary btn-sm" onclick="showReviewForm(${booking.id})">Berikan Review</button>
                      <div id="reviewForm-${booking.id}" class="mt-2" style="display:none;">
                        <form onsubmit="submitReview(event, ${booking.id})">
                          <div class="mb-3">
                            <label for="rating-${booking.id}" class="form-label">Rating (0-5)</label>
                            <input type="number" min="0" max="5" class="form-control" id="rating-${booking.id}" required>
                          </div>
                          <div class="mb-3">
                            <label for="komentar-${booking.id}" class="form-label">Komentar</label>
                            <textarea class="form-control" id="komentar-${booking.id}" rows="3" required></textarea>
                          </div>
                          <button type="submit" class="btn btn-success btn-sm">Submit</button>
                        </form>
                      </div>
                    `
                }
              </td>
            </tr>
          `;
        }
      });

      table += `
          </tbody>
        </table>
      `;
      historyContent.innerHTML = table;
    })
    .catch(error => {
      console.error("Error fetching history:", error);
      alert(`Gagal mengambil history: ${error.message}`);
    });
}

/**
 * Menampilkan form review untuk booking tertentu.
 *
 * @param {number} bookingId ID Booking
 */
function showReviewForm(bookingId) {
  const form = document.getElementById(`reviewForm-${bookingId}`);
  form.style.display = form.style.display === "none" ? "block" : "none";
}

/**
 * Mengirim review ke backend.
 *
 * @param {Event} event Event submit form
 * @param {number} bookingId ID Booking
 */
function submitReview(event, bookingId) {
  event.preventDefault();
  const rating = document.getElementById(`rating-${bookingId}`).value;
  const komentar = document
    .getElementById(`komentar-${bookingId}`)
    .value.trim();

  if (rating === "" || komentar === "") {
    alert("Rating dan komentar harus diisi.");
    return;
  }

  fetch("/api/booking/review", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "X-Requested-With": "XMLHttpRequest",
      [getCsrfHeader()]: getCsrfToken(),
    },
    body: `bookingId=${bookingId}&rating=${rating}&komentar=${encodeURIComponent(
      komentar
    )}`,
  })
    .then((response) => {
      if (response.ok) {
        alert("Review berhasil ditambahkan.");
        fetchHistory();
      } else {
        return response.text().then((text) => {
          throw new Error(text);
        });
      }
    })
    .catch((error) => {
      console.error("Error submitting review:", error);
      alert(`Gagal menambahkan review: ${error.message}`);
    });
}

/**
 * Helper untuk mendapatkan CSRF token.
 *
 * @return {string} CSRF Token
 */
function getCsrfToken() {
  const token = document.querySelector('meta[name="_csrf"]').getAttribute("content");
  return token;
}

/**
 * Helper untuk mendapatkan nama header CSRF.
 *
 * @return {string} CSRF Header Name
 */
function getCsrfHeader() {
  const header = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
  return header;
}

/**
 * Helper untuk format tanggal dan waktu.
 *
 * @param {string} dateTimeStr String tanggal dan waktu
 * @return {string} Tanggal dan waktu yang diformat
 */
function formatDateTime(dateTimeStr) {
  const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute:'2-digit' };
  return new Date(dateTimeStr).toLocaleDateString(undefined, options);
}

/**
 * Helper untuk format tanggal.
 *
 * @param {string} dateStr String tanggal
 * @return {string} Tanggal yang diformat
 */
function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString();
}
