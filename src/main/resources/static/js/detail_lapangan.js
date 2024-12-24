document.addEventListener("DOMContentLoaded", function () {
  // Initialize dates
  initializeDates();

  // Setup event listeners
  setupImagePreview();
  setupModalHandlers();

  // Handle date item clicks
  const dateItems = document.querySelectorAll(".date-item");
  dateItems.forEach((item) => {
    item.addEventListener("click", function () {
      // Remove active class dari semua item
      dateItems.forEach((di) => di.classList.remove("active"));
      // Tambahkan active class ke item yang diklik
      this.classList.add("active");

      // Update tampilan jadwal
      updateScheduleForDate(this.dataset.date);
    });
  });
});

let selectedSlots = [];
let totalPrice = 0;

function initializeDates() {
  const dateScroll = document.querySelector(".date-scroll");
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

    const dateItem = document.createElement("div");
    dateItem.className = "date-item" + (i === 0 ? " active" : "");
    const isoDate = nextDate.toISOString().split("T")[0];
    dateItem.dataset.date = isoDate;
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

  // Handler untuk menutup modal
  modal.querySelector(".close-modal").onclick = () => modal.remove();
  modal.onclick = (e) => {
    if (e.target === modal) modal.remove();
  };

  // Cegah scroll saat modal terbuka
  document.body.style.overflow = "hidden";
  modal.addEventListener("click", () => {
    document.body.style.overflow = "";
    modal.remove();
  });
}

function setupModalHandlers() {
  const modal = document.getElementById("scheduleModal");
  const closeBtn = document.querySelector(".detail-close-modal");

  if (modal && closeBtn) {
    // Tutup saat tombol X diklik
    closeBtn.onclick = () => {
      tutupModal();
    };

    // Tutup saat klik di luar modal
    window.onclick = (e) => {
      if (e.target === modal) {
        tutupModal();
      }
    };

    // Handler untuk tombol Escape
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && modal.style.display === "flex") {
        tutupModal();
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

  // Get lapangan detail
  fetch(`/booking/lapangan/${venueId}`)
    .then((response) => response.json())
    .then((lapangan) => {
      // Get jadwal
      fetch(`/booking/jadwal?lapanganId=${venueId}&tanggal=${date}`)
        .then((response) => response.json())
        .then((jadwalList) => {
          updateScheduleDisplay(jadwalList, lapangan);
        })
        .catch((error) => console.error("Error fetching jadwal:", error));
    })
    .catch((error) => console.error("Error fetching lapangan:", error));
}

function updateScheduleDisplay(jadwalList, lapangan) {
  const scheduleGrid = document.querySelector(".detail-schedule-grid");
  scheduleGrid.innerHTML = "";

  jadwalList.forEach((jadwal) => {
    const slot = document.createElement("div");
    slot.className = `time-slot ${jadwal.tersedia ? "available" : "booked"}`;
    slot.dataset.time = jadwal.jam;
    slot.dataset.price = jadwal.harga;

    slot.innerHTML = `
      <div class="time-slot-content">
        <span class="time">${jadwal.jam}</span>
        <span class="status">${
          jadwal.tersedia ? "Tersedia" : "Sudah Dipesan"
        }</span>
        <span class="price">${formatCurrency(jadwal.harga)}</span>
      </div>
    `;

    if (jadwal.tersedia) {
      slot.addEventListener("click", () =>
        selectTimeSlot(slot, jadwal.jam, jadwal.harga, lapangan.id)
      );
    }
    scheduleGrid.appendChild(slot);
  });
}

function selectTimeSlot(slotElement, time, price, courtId) {
  const isSelected = slotElement.classList.contains("selected");

  if (isSelected) {
    // Unselect slot
    slotElement.classList.remove("selected");
    selectedSlots = selectedSlots.filter((slot) => slot.time !== time);
    totalPrice -= price;
  } else {
    // Select slot
    slotElement.classList.add("selected");
    selectedSlots.push({
      time: time,
      price: price,
      courtId: courtId,
    });
    totalPrice += price;
  }

  // Update UI
  updateSelectionSummary();
}

function updateSelectionSummary() {
  const selectedSlotsCount = document.getElementById("selectedSlotsCount");
  const totalPriceElement = document.getElementById("totalPrice");
  const checkoutButton = document.getElementById("checkoutButton");

  selectedSlotsCount.textContent = selectedSlots.length;
  totalPriceElement.textContent = formatCurrency(totalPrice).replace("IDR", "");

  // Enable/disable checkout button
  checkoutButton.disabled = selectedSlots.length === 0;
}

function lanjutPembayaran() {
    if (selectedSlots.length === 0) {
        alert("Pilih jadwal terlebih dahulu");
        return;
    }

    // Ambil token CSRF
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    // Data booking
    const bookingData = {
        lapanganId: selectedSlots[0].courtId,
        jadwalList: selectedSlots.map(slot => ({
            waktu: slot.time,
            harga: slot.price
        }))
    };

    console.log("Mengirim data booking:", bookingData); // Debug log

    // Kirim request dengan CSRF token
    fetch('/booking/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken,
            'X-Requested-With': 'XMLHttpRequest'
        },
        credentials: 'same-origin',
        body: JSON.stringify(bookingData)
    })
    .then(response => {
        console.log("Status response:", response.status); // Debug log
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'HTTP error! status: ' + response.status);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log("Response data:", data); // Debug log
        if (data.success) {
            window.location.href = `/booking/payment/${data.bookingId}`;
        } else {
            alert(data.message || "Gagal membuat booking");
        }
    })
    .catch(error => {
        console.error("Error detail:", error); // Debug log
        alert(`Gagal membuat booking: ${error.message}`);
    });
}

// Add style for selected slots
const style = document.createElement("style");
style.textContent = `
  .time-slot.selected {
    background-color: #4CAF50;
    color: white;
  }
  .time-slot.selected .time-slot-content {
    color: white;
  }
`;
document.head.appendChild(style);

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
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount);
}

function formatDisplayDate(dateStr) {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(dateStr).toLocaleDateString("id-ID", options);
}
