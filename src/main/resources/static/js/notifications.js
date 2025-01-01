// notifications.js

document.addEventListener("DOMContentLoaded", () => {
  fetchBookings();
  // Jalankan updateCountdowns setiap detik
  setInterval(updateCountdowns, 1000);
});

function fetchBookings() {
  const today = new Date().toISOString().split("T")[0]; // Format 'YYYY-MM-DD'
  fetch(`/api/booking/bookings?tanggal=${today}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include", // Sertakan kredensial jika diperlukan
  })
    .then((response) => response.json())
    .then((data) => {
      if (data && data.length > 0) {
        data.forEach((booking) => {
          setReminder(booking);
        });
      } else {
        console.log("Tidak ada notifikasi saat ini.");
      }
    })
    .catch((error) => {
      console.error("Error fetching bookings:", error);
    });
}

// Fungsi untuk menghitung waktu pengingat
function setReminder(booking) {
  console.log("Setting reminder for booking:", booking); // Debugging
  const now = new Date(); // Waktu sekarang

  // Kombinasikan tanggal dari bookingDate dengan jamMulai
  const bookingDateParts = booking.bookingDate.split("T")[0].split("-");
  const year = parseInt(bookingDateParts[0], 10);
  const month = parseInt(bookingDateParts[1], 10) - 1; // Bulan 0-indexed
  const day = parseInt(bookingDateParts[2], 10);
  const jamMulai = booking.jamMulai;

  const bookingDateTime = new Date(year, month, day, jamMulai, 0, 0, 0);
  const pengingatWaktu = new Date(bookingDateTime.getTime() - 60 * 60 * 1000); // 1 jam sebelum

  console.log("Booking DateTime:", bookingDateTime);
  console.log("Pengingat Waktu:", pengingatWaktu);
  console.log("Waktu Sekarang:", now);

  // Jika pengingat waktu belum terlewat
  if (pengingatWaktu > now) {
    const timeToReminder = pengingatWaktu - now; // Selisih waktu dalam milidetik
    console.log("Time to Reminder (ms):", timeToReminder);
    setTimeout(() => {
      tampilkanNotifikasi(booking);
    }, timeToReminder);
  } else {
    console.log("Pengingat waktu sudah terlewat untuk booking ID:", booking.id);
    // Tampilkan langsung countdown jika pengingat sudah terlewat
    tampilkanNotifikasi(booking);
  }
}

// Fungsi untuk menampilkan notifikasi
function tampilkanNotifikasi(booking) {
  console.log("Displaying notification for booking:", booking); // Debugging
  const notificationList = document.getElementById("notification-list");

  if (!notificationList) {
    console.error("Elemen dengan ID 'notification-list' tidak ditemukan.");
    return;
  }

  // Pastikan lapangan dan namaLapangan ada
  const lapanganNama =
    booking.lapangan && booking.lapangan.namaLapangan
      ? booking.lapangan.namaLapangan
      : "Lapangan";

  const notificationItem = document.createElement("div");
  notificationItem.className = "notification-item";
  notificationItem.innerHTML = `
    <div class="icon">⏰</div>
    <div class="notification-content">
      <h4>Pengingat: ${lapanganNama}</h4>
      <p>Jadwal Anda dimulai dalam 1 jam (${formatJam(
        booking.jamMulai
      )}). Siapkan perlengkapan Anda!</p>
      <div class="countdown" data-date="${
        booking.bookingDate.split("T")[0]
      }" data-jammulai="${booking.jamMulai}"></div>
    </div>
  `;

  notificationList.appendChild(notificationItem);
}

// Fungsi helper untuk memformat jam
function formatJam(jam) {
  return `${jam}:00:00`;
}

// Fungsi untuk memperbarui countdown
function updateCountdowns() {
  document.querySelectorAll(".countdown").forEach((el) => {
    const bookingDateStr = el.getAttribute("data-date");
    const jamMulai = parseInt(el.getAttribute("data-jammulai"), 10);

    if (!bookingDateStr || isNaN(jamMulai)) {
      console.log("Data tidak lengkap untuk countdown");
      return;
    }

    // Kombinasikan tanggal dengan jamMulai
    const bookingDateParts = bookingDateStr.split("-");
    const year = parseInt(bookingDateParts[0], 10);
    const month = parseInt(bookingDateParts[1], 10) - 1; // Bulan 0-indexed
    const day = parseInt(bookingDateParts[2], 10);

    const bookingDateTime = new Date(year, month, day, jamMulai, 0, 0, 0);
    const now = new Date();
    const difference = bookingDateTime - now;

    if (difference > 0) {
      const days = Math.floor(difference / (1000 * 60 * 60 * 24));
      const hours = Math.floor(
        (difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
      );
      const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((difference % (1000 * 60)) / 1000);

      let timeStr = "";
      if (days > 0) timeStr += `${days}h `;
      if (hours > 0) timeStr += `${hours}j `;
      timeStr += `${minutes}m ${seconds}s`;

      el.innerHTML = `Jadwal Booking dalam: ${timeStr}`;

      // Tambah highlight jika kurang dari 1 jam
      if (hours < 1 && days === 0) {
        el.classList.add("urgent");
      } else {
        el.classList.remove("urgent");
      }
    } else {
      el.innerHTML = "Jadwal telah dimulai";
      el.classList.add("expired");
    }
  });
}
