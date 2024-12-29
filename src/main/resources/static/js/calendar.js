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

    monthYear.textContent = `${date.toLocaleString("default", {
      month: "long",
    })} ${year}`;

    // Menambahkan sel kosong sebelum hari pertama
    for (let i = 0; i < firstDay; i++) {
      const emptyCell = document.createElement("div");
      emptyCell.classList.add("calendar-day", "empty");
      calendarDays.appendChild(emptyCell);
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Set waktu ke tengah malam untuk perbandingan yang akurat

    for (let day = 1; day <= daysInMonth; day++) {
      const dayCell = document.createElement("div");
      dayCell.classList.add("calendar-day");
      dayCell.textContent = day;

      const dateObj = new Date(year, month, day);
      const dateString = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;

      // Tandai hari ini
      if (
        day === today.getDate() &&
        month === today.getMonth() &&
        year === today.getFullYear()
      ) {
        dayCell.classList.add("today");
      }

      const isPast = dateObj < today;

      if (!isPast) {
        // Fetch bookings untuk tanggal ini
        fetch(`/api/booking/bookings?tanggal=${dateString}`)
          .then((response) => {
            if (!response.ok) {
              throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
          })
          .then((bookings) => {
            console.log(`Bookings for ${dateString}:`, bookings); // Debugging
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
      } else {
        // Nonaktifkan interaksi untuk tanggal yang sudah lewat
        dayCell.classList.add("disabled");
        dayCell.style.pointerEvents = "none";
        dayCell.style.opacity = "0.5";
      }

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

// Fungsi helper untuk memformat jam
function formatJam(jam) {
  return jam; // Sudah dalam format "HH:00"
}

// Fungsi untuk menampilkan detail booking
function showBookingDetails(bookings) {
  const bookingModal = document.getElementById("bookingModal");
  const bookingDetails = document.getElementById("bookingDetails");
  bookingDetails.innerHTML = "";

  bookings.forEach((booking) => {
    const detail = document.createElement("div");
    detail.classList.add("booking-detail");
    detail.innerHTML = `
      <p><strong>Lapangan:</strong> ${booking.lapangan}</p>
      <p><strong>Jam:</strong> ${formatJam(
        booking.jamMulaiFormatted
      )} - ${formatJam(booking.jamSelesaiFormatted)}</p>
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
