document.addEventListener("DOMContentLoaded", () => {
  initializeDates();
  setupModalHandlers();
  setupTimeSlotAvailability();
  fetchAndDisplayReviews(); // Tambahkan ini
});

let selectedSlots = [];
let totalPrice = 0;

function initializeDates() {
  const dateScroll = document.getElementById("dateScroll");
  if (!dateScroll) return;

  dateScroll.innerHTML = "";
  const days = ["Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"];
  const months = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "Mei",
    "Jun",
    "Jul",
    "Ags",
    "Sep",
    "Okt",
    "Nov",
    "Des",
  ];

  const today = new Date();
  for (let i = 0; i < 7; i++) {
    const nextDate = new Date(today);
    nextDate.setDate(today.getDate() + i);
    const isoDate = nextDate.toISOString().split("T")[0];

    const dateItem = document.createElement("div");
    dateItem.className = "date-item";
    dateItem.dataset.date = isoDate; // Add data-date attribute
    dateItem.innerHTML = `
      <span class="day">${days[nextDate.getDay()]}</span>
      <span class="date">${nextDate.getDate()} ${
      months[nextDate.getMonth()]
    }</span>
    `;
    dateItem.addEventListener("click", () => {
      document
        .querySelectorAll(".date-item")
        .forEach((di) => di.classList.remove("active"));
      dateItem.classList.add("active");
      updateScheduleForDate(isoDate);
      document.getElementById("selectedDate").textContent =
        formatDisplayDate(isoDate);

      // Reset selections when date changes
      resetSelectedSlots();
    });
    dateScroll.appendChild(dateItem);
  }
}

function setupImagePreview() {
  const images = document.querySelectorAll(".detail-image-grid img");
  images.forEach((img) => {
    img.addEventListener("click", function () {
      showImagePreview(this.src);
    });
  });
}

function showImagePreview(imageSrc) {
  const modal = document.createElement("div");
  modal.className = "image-preview-modal";
  modal.innerHTML = `
    <div class="modal-content">
      <span class="close-modal">&times;</span>
      <img src="${imageSrc}" alt="Preview">
    </div>
  `;

  document.body.appendChild(modal);

  // Handler to close modal
  modal.querySelector(".close-modal").onclick = () => modal.remove();
  modal.onclick = (e) => {
    if (e.target === modal) modal.remove();
  };

  // Prevent scrolling when modal is open
  document.body.style.overflow = "hidden";
  modal.addEventListener("click", () => {
    document.body.style.overflow = "";
    modal.remove();
  });

  // Reset selections when modal is closed
  const closeModal = () => {
    resetSelectedSlots();
  };

  modal.querySelector(".close-modal").addEventListener("click", closeModal);
}

function setupModalHandlers() {
  const modal = document.getElementById("scheduleModal");
  const closeBtn = document.querySelector(".detail-close-modal");

  if (modal && closeBtn) {
    // Close when X is clicked
    closeBtn.onclick = () => {
      tutupModal();
      resetSelectedSlots();
    };

    // Close when clicking outside the modal content
    window.onclick = (e) => {
      if (e.target === modal) {
        tutupModal();
        resetSelectedSlots();
      }
    };

    // Close when Escape key is pressed
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && modal.style.display === "flex") {
        tutupModal();
        resetSelectedSlots();
      }
    });
  }
}

function lihatKalender() {
  const modal = document.getElementById("scheduleModal");
  if (modal) {
    modal.style.display = "flex";
    const activeDateItem = document.querySelector(".date-item.active");
    if (activeDateItem) {
      updateScheduleForDate(activeDateItem.dataset.date);
      document.getElementById("selectedDate").textContent = formatDisplayDate(
        activeDateItem.dataset.date
      );
    }
  }
}

function tutupModal() {
  const modal = document.getElementById("scheduleModal");
  if (modal) {
    modal.style.display = "none";
  }
}

