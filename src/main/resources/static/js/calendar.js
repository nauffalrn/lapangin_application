document.addEventListener("DOMContentLoaded", function () {
  const calendarDays = document.getElementById("calendar-days");
  const monthYear = document.getElementById("month-year");
  const prevMonthBtn = document.getElementById("prev-month");
  const nextMonthBtn = document.getElementById("next-month");

  let currentDate = new Date();

  function renderCalendar(date) {
    calendarDays.innerHTML = "";
    const year = date.getFullYear();
    const month = date.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    monthYear.textContent = `${date.toLocaleString("default", { month: "long" })} ${year}`;

    // Menambahkan sel kosong sebelum hari pertama
    for (let i = 0; i < firstDay; i++) {
      const emptyCell = document.createElement("div");
      emptyCell.classList.add("calendar-day", "empty");
      calendarDays.appendChild(emptyCell);
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const dayCell = document.createElement("div");
      dayCell.classList.add("calendar-day");
      dayCell.textContent = day;

      const dateObj = new Date(year, month, day);
      const dateString = dateObj.toISOString().split("T")[0];

      // Tandai hari ini
      const today = new Date();
      if (
        day === today.getDate() &&
        month === today.getMonth() &&
        year === today.getFullYear()
      ) {
        dayCell.classList.add("today");
      }

      // Fetch bookings untuk tanggal ini
      fetch(`/api/booking/bookings?tanggal=${dateString}`)
        .then((response) => response.json())
        .then((bookings) => {
          if (bookings.length > 0) {
            dayCell.classList.add("has-booking");
            dayCell.dataset.bookings = JSON.stringify(bookings);
          }
        })
        .catch((error) => console.error("Error fetching bookings:", error));

      // Tambahkan event listener untuk modal
      dayCell.addEventListener("click", () => {
        if (dayCell.classList.contains("has-booking")) {
          const bookings = JSON.parse(dayCell.dataset.bookings || "[]");
          showBookingDetails(bookings);
        }
      });

      calendarDays.appendChild(dayCell);
    }
  }

  prevMonthBtn.addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(currentDate);
  });

  nextMonthBtn.addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(currentDate);
  });

  renderCalendar(currentDate);
});

// Fungsi untuk menampilkan detail booking
function showBookingDetails(bookings) {
  const bookingModal = document.getElementById("bookingModal");
  const bookingDetails = document.getElementById("bookingDetails");
  bookingDetails.innerHTML = "";

  bookings.forEach((booking) => {
    const detail = document.createElement("div");
    detail.classList.add("booking-detail");
    detail.innerHTML = `
      <p><strong>Lapangan:</strong> ${booking.lapangan.namaLapangan}</p>
      <p><strong>Jam Mulai:</strong> ${new Date(booking.waktuMulai).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</p>
      <p><strong>Jam Selesai:</strong> ${new Date(booking.waktuSelesai).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</p>
    `;
    bookingDetails.appendChild(detail);
  });

  bookingModal.classList.remove("hidden");
  bookingModal.style.display = "flex";
}

function closeBookingModal() {
  const bookingModal = document.getElementById("bookingModal");
  bookingModal.classList.add("hidden");
  bookingModal.style.display = "none";
}
