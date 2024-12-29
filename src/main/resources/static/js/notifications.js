// Fungsi untuk menghitung waktu pengingat
function setReminder(booking) {
  console.log("Setting reminder for booking:", booking); // Debugging
  const now = new Date(); // Waktu sekarang
  const waktuMulai = new Date(booking.waktuMulai); // Waktu booking dimulai
  const pengingatWaktu = new Date(waktuMulai.getTime() - 60 * 60 * 1000); // 1 jam sebelum

  // Jika pengingat waktu belum terlewat
  if (pengingatWaktu > now) {
    const timeToReminder = pengingatWaktu - now; // Selisih waktu dalam milidetik
    setTimeout(() => {
      tampilkanNotifikasi(booking);
    }, timeToReminder);
  }
}

// Fungsi untuk menampilkan notifikasi
function tampilkanNotifikasi(booking) {
  console.log("Displaying notification for booking:", booking); // Debugging
  const notificationList = document.getElementById("notification-list");
  const notificationItem = document.createElement("div");
  notificationItem.className = "notification-item";
  notificationItem.innerHTML = `
    <div class="icon">⏰</div>
    <div class="notification-content">
      <h4>Pengingat: Lapangan ${booking.lapangan}</h4>
      <p>Jadwal Anda dimulai dalam 1 jam (${new Date(booking.waktuMulai).toLocaleTimeString()}). Siapkan perlengkapan Anda!</p>
    </div>
  `;
  notificationList.appendChild(notificationItem);
}

// Inisialisasi pengingat untuk semua booking
document.addEventListener("DOMContentLoaded", () => {
  // Hapus bagian yang menggunakan variabel notifications
  // Langsung jalankan updateCountdowns
  updateCountdowns();
  // Update setiap detik
  setInterval(updateCountdowns, 1000);
});

function updateCountdowns() {
  document.querySelectorAll('.countdown').forEach(el => {
    const bookingTimeStr = el.getAttribute('data-time');
    if (!bookingTimeStr) {
      console.log('No data-time attribute found');
      return;
    }
    
    const bookingTime = new Date(bookingTimeStr).getTime();
    const now = new Date().getTime();
    const distance = bookingTime - now;

    if (distance > 0) {
      const days = Math.floor(distance / (1000 * 60 * 60 * 24));
      const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000);

      let timeStr = '';
      if (days > 0) timeStr += `${days}h `;
      if (hours > 0) timeStr += `${hours}j `;
      timeStr += `${minutes}m ${seconds}d`;

      el.innerHTML = `Jadwal Booking dalam: ${timeStr}`;

      // Tambah highlight jika kurang dari 1 jam
      if (hours < 1 && days === 0) {
        el.classList.add('urgent');
      }
    } else {
      el.innerHTML = 'Jadwal telah dimulai';
      el.classList.add('expired');
    }
  });
}