function updateScheduleForDate(date) {
  const venueId = getVenueId();
  if (!venueId) {
    console.error("Venue ID tidak ditemukan.");
    return;
  }

  // Mendapatkan detail lapangan
  fetch(`/api/booking/lapangan/${venueId}`)
    .then((response) => response.json())
    .then((lapangan) => {
      if (!lapangan) {
        console.error("Data lapangan tidak ditemukan.");
        return;
      }

      // Mendapatkan jadwal
      fetch(`/api/booking/jadwal?lapanganId=${venueId}&tanggal=${date}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error(`Error fetching jadwal: ${response.statusText}`);
          }
          return response.json();
        })
        .then((jadwalList) => {
          if (!Array.isArray(jadwalList)) {
            console.error("Jadwal tidak terdefinisi atau bukan array.");
            return;
          }
          updateScheduleDisplay(jadwalList, lapangan);
        })
        .catch((error) => {
          console.error("Error saat mengambil jadwal:", error);
        });
    })
    .catch((error) => {
      console.error("Error saat mengambil detail lapangan:", error);
    });
}

function updateScheduleDisplay(jadwalList, lapangan) {
  const scheduleGrid = document.getElementById("scheduleGrid");
  scheduleGrid.innerHTML = "";

  if (jadwalList.length === 0) {
    scheduleGrid.innerHTML =
      "<p>Tidak ada jadwal tersedia untuk tanggal ini.</p>";
    return;
  }

  jadwalList.forEach((jadwal) => {
    console.log("Jadwal diterima:", jadwal); // Untuk debugging

    const slot = document.createElement("div");
    slot.className = `time-slot ${jadwal.available ? "available" : "booked"}`;
    slot.dataset.time = jadwal.waktu; // Gunakan 'waktu' sesuai respons
    slot.dataset.price = jadwal.harga;
    slot.dataset.courtId = lapangan.id;

    // Tentukan status
    let statusText = "";
    if (jadwal.available) {
      statusText = "Tersedia";
    } else {
      statusText = "Jadwal sudah terisi";
    }

    slot.innerHTML = `
      <div class="time-slot-content">
        <span class="time">${jadwal.waktu}</span>
        <span class="price">${formatCurrency(jadwal.harga)}</span>
        <span class="status">${statusText}</span>
      </div>
    `;

    if (jadwal.available) {
      slot.addEventListener("click", () =>
        selectTimeSlot(slot, jadwal.waktu, jadwal.harga, lapangan.id)
      );
    }

    scheduleGrid.appendChild(slot);
  });

  setupTimeSlotAvailability();
}

function selectTimeSlot(slotElement, waktu, price, courtId) {
  const slotIndex = selectedSlots.findIndex(
    (slot) => slot.waktu === waktu && slot.courtId === courtId
  );

  if (slotIndex > -1) {
    // Slot sudah dipilih, hapus dari selectedSlots
    selectedSlots.splice(slotIndex, 1);
    slotElement.classList.remove("selected");
  } else {
    // Pilih slot, tambahkan ke selectedSlots
    selectedSlots.push({ waktu, price: parseInt(price, 10), courtId });
    slotElement.classList.add("selected");
  }

  // Perbarui total harga
  totalPrice = selectedSlots.reduce((acc, slot) => acc + slot.price, 0);
  updateSelectionSummary();

  // Tampilkan atau sembunyikan tombol checkout
  const checkoutButton = document.getElementById("checkoutButton");
  if (selectedSlots.length > 0) {
    checkoutButton.style.display = "block";
    checkoutButton.disabled = false;
  } else {
    checkoutButton.style.display = "none";
  }
}

function updateSelectionSummary() {
  document.getElementById("selectedSlotsCount").textContent =
    selectedSlots.length;
  document.getElementById("totalPrice").textContent =
    formatCurrency(totalPrice);
}

function lanjutPembayaran() {
  if (selectedSlots.length === 0) {
    alert("Pilih jadwal terlebih dahulu");
    return;
  }

  // Fetch CSRF tokens
  const csrfTokenElement = document.querySelector('meta[name="_csrf"]');
  const csrfHeaderElement = document.querySelector('meta[name="_csrf_header"]');

  if (!csrfTokenElement || !csrfHeaderElement) {
    alert("CSRF token tidak ditemukan.");
    return;
  }

  const csrfToken = csrfTokenElement.content;
  const csrfHeader = csrfHeaderElement.content;

  // Data booking
  const activeDateItem = document.querySelector(".date-item.active");
  if (!activeDateItem) {
    alert("Tanggal tidak dipilih.");
    return;
  }
  const bookingDate = activeDateItem.dataset.date; // ISO date string 'YYYY-MM-DD'

  // Sort selected slots by time
  selectedSlots.sort((a, b) => parseInt(a.waktu) - parseInt(b.waktu));

  // Create booking data
  const bookingData = {
    tanggal: bookingDate,
    lapanganId: selectedSlots[0].courtId,
    jadwalList: selectedSlots.map((slot) => ({
      jam: parseInt(slot.waktu.split(":")[0], 10),
      harga: slot.price,
    })),
  };

  // Check if customer has an available promo
  const hasPromoElement = document.getElementById("hasPromo");
  const hasPromo = hasPromoElement ? hasPromoElement.value === "true" : false;

  // Include kodePromo only if hasPromo is true
  if (hasPromo) {
    bookingData.kodePromo = "Lapangin2024";
  }

  console.log("Sending booking data:", bookingData); // Debug log

  // Send request with CSRF token
  fetch("/api/booking/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [csrfHeader]: csrfToken,
      "X-Requested-With": "XMLHttpRequest",
    },
    credentials: "same-origin",
    body: JSON.stringify(bookingData),
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text || "HTTP error! status: " + response.status);
        });
      }
      return response.json();
    })
    .then((data) => {
      console.log("Response data:", data);
      if (data.success) {
        window.location.href = `/booking/payment/${data.bookingId}`;
      } else {
        alert(data.message || "Gagal membuat booking");
        resetSelectedSlots();
      }
    })
    .catch((error) => {
      console.error("Error creating booking:", error);
      alert(`Gagal membuat booking: ${error.message}`);
      resetSelectedSlots();
    });
}

function resetSelectedSlots() {
  selectedSlots = [];
  totalPrice = 0;
  document.getElementById("selectedSlotsCount").innerText = "0";
  document.getElementById("totalPrice").innerText = "0";
  const checkoutButton = document.getElementById("checkoutButton");
  checkoutButton.style.display = "none";
  checkoutButton.disabled = true;

  // Clear selections in the UI
  const selectedElements = document.querySelectorAll(".time-slot.selected");
  selectedElements.forEach((slot) => slot.classList.remove("selected"));
}

function setupTimeSlotAvailability() {
  const activeDateItem = document.querySelector(".date-item.active");
  if (!activeDateItem) return;
  const selectedDate = activeDateItem.dataset.date; // 'YYYY-MM-DD'

  const now = new Date();

  const timeSlots = document.querySelectorAll(".time-slot.available");
  timeSlots.forEach((slot) => {
    const slotTime = slot.getAttribute("data-time"); // "HH:mm"
    const [hour, minute] = slotTime.split(":").map(Number);
    const slotDateTime = new Date(selectedDate);
    slotDateTime.setHours(hour, minute, 0, 0);

    if (slotDateTime < now) {
      // Disable the slot
      slot.classList.remove("available");
      slot.classList.add("disabled");
      slot.classList.remove("selected");
      slot.removeEventListener("click", selectTimeSlot);
      slot.title = "Jam sudah lewat";

      // Update the status text
      const statusSpan = slot.querySelector(".status");
      if (statusSpan) {
        statusSpan.textContent = "Jam sudah lewat";
      }
    }
  });

  // Cek tombol checkout
  const remainingAvailable = document.querySelectorAll(
    ".time-slot.available"
  ).length;
  const checkoutButton = document.getElementById("checkoutButton");
  if (remainingAvailable === 0) {
    checkoutButton.style.display = "none";
    checkoutButton.disabled = true;
  } else {
    if (selectedSlots.length > 0) {
      checkoutButton.style.display = "block";
      checkoutButton.disabled = false;
    } else {
      checkoutButton.style.display = "none";
      checkoutButton.disabled = true;
    }
  }

  // Update ringkasan seleksi
  updateSelectionSummary();
}

function getContextPath() {
  const metaContextPath = document.querySelector("meta[name='context-path']");
  return metaContextPath ? metaContextPath.getAttribute("content") : "";
}

function getVenueId() {
  const pathParts = window.location.pathname.split("/");
  return pathParts[pathParts.length - 1];
}

function formatCurrency(amount) {
  return new Intl.NumberFormat("id-ID", {
    style: "currency",
    currency: "IDR",
  }).format(amount);
}

function formatDisplayDate(dateStr) {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(dateStr).toLocaleDateString("id-ID", options);
}

function fetchAndDisplayReviews() {
  const lapanganId = getVenueId();
  if (!lapanganId) {
    console.error("Lapangan ID tidak ditemukan.");
    return;
  }

  fetch(`/api/booking/reviews/${lapanganId}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "X-Requested-With": "XMLHttpRequest",
    },
    credentials: "same-origin",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Error fetching reviews: ${response.statusText}`);
      }
      return response.json();
    })
    .then((reviews) => {
      displayReviews(reviews);
    })
    .catch((error) => {
      console.error("Error fetching reviews:", error);
      document.getElementById("reviewsContainer").innerHTML =
        "<p>Gagal memuat reviews.</p>";
    });
}

function displayReviews(reviews) {
  const container = document.getElementById("reviewsContainer");
  container.innerHTML = "";

  if (reviews.length === 0) {
    container.innerHTML = "<p>Belum ada reviews untuk lapangan ini.</p>";
    return;
  }

  reviews.forEach((review) => {
    const reviewElement = document.createElement("div");
    reviewElement.className = "review-item";
    reviewElement.innerHTML = `
      <div class="review-header">
        <span class="username">${review.username}</span>
        <span class="tanggalBooking">${formatDisplayDate(
          review.tanggalReview
        )}</span>
      </div>
      <div class="review-content">
        <span class="rating">⭐ ${review.rating}</span>
        <span class="komentar">${review.komentar}</span>
      </div>
    `;
    container.appendChild(reviewElement);
  });
}
